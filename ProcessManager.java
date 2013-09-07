import java.io.*;

public class ProcessManager {

    public static void main (String[] args) {
	System.out.println("Available Commands");

	System.out.println("Create New Process: new <Class> <nickname>\n Move Process: migrate <nickname> <target>\n Server Client Query: query\n Exit Program: quit\n"); 
   	
	BufferedReader br =new BufferedReader(new InputStreamReader(System.in));

	String command = null;

	\\ Needs to read in commands and execute until it is told to die

	while(command != "quit"){
	    command = br.readLine();
	    execute(command);
	} catch (IOException e) {
	    System.out.println(e);
	}
    }

    public void execute(String command) {

    }

}
