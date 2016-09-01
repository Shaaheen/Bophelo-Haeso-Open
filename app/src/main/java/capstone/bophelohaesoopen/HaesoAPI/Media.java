package capstone.bophelohaesoopen.HaesoAPI;

/**
 * Created by Shaaheen on 8/8/2016.
 */
public class Media {
    String name;
    String filePath;
    public static String identifierPrefix = "";
    public static String mediaExtension = "";

    /**
     * Represents a media object
     * @param name Name of media (No extension or prefix here)
     * @param filePath Path to file in phone storage
     */
    public Media(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName(){
        return (identifierPrefix + name + mediaExtension);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setIdentifierPrefix(String identifierPrefix) {
        Media.identifierPrefix = identifierPrefix;
    }
}
