package capstone.bophelohaesoopen.HaesoAPI.Controller;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.IOException;

import capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry;

/**
 * Created by Shaaheen on 8/8/2016.
 * Implemented by Jacob on 12/8/2016.
 * A class that will make audio recordings_black for our Haeso app.
 */
public class AudioRecorder {

    //In order to use Android MediaRecorder class, we first ned to create an instance of it.
    //MediaRecorder myAudioRecorder = new MediaRecorder();
    private String outputFile;
    MediaRecorder myAudioRecorder;
    public static int MAX_DURATION;
    public boolean maxDurationReached = false;

    public AudioRecorder(){
        myAudioRecorder = new MediaRecorder();
        outputFile = FileUtils.getAudioRecordingFileName();
    }

    /**
     *Prepare the application for recording_black.
     */
    public void prepareForRecording() {
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setMaxDuration(MAX_DURATION);
//        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/chw_recording.3gp";
        myAudioRecorder.setOutputFile(outputFile);
        myAudioRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener()
        {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int i, int i1)
            {
                if(i == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
                {
                    maxDurationReached = true;
                }
            }
        });
        try
        {
            myAudioRecorder.prepare();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Prepare the audioRecorder and start recording_black
     * @throws IOException
     */
    public void startRecording() throws IOException {

        myAudioRecorder.start();

        //Log playing of video
        if ( DatabaseUtils.isDatabaseSetup() ){
            LogEntry logEntry = new LogEntry(LogEntry.LogType.RECORDING,"Started recording_black");
            DatabaseUtils.getInstance().addLog(logEntry);
        }
    }

    /**
     * Stop recording_black and release the audio Recorder instance.
     */
    public void stopRecording() {
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;

        //Log playing of audio
        if ( DatabaseUtils.isDatabaseSetup() ){
            LogEntry logEntry = new LogEntry(LogEntry.LogType.RECORDING,"Stopped recording_black");
            DatabaseUtils.getInstance().addLog(logEntry);
        }


    }

    public void setRecordingDurationLimit(int minutes)
    {
        // Minutes converted to milliseconds
        MAX_DURATION = minutes * 60 * 1000;
    }
}
