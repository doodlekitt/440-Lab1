import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Hashtable;
import java.lang.*;

public class ProcessManager {


    private static int port = 0;
    private static ServerSocket server = null;
    private static ObjectInputStream is = null;
    private static ObjectOutputStream os = null;

    private static Hashtable<Integer, Socket> clients =
        new Hashtable<Integer, Socket>();

    public static void main (String[] args) throws IOException {

        // Parse args
        if (args.length < 1) {
            System.out.println("Expecting command of the form:");
            System.out.println("ProcessManager <port>");
            System.out.println("Where <port> is the port for the new ProcessManager.");
            return;
        }
        
        // Create ServerSocket
        port = Integer.valueOf(args[0]).intValue();
        server = new ServerSocket(port);

        // Run server to listen to slaves
        AcceptRunnable accept = new AcceptRunnable();
        Thread acceptThread = new Thread(accept);
        acceptThread.start();

        // Listen to command line
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String command = null;
	try {
	    is = new ObjectInputStream(PMSocket.getInputStream());
	    os = new ObjectOutputStream(PMSocket.getOutputStream());

	    while (true){
                command = br.readLine();

		if(command.startsWith("quit"))
		{
		    break;
		}
		else{
		    // execute should probably also take the os so that you can
		    // send appropriate messages based on the command
		    execute(command);
		}

		//probably here we add listening on the socket for stuff
	    }
	} catch (IOException e) {
	    System.out.println(e);
	}

	// Clean up is last
        accept.stop();
	try {
            os.close();
            is.close();
            server.close();
            br.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static class AcceptRunnable implements Runnable {

        private static boolean flag = true;

        public void stop() {
            flag = false;
        }

        public void run() {
            Socket clientSocket = null;
            while(flag){
                try{
                    clientSocket = server.accept();
                    // Read from socket
                    clients.put(clientSocket.getPort(), clientSocket);
                } catch (IOException e) {
                    // Don't print exception when stopping thread
                    if(flag) {
                        System.out.println(e);
                    }
                }
            }
        }

        public static void main(String ags[]) {
            (new Thread(new AcceptRunnable())).start();
        }
    }

    public static void execute(String command) {
	if(command.startsWith("new")){
	    String[] arg = command.split(" ");
	    boolean migratable = false;

	    // check if implements MigratableProcess
	    for(Class<?> i : (Class.forName(arg[1])).getInterfaces()){
		if(i.isInstance(MigratableProcess))
		    migratable = true;
	    }

	    // if not, break
	    if(!migratable){
		System.out.println("Invalid Class");
		break;
	    }

	    //else, create new process requested
	    // The arguments are a subarray of arg
	    // I dunno how to do this for String[]

	    String[] process_args = Arrays.copyOfRange(arg, 2, );
	    Constructor<?> constructor = (Class.forName(arg[1])).getConstructor(String[].class);
	 
	    // if you send it, then you should be able to make a new one by
	    // constructor.newInstance(process_args) 


	} else if (command.startsWith("list")){
            list_slaves();
	} else if (command.startsWith("query")){

	} else {
	     System.out.println("Invalid Command");
	}
        return;
    }

    private static void list_slaves() {
        System.out.println("Printing slaves:");
        for (Integer key : clients.keySet()) {
            System.out.println("Slave " + key + ": " + clients.get(key));
        }
        return;
    } 
}
