package capstone.bophelohaesoopen.HaesoAPI.Controller;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.app.Activity;

import java.util.ArrayList;

import capstone.bophelohaesoopen.HaesoAPI.Model.Audio;
import capstone.bophelohaesoopen.HaesoAPI.Model.Image;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;
import capstone.bophelohaesoopen.HaesoAPI.Model.Video;

public class MediaLoadService extends Service
{
    Activity activity;
    FileUtils fileUtils;
    ArrayList<Video> videoList;
    ArrayList<Image> imageList;
    ArrayList<Audio> audioList;

    Media.MediaType mediaType;

    public boolean mediaLoaded = false;

    public static String TAG = MediaLoadService.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    public MediaLoadService(Activity activity, Media.MediaType mediaType)
    {
        this.activity = activity;
        this.mediaType = mediaType;
    }

    public MediaLoadService(){}


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
        new LoadTask().execute(new Void[]{});
    }

    public void loadVideoList()
    {
        fileUtils = new FileUtils(activity);
        videoList = (ArrayList<Video>)fileUtils.getMediaCollectionFromStorage("chw_", Video.mediaExtension);
        mediaLoaded = true;
    }

    public void loadImageList()
    {
        fileUtils = new FileUtils(activity);
        imageList = fileUtils.getImagesFromStorage();
        mediaLoaded = true;
    }

    public void loadAudioList()
    {
        fileUtils = new FileUtils(activity);
        audioList = fileUtils.getRecordingsFromStorage();
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

    public class LoadTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voidObj)
        {
            if(mediaType == Media.MediaType.IMAGE)
            {
                imageList = new ArrayList<>();
                loadImageList();
            }
            else if(mediaType == Media.MediaType.AUDIO)
            {
                audioList = new ArrayList<>();
                loadAudioList();
            }
            else if(mediaType == Media.MediaType.VIDEO)
            {
                videoList = new ArrayList<>();
                loadVideoList();
            }

            return null;
        }
    }
}
