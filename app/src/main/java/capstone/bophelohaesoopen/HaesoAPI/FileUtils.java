package capstone.bophelohaesoopen.HaesoAPI;

import android.content.Context;
import android.os.Environment;
import java.io.*;
import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Video;

public class FileUtils
{
    // Reference to calling activity
    Context context;

    public FileUtils(Context context)
    {
        this.context = context;
    }


    public ArrayList<Video> getVideoCollectionFromStorage()
    {
        ArrayList<Video> videos = new ArrayList<>();


        /* The video attributes are hardcoded for the sake of the prototype
        * however, the final implementation will read the device file directory and add videos accordingly*/
        for(int i = 0; i < 11; i++)
        {
            Video temp = new Video("Video "+i, "");
            temp.filePath ="/video.mp4";
            videos.add(temp);
        }

        return videos;
    }

}
