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

    private static Hashtable<Long, Thread> threads =
        new Hashtable<Long, Thread>();

    public static void main(String args[]) throws IOException {

        String pmHost = "";
        int pmPort = 0;
        Socket PM = null;
        ObjectInputStream is = null;
        ObjectOutputStream os = null;

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

        Package.PMPackage recieve = null;
        Package.SlavePackage send = null;
        os = new ObjectOutputStream(PM.getOutputStream());
        os.flush();
        is = new ObjectInputStream(PM.getInputStream());

        while(alive) {
            try {
                recieve = (Package.PMPackage)is.readObject();
            }
            catch (IOException e) {
                System.out.println(e);
                return;
            } catch ( ClassNotFoundException e) {
                System.out.println(e);
            }

            if (recieve != null) {
                switch (recieve.command()) {
                    case KILL: send = kill();
                               break;
                    case NEW: send = newThread(recieve);
                              break;
                    case THREADS: send = listThreads(recieve);
                                  break;
                    default: break;
                }
                try {
                    os.writeObject(send);
                } catch (IOException e) {
                    System.out.println(e);
                }
                recieve = null;
            }
        }

        try {
            is.close();
            os.close();
            PM.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private static Package.SlavePackage kill() {
        alive = false;
        return new Package.SlavePackage(Package.Command.KILL, true);
    }

    private static Package.SlavePackage listThreads(Package.PMPackage recieve) {
        String message = "Printing Threads:\n";
        for (Long key : threads.keySet()) {
            message.concat("Threads "+key+": "+threads.get(key).getName()+"\n");
        }

        return new Package.SlavePackage(Package.Command.THREADS, message, true);
    }

    private static Package.SlavePackage newThread(Package.PMPackage recieve) {
        boolean success = true;
        Runnable task = null;
        Thread thread = null;

        if (recieve == null || recieve.process() == null) {
            // Return failure
            success = false;
        } else {
            System.out.println("TEST1");
            task = recieve.process();
            thread = new Thread(task);
            threads.put(thread.getId(), thread);
            thread.start();
        }

        return new Package.SlavePackage(Package.Command.NEW, success);
    }
}
