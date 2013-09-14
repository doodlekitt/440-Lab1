import java.io.*;
import java.lang.reflect.Constructor;

public class Package {

    public enum Command {
        NEW, MIGRATE, START;
    }

    public class PMPackage implements Serializable{

        private Command command;
        private int target; // where to send it
        private Constructor process;
        private String[] args;

        public PMPackage(Command com, int tar, Constructor proc, String[] arg){
            this.command = com;
            this.target = tar;
            this.process = proc;
            this.args = arg;
        }

        public Command command(){
            return this.command;
        }

        public int target(){
            return this.target;
        }

        public Constructor process(){
            return this.process;
        }
    }

    public class SlavePackage {

        private boolean success; // true if command executed successfully
        private Command command;
        private int target;
        private String filePath;

        // For use after NEW or failed MIGRATE
        public SlavePackage (Command com, boolean suc) {
            this.command = com;
            this.success = suc;
        }

        // For use in MIGRATE replies
        public SlavePackage (Command com, int tar, String path, boolean suc){
            this.command = com;
            this.target = tar;
            this.filePath = path;
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

        public String filePath() {
            return this.filePath;
        }
    }
}
