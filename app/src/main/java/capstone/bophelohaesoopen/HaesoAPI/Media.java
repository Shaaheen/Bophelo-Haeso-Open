package capstone.bophelohaesoopen.HaesoAPI;

/**
 * Created by Shaaheen on 8/8/2016.
 */
public class Media {
    String name;
    String filePath;
    public static String mediaExtension = "";

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

    public void setName(String name) {
        this.name = name;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
