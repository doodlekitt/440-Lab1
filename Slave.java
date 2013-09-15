import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Slave implements java.io.Serializable {

    private static boolean alive = true;

    private static String pmHost = "";
    private static int pmPort = 0;

    private static Socket PM = null;
    private static ObjectInputStream is = null;
    private static ObjectOutputStream os = null;

    public static void main(String args[]) throws IOException {

        // Parse args
	if(args.length < 2) {
	    System.out.println("Expecting arguements of the form:");
            System.out.println("slave <pmHost> <pmPort>");
            return;
	}
	else {
            pmHost = args[0];
            pmPort = Integer.valueOf(args[1]).intValue();
	}

        // Connect to ProcessManager
	try {
            PM = new Socket(pmHost, pmPort);
	} catch (IOException e) {
	    System.out.println(e);
            return;
	}

        System.out.println("Connection successful");

        Package.PMPackage recieve = null;
        Package.SlavePackage send = null;

        while(alive) {
            try {
                is = new ObjectInputStream(PM.getInputStream());
                recieve = (Package.PMPackage)is.readObject();
            }
            catch (IOException | ClassNotFoundException e) {
                System.out.println(e);
            }

            if (recieve != null) {
                switch (recieve.command()) {
                    case KILL: send = kill();
                         break;
                    case NEW: send = newThread(recieve);
                         break;
                    default: break;
                }
                try {
                    os = new ObjectOutputStream(PM.getOutputStream());
                    os.writeObject(send);
                } catch (IOException e) {
                    System.out.println(e);
                }
                recieve = null;
            }
        }

        try {
            PM.close();
            is.close();
            os.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static Package.SlavePackage kill() {
        alive = false;
        return new Package.SlavePackage(Package.Command.KILL, true);
    }

    private static Package.SlavePackage newThread(Package.PMPackage recieve) {
        boolean success = true;

        if (recieve == null || recieve.process() == null) {
            // Return failure
            success = false;
        } else {
                Runnable task = recieve.process();
                Thread thread = new Thread(task);
                thread.start();
        }

        return new Package.SlavePackage(Package.Command.NEW, success);
    }
}
