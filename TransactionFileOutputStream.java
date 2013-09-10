import java.io.*;

public class TransactionFileOutputStream extends java.io.OutputStream implements java.io.Serializable {
    public synchronized void write (int b) throws IOException {}

    public synchronized void write (byte[] bytes, int off, int len) throws IOException {}
}
