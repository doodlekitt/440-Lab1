import java.io.*;

public class Package {

    public enum Command {
        START, MIGRATE;
    }

    public class PMPackage implements Serializable{

        private Command command;
        private int target; // where to send it
        private String process;

        public PMPackage(Command com, int tar, String proc){
            command = com;
            target = tar;
            process = proc;
        }

        public Command command(){
            return this.command;
        }

        public int target(){
            return this.target;
        }

        public String process(){
            return this.process;
        }
    }

    public class SlavePackage {

        private boolean success; // true if command executed successfully
        private Command command;
        private int target;
        private String filePath;

        // For use after START or failed MIGRATE
        public SlavePackage (Command com, boolean suc) {
            command = com;
            success = suc;
        }

        // For use in MIGRATE replies
        public SlavePackage (Command com, int tar, String path, boolean suc){
            command = com;
            target = tar;
            filePath = path;
            success = suc;
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
