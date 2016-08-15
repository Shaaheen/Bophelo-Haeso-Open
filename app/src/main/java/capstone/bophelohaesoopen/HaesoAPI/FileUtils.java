package capstone.bophelohaesoopen.HaesoAPI;

import android.os.Environment;
import java.io.*;
import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Video;

public class FileUtils
{

    public FileUtils()
    {}

    /**
     * Scans device file directory for video files and creates an array of Video objects from the videos found
     * @return An array of Video objects
     */
    public ArrayList<Video> getVideoCollectionFromStorage()
    {
        ArrayList<Video> videos = new ArrayList<>();

        /* The video attributes are hardcoded for the sake of the prototype
        * however, the final implementation will read the device file directory and add videos found accordingly
        * */
        for(int i = 1; i < 11; i++)
        {
            Video temp = new Video("Video "+i, "");
            temp.filePath ="/video.mp4";
            videos.add(temp);
        }

        return videos;
    }

}
