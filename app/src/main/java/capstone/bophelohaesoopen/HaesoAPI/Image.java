package capstone.bophelohaesoopen.HaesoAPI;

import android.graphics.Bitmap;


public class Image extends Media {
    public static String mediaExtension = ".jpg";

    public Bitmap thumb;

    public Image(String name, String filePath) {
        super(name, filePath);
    }

    public String getFileName(){
        return (identifierPrefix + name + mediaExtension);
    }
}
