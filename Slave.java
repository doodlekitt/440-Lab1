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
            System.out.println("Connecting at " + pmHost + " " + pmPort);
	}

        // Set up sockets for listening and sending
	try {
            PM = new Socket(pmHost, pmPort);
            System.out.println("Connection successful");
try {
    Thread.sleep(1000);
} catch(InterruptedException ex) {
    Thread.currentThread().interrupt();
}
            is = new ObjectInputStream(PM.getInputStream());
            os = new ObjectOutputStream(PM.getOutputStream());
	} catch (IOException e) {
cation: class ProcessManager
ProcessManager.java:198: error: cannot find symbol
            
	    System.out.println(e);
	}

        Package.PMPackage recieve = null;

        while(alive) {
            try {
                recieve = (Package.PMPackage)is.readObject();
            }
            catch (ClassNotFoundException e) {
                System.out.println(e);
            }

            if (recieve != null) {
                switch (recieve.command()) {
                    case KILL: kill();
                         break;
                    default: break;
                }
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

    private static void kill() {
        alive = false;
        Package.SlavePackage reply =
            new Package.SlavePackage(Package.Command.KILL, true);

        try {
            os.writeObject(reply);
        } catch(IOException e) {
            System.out.println(e);
        }

        return;
    }
}
