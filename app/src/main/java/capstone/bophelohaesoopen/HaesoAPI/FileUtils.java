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


        for(int i = 0; i < 11; i++)
        {
            Video temp = new Video("Video "+i, "");
            videos.add(temp);
        }

        return videos;
    }

    public boolean isExternalStorageAvailable()
    {
        String extStorageState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(extStorageState))
        {
            return true;
        }
        return false;
    }


}
