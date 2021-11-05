package function;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;

public class TestEncoder {

	public static void main(String[] args) throws IOException {
		
		
		for(int i = 5; i <= 1400; i+=5){
			Date now = (Calendar.getInstance()).getTime() ;
			Timestamp ts = new Timestamp(now.getTime());
			String ratioImages = "1-"+i;
			ZipUtils.ZipData(ratioImages, "ABC");
			//System.out.println("Finish zipping "+ ratioImages);
			now = (Calendar.getInstance()).getTime();
			long time = (new Timestamp(now.getTime())).getTime() - ts.getTime();
			System.out.println("Time zipping " + ratioImages + " is " + time);
		}
		/*
		 * 
		
		Date now = (Calendar.getInstance()).getTime() ;
		Timestamp ts = new Timestamp(now.getTime());
		for(int i = 5; i <= 1400; i+=5){
			Date now = (Calendar.getInstance()).getTime() ;
			Timestamp ts = new Timestamp(now.getTime());
			String encodedString = "";
			String dataName = "image1-"+i+"_ABC";
			try {		
				byte[] fileContent = FileUtils.readFileToByteArray(new File(CommonVar.DATADIR + "/" + dataName+".zip"));
				encodedString = Base64.getEncoder().encodeToString(fileContent);
				now = (Calendar.getInstance()).getTime();
				long time = (new Timestamp(now.getTime())).getTime() - ts.getTime();
				System.out.println("Time encoding " + dataName + " is " + time);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		 */
		
	}

}
