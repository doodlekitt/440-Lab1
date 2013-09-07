import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Hashtable;

public class GlobalManager {

    public static void main(String args[]) {
	private static ServerSocket GM = null;
	Hashtable<String, Socket> clients = new Hashtable<String, Socket>();
    }
    // THERE SHOULD PROBABLY BE A METHOD THAT CHECKS THE TABLE AND REMOVES
    // TERMINATED CONNECTIONS

}
