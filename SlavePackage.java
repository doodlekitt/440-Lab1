
public class SlavePackage {
    
    private String message;
    private String target;

    // should have file path as message
    // some way to indicate the target for the process migration
    public SlavePackage(String tar, String mes){
	message = mes;
	target = tar;
    }

    public String message(){
	return this.message;
    }

    public String target(){
	return this.target;
    }
}
