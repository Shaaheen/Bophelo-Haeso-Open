package capstone.bophelohaesoopen.HaesoAPI.Model;

/**
 * Created by Shaaheen on 8/8/2016.
 */
public class Audio extends Media {
    public static String mediaExtension = ".mp3";
    public long duration;

    public Audio(String name, String filePath) {
        super(name, filePath);
    }

    public String getFileName(){
        return (identifierPrefix + name + mediaExtension);
    }
}