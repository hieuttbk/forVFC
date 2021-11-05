package function;

import java.io.File;

import main.CommonVar;

public class CWorkload {
	int currentWorkload;
	public CWorkload() {}

	public CWorkload(int currentWorkload) {
		super();
		this.currentWorkload = currentWorkload;
	}


	public void setCurrentWorkload(int currentWorkload) {
		this.currentWorkload = currentWorkload;
	}

	public int getCurrentWorkload() {
		
		return currentWorkload;
	}

	public int findCurrentWorkload(){
		int currentWorkload = 0;
		File folder = new File(CommonVar.DATADIR);
		File[] listOfFile = folder.listFiles();
		if(listOfFile.length == 0){
			return 0;
		}
		for(File file : listOfFile){
			if(file.isDirectory()){
				//System.out.println(file.getAbsolutePath());
				folder = new File(file.getAbsolutePath());
				currentWorkload += folder.listFiles().length;
			}
		}
		
		return currentWorkload;
	}
}
