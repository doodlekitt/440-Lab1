import java.io.*;
import java.lang.reflect.Constructor;

public class Package {

    /* NEW: Creates a new process
     * MIGRATE: Migrates an existing process to another slave
     * START: Starts a process from a file
     * QUERY: Requests information about slave state
     */
    public enum Command {
        KILL, NEW, MIGRATE, QUERY, START;
    }

    public static class PMPackage implements Serializable{

        private Command command;
        // For a new process
        private Constructor constructor;
        private String[] args;
        // For migrating a process
        private int target;
        private int process;
        // For starting a proces
        private String path;

        // For KILL or QUERY
        public PMPackage(Command com) {
            this.command = com;
        }

        // For NEW
        public PMPackage(Command com, Constructor construct, String[] arg){
            this.command = com;
            this.constructor = construct;
            this.args = arg;
        }

        // For MIGRATE
        public PMPackage(Command com, int tar, int proc) {
            this.command = com;
            this.target = tar;
            this.process = proc;
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

        public Constructor constructor() {
            return this.constructor;
        }

        public String[] args() {
            return this.args;
        }

        public int target(){
            return this.target;
        }

        public int process(){
            return this.process;
        }

        public String path() {
            return this.path;
        }
    }

    public static class SlavePackage implements Serializable {

        private boolean success; // true if command executed successfully
        private Command command;
        private int target;
        private String path;

        // For use after NEW or failed MIGRATE
        public SlavePackage (Command com, boolean suc) {
            this.command = com;
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
