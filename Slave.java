import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

public class Slave implements java.io.Serializable {

    private static boolean alive = true;

    private static class MPThread {
        private MigratableProcess process;
        private Thread thread;

        public MPThread(MigratableProcess process, Thread thread) {
            this.process = process;
            this.thread = thread;
        }

        public MigratableProcess process() {
            return process;
        }

        public Thread thread() {
            return thread;
        }
    }

    private static Hashtable<Long, MPThread> threads =
        new Hashtable<Long, MPThread>();

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
                    case THREADS: System.out.println("Listing threads...");
                                  send = listThreads(recieve);
                                  break;
		    case MIGRATE: System.out.println("Migrating...");
                                  send = migrate(recieve);
				  break;
		    case START: send = startThread(recieve);
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

    private static Package.SlavePackage startThread(Package.PMPackage recieve) {
	boolean success = true;
	String filepath = recieve.path();
	
	try{
	    FileInputStream fs = new FileInputStream(filepath);
	    ObjectInputStream infile = new ObjectInputStream(fs);
	
	    MigratableProcess mp = (MigratableProcess) infile.readObject();

	    Thread thread = new Thread(mp);
            MPThread m = new MPThread(mp, thread);
            threads.put(thread.getId(), m);
            thread.start();
	
	    infile.close();
	    fs.close();
	} catch (Exception e) {
	    success = false;
	    System.out.println(e);
	}
	return new Package.SlavePackage(Package.Command.START, success);
    }

    private static Package.SlavePackage migrate(Package.PMPackage recieve) {
	boolean success = true;
        String filename = "thread"+recieve.target()+recieve.thread()+".ser";

	try{
	    FileOutputStream fs = new FileOutputStream(filename);
	    ObjectOutputStream outfile = new ObjectOutputStream(fs);

	    MPThread mpt = threads.get(recieve.thread());
	    threads.remove(recieve.thread());
	    MigratableProcess mp = mpt.process();
	    mp.suspend();

	    outfile.writeObject(mp); 
	    outfile.close();
	    fs.close();
        }
	catch (IOException e) {
	    success = false;
	    System.out.println(e);
	}
	return new Package.SlavePackage(Package.Command.MIGRATE,
                                        recieve.target(), filename, success);
    }

    private static Package.SlavePackage kill() {
        alive = false;
        return new Package.SlavePackage(Package.Command.KILL, true);
    }

    private static Package.SlavePackage listThreads(Package.PMPackage recieve) {
        String message = "Printing Threads:\n";
        for (Long key : threads.keySet()) {
            message = message + "Threads "+key+"\n";
        }

        return new Package.SlavePackage(Package.Command.THREADS, message, true);
    }

    private static Package.SlavePackage newThread(Package.PMPackage recieve) {
        boolean success = true;
        MigratableProcess task = null;
        Thread thread = null;
        MPThread mpThread = null;

        if (recieve == null || recieve.process() == null) {
            // Return failure
            success = false;
        } else {
            task = recieve.process();
            thread = new Thread(task);
            mpThread = new MPThread(task, thread);
            threads.put(thread.getId(), mpThread);
            thread.start();
        }

        return new Package.SlavePackage(Package.Command.NEW, success);
    }
}
