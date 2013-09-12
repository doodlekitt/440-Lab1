import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Slave implements java.io.Serializable {

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

        // Set up sockets for listening and sending
	try {
            PM = new Socket(pmHost, pmPort);
            is = new ObjectInputStream(PM.getInputStream());
            os = new ObjectOutputStream(PM.getOutputStream());
	} catch (IOException e) {
	    System.out.println(e);
	}

        SlavePackage send = new SlavePackage();
        PMPackage recieve = null;

        os.writeObject(send);

        try {
            recieve = (PMPackage)is.readObject();
        }
        catch (ClassNotFoundException e) {
            System.out.println(e);
        }

        try {
            PM.close();
            is.close();
            os.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
