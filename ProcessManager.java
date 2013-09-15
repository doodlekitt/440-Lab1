import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Hashtable;
import java.lang.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
        BufferedReader br =
            new BufferedReader(new InputStreamReader(System.in));
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

        } else if(command.startsWith("new")){
            if (args.length < 2) {
                // TODO: Add error message
                return;
            }

            port = args[1];

	    // check if valid class
            Class<?> c = null;
            try {
                c = Class.forName(args[2]);
            } catch(ClassNotFoundException e) {
                System.out.println("Invalid Class");
                return;
            }

            // check if implements MigratableProcess
            if (!MigratableProcess.class.isAssignableFrom(c)) {
		System.out.println("Class Must Implement MigratableProcess");
		return;
	    }

            MigratableProcess mp = null;
            String[] process_args = Arrays.copyOfRange(args, 3, args.length);
            try {
                mp = (MigratableProcess)c.getConstructor(String[].class)
                     .newInstance((Object[])process_args);
            } catch(NoSuchMethodException | InstantiationException |
                    IllegalAccessException | InvocationTargetException e) {
                System.out.println(e);
                return;
            }

            send = new Package.PMPackage(Package.Command.NEW, mp);

	} else if (command.startsWith("query")){

	} else {
	     System.out.println("Invalid Command");
	}

        if(port != "" && send != null) {
            sendPackage(port, send);
        }

        return;
    }

    private static void sendPackage(String portString, Package.PMPackage send) {
        ObjectOutputStream os = null;
        ObjectInputStream is = null;
        Package.SlavePackage recieve = null;

        int port = Integer.valueOf(portString).intValue();
        if(!clients.containsKey(port)) {
            System.out.println("Invalid port number.");
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
