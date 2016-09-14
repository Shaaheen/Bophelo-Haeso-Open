package capstone.bophelohaesoopen.HaesoAPI.Controller;

import android.view.SurfaceView;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;

/**
 * Created by Shaaheen on 8/8/2016.
 * Class to act as the Haeso media player - plays,pause,seek videos and logs all this information
 */
public class MediaPlayer extends android.media.MediaPlayer{
    String nameOfApp;
    android.media.MediaPlayer mediaPlayer;

    // Stores play position of video when it is paused
    int currentPosition = 0;

    public MediaPlayer(String nameOfApp) {
        this.nameOfApp = nameOfApp;
    }

    /**
     * Plays a media file on a given view
     * @param mediaFile - The Media object containing the media want to play
     * @param mediaView - The view that the media will show on
     * @throws IOException
     */
    public void playMedia(Media mediaFile, SurfaceView mediaView) throws IOException
    {
        String filePath =  mediaFile.getFilePath();
        mediaPlayer = new android.media.MediaPlayer();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.setScreenOnWhilePlaying(true);
        mediaPlayer.setDisplay( mediaView.getHolder() ); //Sets media onto view
        mediaPlayer.prepare();
        mediaPlayer.start();

        //Log playing of video
        if ( DatabaseUtils.isDatabaseSetup() ){
            LogEntry logEntry = new LogEntry(LogEntry.LogType.MEDIA_PLAYER,"Play",mediaFile.getFileName());
            DatabaseUtils.getInstance().addLog(logEntry);
        }

    }

    public void stopMedia()
    {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;

        //Log playing of video
        if ( DatabaseUtils.isDatabaseSetup() ){
            LogEntry logEntry = new LogEntry(LogEntry.LogType.MEDIA_PLAYER,"Stopped");
            DatabaseUtils.getInstance().addLog(logEntry);
        }
    }

    public boolean isMediaPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public void pauseMedia() throws IllegalStateException
    {
        mediaPlayer.pause();
        currentPosition = mediaPlayer.getCurrentPosition();

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



}

