package function;

import java.io.File;

import main.CommonVar;

public class CountFile {
	
	private CountFile(){};
	
	public int countLeftFile(String serviceId){
		String folderDetect = CommonVar.folderDetect + serviceId;
		int n = -1;
		try{
			File Files = new File(folderDetect);
			n = Files.list().length;
		}catch(Exception e){
			return n;
		}
		return n;
	}
}
