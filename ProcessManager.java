import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Arrays.*;
import java.util.Hashtable;
import java.lang.*;

public class ProcessManager {

    private static int port = 0;
    private static ServerSocket server = null;

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
        String port = "";
        Package.PMPackage send = null;

        String[] args = command.split(" ");

        if(command.startsWith("list")) {
            list_slaves();
        } else if(command.startsWith("kill")) {
            if(args.length < 2) {
                // TODO: Add error message
                return;
            }

            port = args[1];
            send = new Package.PMPackage(Package.Command.KILL);
        }

        if(port != "" && send != null) {
            sendPackage(port, send);
        }

        /*
	if(command.startsWith("new")){
	    String[] args = command.split(" ");
	    boolean migratable = false;

            if (args.length < 2) {
                return;
            }


            int port = Integer.valueOf(args[1]).intValue();
            if(!clients.containsKey(port)) {
                return;
            }

            Socket slave = clients[port];
            os = new ObjectOutputStream(slave.getOutputStream());

	    // check if implements MigratableProcess
	    for(Class<?> i : (Class.forName(args[2])).getInterfaces()){
		if(i.isInstance(MigratableProcess))
		    migratable = true;
	    }

	    // if not, break
	    if(!migratable){
		System.out.println("Invalid Class");
		return;
	    }

	    //else, create new process requested
	    // The arguments are a subarray of arg
	    // I dunno how to do this for String[]

	    String[] process_args = Arrays.copyOfRange(args, 3, args.length);
	    Constructor<?> constructor =
                (Class.forName(arg[2])).getConstructor(String[].class);


            String[] process_args = Arrays.copyOfRange(args, 3, args.length);
            Constructor<?> constructor =
            // if you send it, then you should be able to make a new one by
	    // constructor.newInstance(process_args)
            Package.PMPackage send = new PMPackage(Package.Command.NEW,
                constructor, process_args);

            is = new objectInputStream(slave.getInputStream());
            try {
                recieve = (Package.PMPackage)is.readObject();
            }
            catch (ClassNotFoundException e) {
                System.out.println(e);
            }


	} else if (command.startsWith("list")){
            list_slaves();
	} else if (command.startsWith("query")){

	} else {
	     System.out.println("Invalid Command");
	}
        */
        return;
    }

    private static void sendPackage(String portString, Package.PMPackage send) {
        ObjectOutputStream os = null;
        ObjectInputStream is = null;
        Package.SlavePackage recieve = null;

        int port = Integer.valueOf(portString).intValue();
        if(!clients.containsKey(port)) {
            // TODO: Add error message
            return;
        }
        Socket slave = clients.get(port); 

        try {
            os = new ObjectOutputStream(slave.getOutputStream());
            is = new ObjectInputStream(slave.getInputStream());

            os.writeObject(send);

            try {
                recieve = (Package.SlavePackage)is.readObject();
            }
            catch (ClassNotFoundException e) {
                System.out.println(e);
            }

            is.close();
            os.close();
        } catch (IOException e) {
            System.out.println(e);
        }

        processReply(recieve);

        if (recieve != null) {
            // Remove this statement
            System.out.println(recieve.success());
            if (recieve.command() == Package.Command.KILL &&
                recieve.success() == true) {
                clients.remove(port);
            }
        }
    }

    private static void processReply(Package.SlavePackage reply) {
        if (reply == null) {
            return;
        }
        switch (reply.command()) {
            default: break;
        }
    }

    private static void list_slaves() {
        System.out.println("Printing slaves:");
        for (Integer key : clients.keySet()) {
            System.out.println("Slave " + key + ": " + clients.get(key));
        }
        return;
    }
}
