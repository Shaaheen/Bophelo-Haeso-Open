package capstone.bophelohaesoopen.HaesoAPI;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

import capstone.bophelohaesoopen.R;

public class FileUtils
{

    Activity activity;
    public FileUtils(Activity activity)
    {
        this.activity = activity;
    }

    /**
     * Scans device file directory for media files and creates an array of Media objects from the videos found
     * @param prefix - prefix to specify files that are retrieved
     * @param extension - specifies the type of media to retrieve
     * @return An array of Media objects
     */
    public ArrayList<? extends Media> getMediaCollectionFromStorage(String prefix,String extension)
    {
        Log.v("Media","Searching for media files...");
        ArrayList<Media> mediaFiles = new ArrayList<>(); //Stores array of retrieved media

        //Gets list of files/subdir at root lvl
        File[] files = Environment.getExternalStorageDirectory().listFiles();

        getMediaFromDirectory( files , prefix ,extension , mediaFiles);

        Log.v("Media","Done searching.");

        return mediaFiles;
    }

    /**
     * Checks all files and subdir in a given directory for mp4 files with specified prefix
     * @param directory list of files/subdir
     * @param prefix specifies specific files
     * @param extension specifies type of media files to retrieve
     * @param mediaFiles array of media objects retrieved
     */
    private void getMediaFromDirectory(File[] directory , String prefix , String extension , ArrayList<Media> mediaFiles){
        for (File f : directory){
            if (f.isDirectory()){
                getMediaFromDirectory( f.listFiles(), prefix ,extension, mediaFiles);
            }
            //Adds file to container if has appropriate extension and prefix
            else if (f.isFile() && f.getPath().endsWith(extension) && f.getName().startsWith(prefix) ) {
                Log.v("Media","Found : " + f.getName() + " " + f.getPath()  );
                if(extension.equals(Video.mediaExtension))
                {
                    Video video = new Video(f.getName(), f.getAbsolutePath());
                    Bitmap thumbTemp = ThumbnailUtils.createVideoThumbnail(f.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);

                    int width = (int)activity.getResources().getDimension(R.dimen.video_item_width);
                    int height = (int)activity.getResources().getDimension(R.dimen.video_item_height);
                    Bitmap thumb = ThumbnailUtils.extractThumbnail(thumbTemp, width, height);
                    video.thumb = thumb;
                    mediaFiles.add(video);
                }
                else
                {
                    mediaFiles.add( new Media(f.getName(), f.getAbsolutePath()));
                }

            }
        }
    }
}
