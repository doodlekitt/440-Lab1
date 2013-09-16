import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ProcessManager {

    private static int port = 0;
    private static ServerSocket server = null;

    public static class ObjectIO {
        private Socket socket;
        private ObjectInputStream is;
        private ObjectOutputStream os;

        public ObjectIO (Socket socket, ObjectInputStream is,
                              ObjectOutputStream os) {
            this.socket = socket;
            this.is = is;
            this.os = os;
        }

        public Socket socket() {
            return this.socket;
        }
        public ObjectInputStream is() {
            return this.is;
        }
        public ObjectOutputStream os() {
            return this.os;
        }
    }

    private static Hashtable<Integer, ObjectIO> clients =
        new Hashtable<Integer, ObjectIO>();

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
		    execute(command);
		}
	    }
	} catch (IOException e) {
	    System.out.println(e);
	}

	// Clean up is last
        accept.stop();
        Package.PMPackage killPackage =
            new Package.PMPackage(Package.Command.KILL);
	try {
            // Kill all slaves in hashtable
            Enumeration e = clients.keys();
            while(e.hasMoreElements()) {
                Integer key = (Integer)e.nextElement();
                ObjectIO io = clients.get(key);
                if(io != null &&
                    io.socket() != null) {
                    // Kill the slaves
                    sendPackage(key.intValue(), killPackage);
                }
            }
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
            Socket client = null;
            ObjectInputStream is = null;
            ObjectOutputStream os = null;
            ObjectIO value = null;
            while(flag) {
                try {
                    client = server.accept();
                    is = new ObjectInputStream(client.getInputStream());
                    os = new ObjectOutputStream(client.getOutputStream());
                    os.flush();
                    value = new ObjectIO(client, is, os);
                    clients.put(client.getPort(), value);
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
        int port = -1;
        Package.PMPackage send = null;

        String[] args = command.split(" ");

        if(command.startsWith("slaves")) {
            list_slaves();
        } else if(command.startsWith("threads")) {
            if(args.length != 2) {
                System.out.println("Expect command of the form:");
                System.out.println("threads <slave port>");
                return;
            }

            port = Integer.valueOf(args[1]).intValue();
            send = new Package.PMPackage(Package.Command.THREADS);       
        }
        else if(command.startsWith("kill")) {
            if(args.length < 2) {
                // TODO: Add error message
                return;
            }

            port = Integer.valueOf(args[1]).intValue();
            send = new Package.PMPackage(Package.Command.KILL);

        } else if(command.startsWith("new")){
            if (args.length < 2) {
                // TODO: Add error message
                return;
            }

            port =  Integer.valueOf(args[1]).intValue();

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
            for(int i = 0; i < process_args.length; i++) {
                System.out.println(process_args[i]);
            }
            try {
                mp = (MigratableProcess)c.getConstructor(String[].class)
                     .newInstance((Object)process_args);
            } catch(Exception e) {
                System.out.println(e);
                return;
            }

            send = new Package.PMPackage(Package.Command.NEW, mp);

	} else if (command.startsWith("migrate")){
	     if(args.length != 4){
		System.out.println("Expect commands of form: migrate <source> <thread> <target>");
		return;
	     }
	     port =  Integer.valueOf(args[1]).intValue();
	     long threadnum = Long.valueOf(args[2]).longValue();
	     int targetport = Integer.valueOf(args[3]).intValue(); 

	     send = new Package.PMPackage(Package.Command.MIGRATE, targetport,                                            threadnum); 
	} else {
	     System.out.println("Invalid Command");
	}

        if(port >= 0 && send != null) {
            sendPackage(port, send);
        }

        return;
    }

    private static void sendPackage(int port, Package.PMPackage send) {
        // Check if valid port number
        if(!clients.containsKey(port)) {
            System.out.println("Invalid port number.");
            return;
        }
        ObjectIO slave = clients.get(port);
        // Check that sockets and io are non-null 
        if(slave.socket() == null || slave.is() == null || slave.os() == null) {
            System.out.println("Slave not functional");
            return;
        }

        Package.SlavePackage recieve = null;
        try {
            System.out.println("Sending...");
            slave.os().writeObject(send);

            try {
                recieve = (Package.SlavePackage)slave.is().readObject();
            }
            catch (ClassNotFoundException e) {
                System.out.println(e);
            }
            System.out.println("Recieved reply");
        } catch (IOException e) {
            System.out.println(e);
        }

        processReply(recieve);

        if (recieve != null) {
            // TODO: Remove this statement
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
            case THREADS: if(reply.message() != null)
                              System.out.print(reply.message());
                          break;
	    case MIGRATE: if(reply == null || !reply.success()){
			      System.out.println("Failed Migrate");
			  } else {
                              startProcess(reply);
                          }
                          break;
            default: break;
        }
    }

    private static void list_slaves() {
        System.out.println("Printing slaves:");
        for (Integer key : clients.keySet()) {
            System.out.println("Slave " + key);
        }
        return;
    }

    private static void startProcess(Package.SlavePackage reply) {
        int port = reply.target();
        Package.PMPackage send = new Package.PMPackage(Package.Command.START, reply.path());
        sendPackage(port, send);
    }
}
