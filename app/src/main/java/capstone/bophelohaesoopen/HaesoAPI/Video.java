package capstone.bophelohaesoopen.HaesoAPI;

/**
 * Created by Shaaheen on 8/8/2016.
 */
public class Video extends Media {
    String thumbnailPath;

    public Video(String name, String filePath) {
        super(name, filePath);
    }

    public Video(String name, String filePath,String thumbnailPath) {
        super(name, filePath);
        this.thumbnailPath = thumbnailPath;
    }

}
