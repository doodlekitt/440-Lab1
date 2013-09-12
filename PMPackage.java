import java.io.*;

public class PMPackage implements Serializable{

    private String command; // migrate or start
    private String target; // where to send it, can be empty
    private String process; //

    public PMPackage(String comm, String tar, String proc){
	command = comm;
	target = tar;
	process = proc;
    }

    public String command(){
	return this.command;
    }

    public String target(){
	return this.target;
    }

    public String process(){
	return this.process;
    }
}
