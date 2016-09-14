package capstone.bophelohaesoopen.HaesoAPI.Model;

import android.graphics.Bitmap;

/**
 * Created by Shaaheen on 8/8/2016.
 */
public class Video extends Media {
    String thumbnailPath;
    public static String mediaExtension = ".mp4";
    public Bitmap thumb;

    public Video(String name, String filePath) {
        super(name, filePath);
    }

    public Video(String name, String filePath, String thumbnailPath) {
        super(name, filePath);
        this.thumbnailPath = thumbnailPath;
    }

    public String getFileName(){
        return (identifierPrefix + name + mediaExtension);
    }


}
