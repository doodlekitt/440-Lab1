import java.io.*;

public class TransactionFileInputStream extends java.io.InputStream implements java.io.Serializable {
    public synchronized int read() throws IOException {
        return -1;
    }
}
