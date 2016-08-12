package capstone.bophelohaesoopen.HaesoAPI;

import android.content.Context;
import android.os.Environment;
import android.text.Layout;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import java.io.IOException;

/**
 * Created by Shaaheen on 8/8/2016.
 * Class to act as the Haeso media player - plays,pause,seek videos and logs all this information
 */
public class MediaPlayer extends android.media.MediaPlayer{
    String nameOfApp;
    android.media.MediaPlayer mediaPlayer;

    public MediaPlayer(String nameOfApp) {
        this.nameOfApp = nameOfApp;
    }

    /**
     * Plays a media file on a given view
     * @param mediaFile - The Media object containing the media want to play
     * @param mediaView - The view that the media will show on
     * @throws IOException
     */
    public void playMedia(Media mediaFile, SurfaceView mediaView) throws IOException {
        String filePath = Environment.getExternalStorageDirectory() + mediaFile.filePath;
        mediaPlayer = new android.media.MediaPlayer();
        mediaPlayer.setDataSource(filePath);
        mediaPlayer.setScreenOnWhilePlaying(true);
        mediaPlayer.setDisplay( mediaView.getHolder() ); //Sets media onto view
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

}

