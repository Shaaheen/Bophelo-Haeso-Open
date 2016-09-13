package capstone.bophelohaesoopen.HaesoAPI;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import capstone.bophelohaesoopen.AudioGalleryActivity;
import capstone.bophelohaesoopen.MainActivity;
import capstone.bophelohaesoopen.PictureGalleryActivity;

public class MediaLoadService extends Service
{
    MainActivity mainActivity;
    PictureGalleryActivity galleryActivity;
    AudioGalleryActivity audioGalleryActivity;
    FileUtils fileUtils;
    ArrayList<Video> videoList;
    ArrayList<Image> imageList;
    ArrayList<Audio> audioList;

    private enum MediaType{IMAGE, VIDEO, AUDIO}

    MediaType mediaType;

    public boolean mediaLoaded = false;

    public static String TAG = MediaLoadService.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    public MediaLoadService(MainActivity activity)
    {
        mainActivity = activity;
        videoList = new ArrayList<>();
        mediaType = MediaType.VIDEO;
    }

    public MediaLoadService(PictureGalleryActivity activity)
    {
        galleryActivity = activity;
        imageList = new ArrayList<>();
        mediaType = MediaType.IMAGE;
    }

    public MediaLoadService(AudioGalleryActivity activity)
    {
        audioGalleryActivity = activity;
        imageList = new ArrayList<>();
        mediaType = MediaType.AUDIO;
    }

    public MediaLoadService()
    {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    public void start()
    {
        new LoadTask().execute(fileUtils);
    }

    public void loadVideoList()
    {
        fileUtils = new FileUtils(mainActivity);
        videoList = (ArrayList<Video>)fileUtils.getMediaCollectionFromStorage("chw_", Video.mediaExtension);
        mediaLoaded = true;
    }

    public void loadImageList()
    {
        fileUtils = new FileUtils(galleryActivity);
        imageList = (ArrayList<Image>)fileUtils.getMediaCollectionFromStorage("chw_", Image.mediaExtension);
        mediaLoaded = true;
    }

    public void loadAudioList()
    {
        fileUtils = new FileUtils(galleryActivity);
        audioList = (ArrayList<Audio>)fileUtils.getMediaCollectionFromStorage("chw_", Audio.mediaExtension);
        mediaLoaded = true;
    }


    public ArrayList<Video> getVideoList()
    {
        return videoList;
    }

    public ArrayList<Image> getImageList()
    {
        return imageList;
    }

    public ArrayList<Audio> getAudioList()
    {
        return audioList;
    }

    public class LoadTask extends AsyncTask<FileUtils, Integer, Long>
    {

        @Override
        protected Long doInBackground(FileUtils... fileUtilses)
        {
            if(mediaType == MediaType.IMAGE)
            {
                loadImageList();
            }
            else if(mediaType == MediaType.AUDIO)
            {
                loadAudioList();
            }
            else if(mediaType == MediaType.VIDEO)
            {
                loadVideoList();
            }

            return null;
        }
    }
}
