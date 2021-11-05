package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonVar {
	CommonVar(){};
	
  	
	public static String WORKERID = "2";
	
	
	public static final String ORIGINATOR="admin:admin";
	public static final String CSEPROTOCOL="http";
	public static final String WORKERCSEIP = "127.0.0.1";
	public static final int WORKERCSEPORT = 8080 + Integer.parseInt(WORKERID);
	public static final String WORKERCSEID = "worker-" + WORKERID + "-id";
	public static final String WORKERCSENAME = "worker-" + WORKERID;
	
	public static final String AENAME = "WorkerAE";
	public static final String AEPROTOCOL="http";
	public static final String AEIP = "127.0.0.1";
	public static final int AEPORT = 1500 + Integer.parseInt(WORKERID) ;	
	public static final String AESUB="WorkerSub";
	
	public static final String WORKERCSEPOA = CSEPROTOCOL+"://"+WORKERCSEIP+":"+WORKERCSEPORT;
	public static final String APPPOA = AEPROTOCOL+"://"+AEIP+":"+AEPORT;
	public static final String NU = "/"+WORKERCSEID+"/"+WORKERCSENAME+"/"+AENAME;
 
	public static final String[] CONTAINER = {"COMMAND", "MONITOR", "SERVICE", "RESULT", "DATA"};
	public static final String[] SUBCONTAINER = {"COMMAND"};
	
	
	public static final String DATADIR = "D:\\2021\\VFC\\data\\w2\\cut_image";
	public static final String folderDetect = "home/pi/data/cut_image/";
	
	public void getConfig() {
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = getClass().getClassLoader().getResourceAsStream("config.properties");

			// load a properties file
			prop.load(input);
			WORKERID = prop.getProperty("workerid");
//	        // get the property value and print it out
//	        System.out.println(prop.getProperty("database"));
//	        System.out.println(prop.getProperty("dbuser"));
//	        System.out.println(prop.getProperty("dbpassword"));
		//Map<String, String> map = new HashMap<String, String>();

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	
	
}
