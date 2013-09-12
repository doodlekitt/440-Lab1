import java.io.*;
import java.lang.*;

public class MathProcess implements MigratableProcess 
{
    private volatile boolean suspended;
    private double sum = 0; 
    private double a;
    private double b;

    public MathProcess(String args[]) throws Exception 
    {
	if(args.length != 2) {
	    System.out.println("usage: MathProcess <startnumber> <endnumber>");
	    throw new Exception("Invalid Arguments");
	}	
	a = Double.parseDouble(args[0]);
	b = Double.parseDouble(args[1]);
    }

    public void run()
    {
	while(!suspended){
	    if(a == b) break;
	    sum += a;
	    a++;

	    try{
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		//ignored
	    }
	}
	suspended = false;
	System.out.println(sum);
    }

    public void suspend()
    {
	suspended = true;
	while(suspended);
    }

}
