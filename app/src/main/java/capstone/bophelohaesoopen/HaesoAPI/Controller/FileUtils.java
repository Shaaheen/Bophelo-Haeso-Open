package capstone.bophelohaesoopen.HaesoAPI.Controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import capstone.bophelohaesoopen.HaesoAPI.Model.Audio;
import capstone.bophelohaesoopen.HaesoAPI.Model.Image;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;
import capstone.bophelohaesoopen.HaesoAPI.Model.Video;
import capstone.bophelohaesoopen.MainActivity;
import capstone.bophelohaesoopen.R;

public class FileUtils
{
    Activity activity;

    Media.MediaType mediaType;

    public FileUtils(Activity activityGiven)
    {
        activity = activityGiven;
    }

    /**
     * Scans device file directory for media files and creates an array of Media objects from the videos found
     * @param prefix - prefix to specify files that are retrieved
     * @param extension - specifies the type of media to retrieve
     * @return An array of Media objects
     */
    public ArrayList<? extends Media> getMediaCollectionFromStorage(String prefix, String extension)
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
                else if(extension.equals(Image.mediaExtension))
                {
                    Image image = new Image(f.getName(), f.getAbsolutePath());

                    int width = (int)activity.getResources().getDimension(R.dimen.image_item_width);
                    int height = (int)activity.getResources().getDimension(R.dimen.image_item_height);
                    Bitmap thumb = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(f.getAbsolutePath()), width, height);
                    image.thumb = thumb;
                    mediaFiles.add(image);
                }
                else
                {
                    Audio audio = new Audio(f.getName(), f.getAbsolutePath());
                    MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                    metaRetriever.setDataSource(f.getAbsolutePath());
                    String durationString = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long duration = Long.valueOf(durationString);
                    audio.duration = duration;
                    metaRetriever.release();
                    mediaFiles.add(audio);
                }

            }
        }
    }
    public static String getAudioRecordingFileName(){
        DateFormat dateFormat = new SimpleDateFormat("dd\\MM\\yyyy_HH:mm:ss");
        String dateTimeNow =  dateFormat.format(new Date());
        return (Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Media.identifierPrefix +"Aud_Report_" +  dateTimeNow + ".3gp");
    }

    public void saveMedia(byte[] data, Media.MediaType media)
    {
        mediaType = media;
        new SaveAsync().execute(toByteArray(data));
    }

    public void savePicture(byte[] bytes)
    {
        File pictureFile = getOutputImageFile();
        if(pictureFile != null)
        {
            Log.i("APP", "Picture file not null");
            try
            {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                fileOutputStream.write(bytes);
                Log.i("APP", "File saved");
                fileOutputStream.close();
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
                Log.i("APP", "File NOT saved");
            }
            catch(IOException e1)
            {
                e1.printStackTrace();
                Log.i("APP", "File NOT saved");
            }
        }
    }


    /** Create a File for saving an image */
    private File getOutputImageFile()
    {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDirectory = new File(Environment.getExternalStorageDirectory(), MainActivity.appRootFolder);

        // Create the main app storage directory if it does not exist
        if (!mediaStorageDirectory.exists())
        {
            if (!mediaStorageDirectory.mkdirs())
            {
                return null;
            }
        }

        // Create the image directory if it does not exist
        File imageDirectory = new File(mediaStorageDirectory.getAbsolutePath(), MainActivity.appImageFolder);

        if (!imageDirectory.exists())
        {
            if (!imageDirectory.mkdirs())
            {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(imageDirectory.getPath() + File.separator +
                Image.identifierPrefix + timeStamp + Image.mediaExtension);

        return mediaFile;
    }

    public class SaveAsync extends AsyncTask<Byte, Integer, Long>
    {

        @Override
        protected Long doInBackground(Byte... bytes)
        {
            if(mediaType == Media.MediaType.IMAGE)
            {
                savePicture(toPrimitiveByteArray(bytes));
            }

            return null;
        }
    }

    public byte[] toPrimitiveByteArray(Byte[] bytes)
    {
        byte[] byteArray = new byte[bytes.length];

        for(int i = 0; i < bytes.length; i++)
        {
            byteArray[i] = bytes[i];
        }

        return byteArray;
    }

    public Byte[] toByteArray(byte[] bytes)
    {
        Byte[] byteArray = new Byte[bytes.length];

        for(int i = 0; i < bytes.length; i++)
        {
            byteArray[i] = bytes[i];
        }

        return byteArray;
    }
}
