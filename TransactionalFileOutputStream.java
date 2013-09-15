import java.io.*;
import java.lang.*;

public class TransactionalFileOutputStream extends java.io.OutputStream implements java.io.Serializable {

    private long pos = 0;
    private String file_name;

    public TransactionalFileOutputStream (String filename) {
	super(); // needed?
	file_name = filename;
    }

    public synchronized void write (int b) throws IOException {
	RandomAccessFile writer = new RandomAccessFile (this.file_name, "rw"); 
	writer.seek(this.pos);
	writer.write(b);
	writer.close();
	this.pos++;
    }

    public synchronized void write(byte[] b) throws IOException 
    {
	RandomAccessFile writer = new RandomAccessFile(this.file_name, "rw");
	writer.seek(this.pos);
	writer.write(b);
	writer.close();
	this.pos += b.length;
    }

    public synchronized void write (byte[] bytes, int off, int len) throws IOException {
    	RandomAccessFile writer = new RandomAccessFile (this.file_name, "rw");
	writer.seek(this.pos);
	writer.write(bytes, off, len);
	writer.close();
	this.pos += len;	 

    }

}
