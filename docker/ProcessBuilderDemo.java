import java.io.*;
import java.lang.*;
import java.util.*;
class ProcessBuilderDemo {
    public static void main(String[] arg) throws IOException
    {
        // creating list of commands
        List<String> commands = new ArrayList<String>();
		//String command_p = "python detect_n_send_2.py http://192.168.1.136:8181 worker-1-id worker-1 36 e45a83a6-0159-4e18-84f2-fe0da3c85b28 1 300";
        commands.add("python"); // command
        commands.add("/home/pi/data/docker/detect_n_send_2.py"); // command
	    commands.add("http://192.168.1.136:8181");
        commands.add("worker-1-id");
		commands.add("worker-1");
		commands.add("36");
		commands.add("e45a83a6-0159-4e18-84f2-fe0da3c85b28");
		commands.add("1");
		commands.add("300");
		
		
        // creating the process
        ProcessBuilder pb = new ProcessBuilder(commands);
	//	pb.directory(new File("/home/pi/data/docker"));
		
		
		System.out.println(commands);
 
        // startinf the process
        Process process = pb.start();
 
        // for reading the output from stream
        BufferedReader stdInput
            = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        String s = null;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
    }
}