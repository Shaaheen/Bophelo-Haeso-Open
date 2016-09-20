package capstone.bophelohaesoopen.HaesoAPI.Model;

import android.graphics.Bitmap;

public class Video extends Media {
    String thumbnailPath;
    public static String mediaExtension = ".mp4";
    public Bitmap thumb;

    public Video(String name, String filePath) {
        super(name, filePath);
    }

    /**
     * Instance of Video object
     * @param name - name of file
     * @param filePath - path to file
     * @param thumbnailPath - path to thumbnail image
     */
    public Video(String name, String filePath, String thumbnailPath) {
        super(name, filePath);
        this.thumbnailPath = thumbnailPath;
    }

    public String getFileName(){
        return (name );
    }

    public String getFilePathName(){
        return (identifierPrefix + name + mediaExtension);
    }


}
