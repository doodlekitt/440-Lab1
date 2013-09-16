import java.io.*;
import java.lang.*;

public class CopyProcess implements MigratableProcess 
{
    private TransactionalFileInputStream source;
    private TransactionalFileOutputStream target;

    private volatile boolean suspended;

    public CopyProcess(String args[]) throws Exception {
	if (args.length != 2) {
	    System.out.println("usage: CopyProcess <sourcefile> <targetfile>");
	    throw new Exception("Invalid Arguments");
	}	
    	source = new TransactionalFileInputStream(args[0]);
	target = new TransactionalFileOutputStream(args[1]);
    }

    public void run()
    {
	PrintStream out = new PrintStream(target);
	DataInputStream in = new DataInputStream(source);
	
	try{
	    while(!suspended) {
		String line = in.readLine();

		if(line == null) break;

		out.println(line);
	    
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		//ignored
	    }
	    }
	} catch (EOFException e) {
	    // Reached end of file
	} catch (IOException e) {
	    System.out.println("CopyProcess: Error " + e);
	}
	suspended = false;

    }

    public void suspend()
    {
	suspended = true;
    }


}
