import java.io.*;

public class TransactionalFileInputStream extends java.io.InputStream implements java.io.Serializable {
    private long pos = 0;
    private String file_name;
    
    public TransactionalFileInputStream(String filename){
	super(); // needed?
	file_name = filename;
    }

    public synchronized int read() throws IOException {
	int next;
	RandomAccessFile reader = new RandomAccessFile(this.file_name, "r");
	reader.seek(pos);
	next = reader.read();
	reader.close();
	this.pos++;
	return next;
	
    }

    public synchronized int read(byte[] b) throws IOException {
	int ans;
	RandomAccessFile reader = new RandomAccessFile(this.file_name, "r");
	reader.seek(pos);
	ans = reader.read(b);
	reader.close();
	this.pos += ans;
	return ans;
    }

    public synchronized int read(byte[] b, int off, int len) throws IOException{
	int ans; 
	RandomAccessFile reader = new RandomAccessFile(this.file_name, "r");
	reader.seek(pos);
	ans = reader.read(b, off, len);
	reader.close();
	this.pos += ans;
	return ans;
    }


}
