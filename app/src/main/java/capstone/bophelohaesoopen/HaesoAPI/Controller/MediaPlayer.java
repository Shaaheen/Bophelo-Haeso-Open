package capstone.bophelohaesoopen.HaesoAPI.Controller;

import android.media.AudioManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.Model.Audio;
import capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;

/**
 * Created by Shaaheen on 8/8/2016.
 * Class to act as the Haeso media player - plays,pause,seek videos and logs all this information
 */
public class MediaPlayer extends android.media.MediaPlayer{
    String nameOfApp;
    android.media.MediaPlayer mediaPlayer;

//    SurfaceView surfaceView;
    // Stores play position of video when it is paused
    int currentPosition = 0;
    public boolean prepared = false;

    public MediaPlayer(String nameOfApp) {
        this.nameOfApp = nameOfApp;
        prepared = false;
    }

    /**
     * Plays a media file on a given view
     * @param mediaFile - The Media object containing the media want to play
     * @param mediaView - The view that the media will show on
     * @throws IOException
     */
    public void playVideo(Media mediaFile, SurfaceView mediaView, final int screenWidth, final int screenHeight) throws IOException
    {
        final SurfaceView mView = mediaView;
        String filePath =  mediaFile.getFilePath();
        mediaPlayer = new android.media.MediaPlayer();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.setScreenOnWhilePlaying(true);
        mediaPlayer.setOnPreparedListener(new android.media.MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(android.media.MediaPlayer mediaPlayer)
            {

                // Get video height and width
                int videoWidth = mediaPlayer.getVideoWidth();
                int videoHeight = mediaPlayer.getVideoHeight();
                float videoProportion = (float) videoWidth / (float) videoHeight;

                float screenProportion = (float) screenWidth / (float) screenHeight;
                ViewGroup.LayoutParams lp = mView.getLayoutParams();

                if (videoProportion > screenProportion)
                {
                    lp.width = screenWidth;
                    lp.height = (int) ((float) screenWidth / videoProportion);
                }
                else
                {
                    lp.width = (int) (videoProportion * screenHeight);
//                    lp.width = (int)((float)screenHeight / screenProportion);
                    lp.height = screenHeight;

                }
                mView.setLayoutParams(lp);

            }
        });
        mediaPlayer.setDisplay( mediaView.getHolder() ); //Sets media onto view
        mediaPlayer.prepare();
        mediaPlayer.start();
        prepared = true;

        //Log playing of video
        if ( DatabaseUtils.isDatabaseSetup() ){
            LogEntry logEntry = new LogEntry(LogEntry.LogType.MEDIA_PLAYER,"Play",mediaFile.getFileName());
            DatabaseUtils.getInstance().addLog(logEntry);
        }

    }

    public void playAudio(Audio audioFile) throws IOException
    {
        String filePath =  audioFile.getFilePath();
        mediaPlayer = new android.media.MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.setScreenOnWhilePlaying(true);
        mediaPlayer.prepare();
        mediaPlayer.setOnPreparedListener(new OnPreparedListener()
        {
            @Override
            public void onPrepared(android.media.MediaPlayer player)
            {
                Log.i("BHO", "Media player prepared!");
                mediaPlayer.start();
                prepared = true;
            }
        });
    }

    public void stopMedia()
    {
        mediaPlayer.stop();

        //Log playing of video
        if ( DatabaseUtils.isDatabaseSetup() ){
            LogEntry logEntry = new LogEntry(LogEntry.LogType.MEDIA_PLAYER,"Stopped at " + mediaPlayer.getCurrentPosition());
            DatabaseUtils.getInstance().addLog(logEntry);
        }

        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;


    }

    public boolean isMediaPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public void pauseMedia() throws IllegalStateException
    {
        currentPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.pause();


        //Log playing of video
        if ( DatabaseUtils.isDatabaseSetup() ){
            LogEntry logEntry = new LogEntry(LogEntry.LogType.MEDIA_PLAYER,"Paused at " + currentPosition);
            DatabaseUtils.getInstance().addLog(logEntry);
        }
    }

    public void resumeMedia() throws IOException
    {
        mediaPlayer.start();
        // Seek to position where video was previously paused
        mediaPlayer.seekTo(currentPosition);

        //Log playing of video
        if ( DatabaseUtils.isDatabaseSetup() ){
            LogEntry logEntry = new LogEntry(LogEntry.LogType.MEDIA_PLAYER,"Resumed at: " + currentPosition);
            DatabaseUtils.getInstance().addLog(logEntry);
        }
    }

    public void seekToPosition(int position)
    {
        mediaPlayer.seekTo(position);
    }

    public int getVideoDuration()
    {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPlayPosition()
    {
        return mediaPlayer.getCurrentPosition();
    }

    public boolean isPrepared()
    {
        return prepared;
    }


}

