import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Hashtable;

public class GlobalManager {

    private static ServerSocket server = null;
    private static Socket PM = null;

    Hashtable<String, Socket> clients = new Hashtable<String, Socket>();
	
    // THERE SHOULD PROBABLY BE A METHOD THAT CHECKS THE TABLE AND REMOVES
    // TERMINATED CONNECTIONS

    public static void main(String args[]) {
	
	int portNumber = 6783;
	if(args.length < 1) {
	    System.out.println("Global Process Manager: Port " + portNumber);
	}
	else{
	    portNumber = Integer.valueOf(args[0]).intValue();
	    System.out.println("Global Process Manager: Port " + portNumber);
	}
	
	try{
	    server = new ServerSocket(portNumber);
	} catch (IOException e) {
	    System.out.println(e);
	}

        Socket clientSocket = null;
	while(true){
	    try{
		clientSocket = server.accept();
		int k = 0;
	    } catch (IOException e) {
		System.out.println(e);
	    }
	}
    }

}
