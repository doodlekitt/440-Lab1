import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Hashtable;

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
	    // is = new ObjectInputStream(PMSocket.getInputStream());
	    // os = new ObjectOutputStream(PMSocket.getOutputStream());

	    while (true){
                command = br.readLine();

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
        accept.stop();
	try {
            // os.close();
            // is.close();
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
