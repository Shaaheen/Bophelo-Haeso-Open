package capstone.bophelohaesoopen.HaesoAPI.Model;

public class Audio extends Media {
    public static String mediaExtension = ".3gp";
    public long duration;

    public Audio(String name, String filePath) {
        super(name, filePath);
    }

    public String getFileName(){
        return ( name );
    }
}
