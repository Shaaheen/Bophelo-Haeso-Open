package capstone.bophelohaesoopen.HaesoAPI.Controller;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import capstone.bophelohaesoopen.HaesoAPI.Model.Audio;
import capstone.bophelohaesoopen.HaesoAPI.Model.Image;
import capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;
import capstone.bophelohaesoopen.HaesoAPI.Model.Video;
import capstone.bophelohaesoopen.R;

public class FileUtils
{
    Activity activity;

    Media.MediaType mediaType;
    String outputFileName;

    //Default values set
    private static String appRootFolder = "HaesoAPI";
    private static String appRecordingsFolder = "Recordings";
    private static String appImageFolder = "Images";
    private static String appVideosFolder = "Videos";

    public static String LOG_FILE_NAME = "UserLog.csv";

    public FileUtils(Activity activityGiven)
    {
        activity = activityGiven;
    }

    public FileUtils() {}

    public static void setFolderNames(String appRoot, String appVideo, String appRecording, String appImage){
          appRootFolder = appRoot;
          appRecordingsFolder = appRecording;
          appVideosFolder = appVideo;
          appRecordingsFolder = appRecording;
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
     * Checks all files and subdir in a given directory for specfic extension files with specified prefix
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
        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        String dateTimeNow =  dateFormat.format(new Date());
        System.out.println("BHO : DATE TIME NOW = "+dateTimeNow);
        File mediaStorageDirectory = getAppDirectory(
                Environment.getExternalStorageDirectory().getAbsolutePath() , appRootFolder);
        if (mediaStorageDirectory == null) return null;

        File audioDirectory = getAppDirectory(mediaStorageDirectory.getAbsolutePath(), appRecordingsFolder);
        if (audioDirectory == null) return null;

        return (audioDirectory.getAbsolutePath() + "/" + Media.identifierPrefix +"Report_" + dateTimeNow + ".3gp");
    }

    public void saveMedia(byte[] data, Media.MediaType media, String outFileName)
    {
        mediaType = media;
        outputFileName = outFileName;

        // Performs save operation asynchronously
        new SaveAsync().execute(toByteArray(data));
    }

    public void saveImage(byte[] bytes)
    {
        File tempOutputFile = getOutputImageFile();

        try
        {
            // Write image out to file temporarily
            FileOutputStream fileOutputStream = new FileOutputStream(tempOutputFile);
            fileOutputStream.write(bytes);
            Log.i("BHO", "File saved");
            fileOutputStream.close();
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
            Log.i("BHO", "File NOT saved");
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.i("BHO", "File NOT saved");
        }
        try
        {
            // Read in saved image file into Bitmap object for manipulation
            FileInputStream fis = new FileInputStream(tempOutputFile);
            Bitmap image = BitmapFactory.decodeStream(fis);

            // Delete original temporary file
            tempOutputFile.delete();

            // Calculate sizes to scale the image down to
            int newWidth = image.getWidth()/3;
            int newHeight = image.getHeight()/3;

            // Create scaled down image from the original image
            Bitmap scaledDownImage = Bitmap.createScaledBitmap(image, newWidth, newHeight, false);

            // Matrix object to be used to rotate the image since by default, the image was captured with a -90 degree rotation
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap finalImage = Bitmap.createBitmap(scaledDownImage, 0, 0, scaledDownImage.getWidth(), scaledDownImage.getHeight(), matrix, false);

            // Get new output file instance
            File newOutputFile = getOutputImageFile();

            // Save scaled, rotated image
            FileOutputStream fos = new FileOutputStream(newOutputFile);
            finalImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void writeOut(byte[] bytes)
    {
        File outputFile = null;
        if(mediaType == Media.MediaType.VIDEO)
        {
            Log.i("BHO", "Video file to be saved");
            outputFile = getOutputVideoFile();
        }

        // Audio files are created and written out automatically by the Android MediaRecorder class

        if(outputFile != null)
        {
            Log.i("BHO", "File not null");
            try
            {
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                fileOutputStream.write(bytes);
                Log.i("BHO", "File saved");
                fileOutputStream.close();
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
                Log.i("BHO", "File NOT saved");
            }
            catch(IOException e1)
            {
                e1.printStackTrace();
                Log.i("BHO", "File NOT saved");
            }
        }

    }

    /** Create a file for saving an image */
    public File getOutputImageFile()
    {
        File mediaStorageDirectory = getAppDirectory(
                Environment.getExternalStorageDirectory().getAbsolutePath() , appRootFolder);
        if (mediaStorageDirectory == null) return null;

        // Create the image directory if it does not exist
        File imageDirectory = getAppDirectory(mediaStorageDirectory.getAbsolutePath(), appImageFolder);
        if (imageDirectory == null) return null;

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File mediaFile = new File(imageDirectory.getPath() + File.separator + Image.identifierPrefix + timeStamp + Image.mediaExtension);

        return mediaFile;
    }

    /** Create a file for saving a video */
    private File getOutputVideoFile()
    {
        File mediaStorageDirectory = getAppDirectory(
                Environment.getExternalStorageDirectory().getAbsolutePath() , appRootFolder);
        if (mediaStorageDirectory == null) return null;

        File videoDirectory = getAppDirectory(mediaStorageDirectory.getAbsolutePath(), appVideosFolder);
        if (videoDirectory == null) return null;

        File videoFile = new File(videoDirectory.getPath() + File.separator + outputFileName);

        return videoFile;
    }

    public static void writeToLogFile(List<LogEntry> logEntriesToWrite){
        Log.v("DB",Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.v("DB",appRootFolder);
        File mediaStorageDirectory = getAppDirectory(
                Environment.getExternalStorageDirectory().getAbsolutePath() , appRootFolder);
        if (mediaStorageDirectory == null) return ;

        File logFile = new File(mediaStorageDirectory.getPath() + File.separator + LOG_FILE_NAME);
        try {
            FileWriter fileWriter = new FileWriter(logFile,true);
            for (LogEntry logEntry : logEntriesToWrite){
                fileWriter.write( logEntry.csvFormat()  + "\r\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.w("File","Wrote to Log file");
    }

    @Nullable
    private static File getAppDirectory(String basePath, String directoryName) {

        File mediaStorageDirectory = new File(basePath, directoryName);
        if (mediaStorageDirectory == null) return null;

        return mediaStorageDirectory;
    }


    public class SaveAsync extends AsyncTask<Byte, Integer, Long>
    {

        @Override
        protected Long doInBackground(Byte... bytes)
        {
            if(mediaType == Media.MediaType.IMAGE)
            {
                saveImage(toPrimitiveByteArray(bytes));
            }
            else
            {
                writeOut(toPrimitiveByteArray(bytes));
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
