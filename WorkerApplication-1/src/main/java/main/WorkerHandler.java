package main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.management.OperatingSystemMXBean;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import function.CWorkload;
import function.PullData;
import function.ZipUtils;
import http.NotifyMessage;
import http.RestHttpClient;

public class WorkerHandler implements HttpHandler {

	private static final Logger LOGGER = LogManager.getLogger(WorkerHandler.class);

	private static OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	static Calendar cal;
	private static Container[] cnt = new Container[1];

	public WorkerHandler(Container[] cnt) {
		// TODO Auto-generated constructor stub
		this.cnt = cnt;
	}

	@SuppressWarnings("deprecation")
	public void handle(HttpExchange httpExchange) {
		System.out.println("Event Recieved!");

		try {
			NotifyMessage notifyMessage = getNotifyMessage(httpExchange);
			JSONObject command = notifyMessage.getCommand();
			int commandIdNumber = command.getInt("COMMANDID");
			int commandCode = command.getInt("COMMANDCODE");

			/*
			 * Period checkup
			 */
			if (commandCode == 1) {
				sendResourceStats(commandIdNumber, commandCode);
			}

			/*
			 * Deploy container service
			 */
			if (commandCode == 2) {
				// LOGGER.info("Received service deployment command");
				// Date now = (Calendar.getInstance()).getTime();
				// Timestamp tsNow = new Timestamp(now.getTime()); //t1
				// long deltaT1 = tsNow.getTime() -
				// Timestamp.valueOf(headerTimeStamp).getTime(); //header = t0
				Date now = (Calendar.getInstance()).getTime();
				Timestamp ts = new Timestamp(now.getTime());
				String dataSource = command.getString("DTSOURCE");
				if (dataSource.equals(CommonVar.WORKERCSEID + "/" + CommonVar.WORKERCSENAME)) {
					// DockerController.Deploy
					System.out.println("DataSource is at this worker, skip pulling data");
					ZipUtils.UnzipData(command.getInt("STARTIMAGE") + "-" + command.getInt("ENDIMAGE"),
							command.getString("SERVICEID"));
					now = (Calendar.getInstance()).getTime();
					LOGGER.info("Unzip data time for service {}: {}", command.getString("SERVICEID"),
							(new Timestamp(now.getTime())).getTime() - ts.getTime());

					DockerController.deployService(CommonVar.WORKERCSEPOA, // result destination
							CommonVar.WORKERCSEID, CommonVar.WORKERCSENAME, command.getString("SERVICEID"),
							command.getString("SERVICE"), commandIdNumber, command.getInt("STARTIMAGE"),
							command.getInt("ENDIMAGE")); // deltaT1
				} else { // if datasource is at another worker, then pull first and deploy after that

					String ratioImages = command.getInt("STARTIMAGE") + "-" + command.getInt("ENDIMAGE");

					PullData.usingDiscovery(command.getString("SERVICEID"), dataSource, ratioImages);
					now = (Calendar.getInstance()).getTime();
					Timestamp ts2 = new Timestamp(now.getTime());
					LOGGER.info("Pull data time for service {}: {}", command.getString("SERVICEID"),
							ts2.getTime() - ts.getTime());
					// unzip data file to folder name = serviceId

					ZipUtils.UnzipData(ratioImages, command.getString("SERVICEID"));
					now = (Calendar.getInstance()).getTime();
					LOGGER.info("Unzip data time for service {}: {}", command.getString("SERVICEID"),
							(new Timestamp(now.getTime())).getTime() - ts2.getTime());

					// LOGGER.INFO("Sending command to container");
					DockerController.deployService(CommonVar.WORKERCSEPOA, // result destination
							command.getString("TARGETID"), command.getString("TARGETNAME"),
							command.getString("SERVICEID"), command.getString("SERVICE"), commandIdNumber,
							command.getInt("STARTIMAGE"), command.getInt("ENDIMAGE"));
					// deltaT1, deltaT2);
				}
			}

			// Zip file command
			if (commandCode == 3) { // zip file
				// get Zip image ratio
				int zipState = 1; // if Zipping success => send this state
				Date now = (Calendar.getInstance()).getTime();
				Timestamp tsZip = new Timestamp(now.getTime());
				JSONArray arr = new JSONArray(command.getString("ZIP"));

				String serviceID = command.getString("SERVICEID");
				for (int index = 0; index < arr.length(); index++) {
					try {
						// System.out.println(" ratioImage = " + arr.getString(index));
						ZipUtils.ZipData(arr.getString(index), serviceID);

					} catch (Exception e) {
						System.out.println("Something Wrong with data IO");
						e.printStackTrace();
						zipState = 0; // if Zipping doesnt success
					}
				}

				now = (Calendar.getInstance()).getTime();
				LOGGER.info("Zip and push data time for service {}: {}", serviceID,
						(new Timestamp(now.getTime())).getTime() - tsZip.getTime());

				sendResponeZipDone(serviceID, commandIdNumber, zipState);

			}

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private NotifyMessage getNotifyMessage(HttpExchange httpExchange) throws IOException {
		NotifyMessage mess = new NotifyMessage();

		InputStream in = httpExchange.getRequestBody();
		String requestBody = "";
		int i;
		char c;
		while ((i = in.read()) != -1) {
			c = (char) i;
			requestBody = (String) (requestBody + c);
		}

		// System.out.println(requestBody);
		// Headers inHeader = httpExchange.getRequestHeaders();
		// String headerTimeStamp = inHeader.getFirst("X-M2M-OT");
		// System.out.println("HeaderOT: "+headerTimeStamp);

		JSONObject json = new JSONObject(requestBody);

		String responseBody = "";
		byte[] out = responseBody.getBytes("UTF-8");
		httpExchange.sendResponseHeaders(200, out.length);
		OutputStream os = httpExchange.getResponseBody();
		os.write(out);
		os.close();

		// process request from NOTIFY scheme
		if (json.getJSONObject("m2m:sgn").has("m2m:vrq")) {
			if (json.getJSONObject("m2m:sgn").getBoolean("m2m:vrq")) {
				mess.setType("Confirm Subcription");
				// System.out.println("Confirm subscription");
			}
		} else {
			JSONObject rep = json.getJSONObject("m2m:sgn").getJSONObject("m2m:nev").getJSONObject("m2m:rep")
					.getJSONObject("m2m:cin");
			int ty = rep.getInt("ty");
			mess.setTy(ty);
			String pi = rep.getString("pi");
			mess.setPi(pi);
			// System.out.println("Resource type: "+ty);
			for (int j = 0; j < cnt.length; j++) {
				if (pi.equals(cnt[j].getContainerID())) {

					if (cnt[j].getContainerName().equals("COMMAND")) {
						JSONArray content = new JSONArray(rep.getString("con"));
						JSONObject command = content.getJSONObject(0).getJSONObject("COMMAND"); // Command always at
																								// index 0
						mess.setCommand(command);
						// int commandIdNumber = command.getInt("COMMANDID");
						// int commandCode = command.getInt("COMMANDCODE");
					}
				}
			}
		}
		return mess;
	}

	private void sendResponeZipDone(String serviceID, int commandIdNumber, int zipState) {
		// send back zip data completion mess
		List<JSONObject> result = new ArrayList<JSONObject>();
		result.add((new JSONObject()).put("SERVICEID", serviceID)); // 0
		result.add((new JSONObject()).put("SERVICE", "ZIP")); // 1
		result.add((new JSONObject()).put("COMMANDID", commandIdNumber)); // 2
		result.add((new JSONObject()).put("ZIPSTATE", zipState)); // 3

		JSONObject obj = new JSONObject();
		cal = Calendar.getInstance();
		obj.put("rn", "zip_result" + commandIdNumber + "_" + serviceID);
		obj.put("cnf", "application/text");
		obj.put("con", result.toString());
		JSONObject resource = new JSONObject();
		resource.put("m2m:cin", obj);
		RestHttpClient.post(CommonVar.ORIGINATOR,
				CommonVar.WORKERCSEPOA + "/~/" + CommonVar.WORKERCSEID + "/" + CommonVar.WORKERCSENAME + "/RESULT",
				resource.toString(), 4);

	}

	private void sendResourceStats(int commandIdNumber, int commandCode) {
		List<JSONObject> result = new ArrayList<JSONObject>();
		result.add((new JSONObject()).put("COMMANDID", commandIdNumber)); // 0
		result.add((new JSONObject()).put("COMMANDCODE", commandCode)); // 1

		List<JSONObject> resourceInfo = new ArrayList<JSONObject>();
		resourceInfo.add((new JSONObject()).put("CPU", osBean.getSystemCpuLoad() * 100));
		resourceInfo.add(
				(new JSONObject()).put("RAM", (osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize())
						/ osBean.getTotalPhysicalMemorySize())); // (Total - Free / Total)
		result.add((new JSONObject()).put("RESOURCEINFO", resourceInfo)); // 2
		result.add((new JSONObject()).put("CWORKLOAD", new CWorkload().findCurrentWorkload())); // 3
		Date now = (Calendar.getInstance()).getTime();
		Timestamp ts = new Timestamp(now.getTime());
		result.add((new JSONObject()).put("TS", ts.toString())); // 4 --> track time take to send monitor mess from this
																	// worker to manager
		JSONObject obj = new JSONObject();
		cal = Calendar.getInstance();
		obj.put("rn", "monitor_result_" + commandIdNumber + "_" + sdf.format(cal.getTime()));
		obj.put("cnf", "application/text");
		obj.put("con", result.toString());
		JSONObject resource = new JSONObject();
		resource.put("m2m:cin", obj);
		RestHttpClient.post(CommonVar.ORIGINATOR,
				CommonVar.WORKERCSEPOA + "/~/" + CommonVar.WORKERCSEID + "/" + CommonVar.WORKERCSENAME + "/MONITOR",
				resource.toString(), 4);
	}

}
