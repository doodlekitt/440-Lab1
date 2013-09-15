import java.io.*;
import java.lang.reflect.Constructor;

public class Package {

    /* NEW: Creates a new process
     * MIGRATE: Migrates an existing process to another slave
     * START: Starts a process from a file
     * QUERY: Requests information about slave state
     */
    public enum Command {
        KILL, NEW, MIGRATE, START, THREADS;
    }

    public static class PMPackage implements Serializable{

        private Command command;
        // For a new process
        private MigratableProcess process;
        // For migrating a process
        private int target;
        private long thread;
        // For starting a proces
        private String path;

        // For KILL or THREADS
        public PMPackage(Command com) {
            this.command = com;
        }

        // For NEW
        public PMPackage(Command command, MigratableProcess process){
            this.command = command;
            this.process = process;
        }

        // For MIGRATE
        public PMPackage(Command com, int tar, long thread) {
            this.command = com;
            this.target = tar;
            this.thread = thread;
        }

        // For START
        public PMPackage(Command com, String path) {
            this.command = com;
            this.path = path;
        }

        // Accessors
        public Command command(){
            return this.command;
        }

        public MigratableProcess process() {
            return this.process;
        }

        public int target(){
            return this.target;
        }

        public long thread(){
            return this.thread;
        }

        public String path() {
            return this.path;
        }
    }

    public static class SlavePackage implements Serializable {

        private boolean success; // true if command executed successfully
        private String message; // message to print to user
        private Command command;
        private int target;
        private String path;

        // For use after NEW or failed MIGRATE
        public SlavePackage (Command com, boolean suc) {
            this.command = com;
            this.success = suc;
        }

        // Sends a message with response
        public SlavePackage (Command com, String msg, boolean suc) {
            this.command = com;
            this.message = msg;
            this.success = suc;
        }

        // For use in MIGRATE replies
        public SlavePackage (Command com, int tar, String path, boolean suc){
            this.command = com;
            this.target = tar;
            this.path = path;
            this.success = suc;
        }

        public boolean success() {
            return this.success;
        }

        public String message() {
            return this.message;
        }

        public Command command() {
            return this.command;
        }

        public int target(){
            return this.target;
        }

        public String path() {
            return this.path;
        }
    }
}
