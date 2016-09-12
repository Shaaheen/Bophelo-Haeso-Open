package capstone.bophelohaesoopen.HaesoAPI;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;

import capstone.bophelohaesoopen.MainActivity;

public class MediaLoadService extends Service
{
    MainActivity mainActivity;
    FileUtils fileUtils;
    ArrayList<Video> videoList;
    public boolean videosLoaded = false;

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
    }

    public MediaLoadService()
    {
        videoList = new ArrayList<>();
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

    public void loadMediaList()
    {
        fileUtils = new FileUtils(mainActivity);
        videoList = (ArrayList<Video>)fileUtils.getMediaCollectionFromStorage("chw_", Video.mediaExtension);

        videosLoaded = true;
    }

    public ArrayList<Video> getVideoList()
    {
        return videoList;
    }

    public class LoadTask extends AsyncTask<FileUtils, Integer, Long>
    {

        @Override
        protected Long doInBackground(FileUtils... fileUtilses)
        {
            loadMediaList();
            return null;
        }
    }
}
