package WorkerApplication.Main;

public class CommonVar {
	private CommonVar(){}
	
	public static final String ORIGINATOR="admin:admin";
	public static final String CSEPROTOCOL="http";
	public static final String WORKERCSEIP = "127.0.0.1";
	public static final int WORKERCSEPORT = 8182;
	public static final String WORKERCSEID = "worker-2-id";
	public static final String WORKERCSENAME = "worker-2";
	
	public static final String AENAME = "WorkerAE";
	public static final String AEPROTOCOL="http";
	public static final String AEIP = "127.0.0.1";
	public static final int AEPORT = 1501;	
	public static final String AESUB="WorkerSub";
	
	public static final String WORKERCSEPOA = CSEPROTOCOL+"://"+WORKERCSEIP+":"+WORKERCSEPORT;
	public static final String APPPOA = AEPROTOCOL+"://"+AEIP+":"+AEPORT;
	public static final String NU = "/"+WORKERCSEID+"/"+WORKERCSENAME+"/"+AENAME;
 
	public static final String[] CONTAINER = {"COMMAND", "MONITOR", "SERVICE", "RESULT", "DATA"};
	public static final String[] SUBCONTAINER = {"COMMAND"};
	
	
	public static final String DATADIR = "D:\\2021\\VFC\\data\\w2\\cut_image";
}
