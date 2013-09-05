public interface MigratableProcess extends java.lang.Runnable, java.io.Serializable
{
    public void suspend();
    public void run ();
}
