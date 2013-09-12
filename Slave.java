import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Hashtable;

public class Slave implements java.io.Serializable {

    private static int port = 0;
    private static String pmHost = "";
    private static int pmPort = 0;

    private static Socket PM = null;

    private static ObjectOutputStream os = null;

    public static void main(String args[]) throws IOException {

        // Parse args
        int portNumber = -1;
        String masterHost = null;
	
	if(args.length < 3) {
	    System.out.println("Expecting arguements of the form:");
            System.out.println("slave <slave port> <host> <PM port>");
            System.out.println("Where <slave port> is the port for the new Slave and <host> and <master port> show the location of the Process Manager");
            return;
	}
	else {
	    port = Integer.valueOf(args[0]).intValue();
            pmHost = args[1];
            pmPort = Integer.valueOf(args[2]).intValue();
	}

        // Set up sockets for listening and sending
	try {
            PM = new Socket(pmHost, pmPort);
            os = new ObjectOutputStream(PM.getOutputStream());
	} catch (IOException e) {
	    System.out.println(e);
	}

        try {
            PM.close();
            os.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
