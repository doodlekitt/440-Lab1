import java.io.*;

public class ProcessManager {

    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket PMSocket = null;

    private DataInputStream is = null;
    private DataOutputStream os = null;

    public static void main (String[] args) {
	System.out.println("Available Commands");

	System.out.println("Create New Process: new <Class> <nickname>\n Move Process: migrate <nickname> <target>\n Server Client Query: query\n Exit Program: quit\n"); 
   	
	BufferedReader br =new BufferedReader(new InputStreamReader(System.in));

	// NEED TO PUT PORTNUMBER IDK 

	// Error checking here?
	PMSocket = new Socket("localhost", portNumber);

	String command = null;

	// Needs to read in commands and execute until it is told to die
	// THE EXECUTE COMMAND SHOULD MAKE AND START THE THING
	// WHERE DO WE STORE THEM THOUGH AND IF WE STORE WE SHOULD
	// REMOVE WHEN THEY ARE DONE

	// Basically, the PM has to check both the buffered reader for input 
	// the cmd line and check the socket input stream for messages 
	// to act on from the server

	// Also when we quit, we should tell the server

	try {
	    is = new DataInputStream(PMSocket.getInputStream());
	    os = new DataOutputStream(PMSocket.getOutputStream());

	    command = br.readLine();
	    while(command != null){
		if(command.startsWith("quit"))
		{
		    break;
		}
		else{
		    execute(command);
		}
	    }
	} catch (IOException e) {
	    System.out.println(e);
	}
	
	// Clean up is last
	os.close();
	is.close();
	br.close();
	PMSocket.close();

    }

    public void execute(String command) {
	if(command.startsWith("new")){

	} else if (command.startsWith("migrate")){

	} else if (command.startsWith("query")){

	} else {
	     System.out.println("Invalid Command");
	}
    }

}
