package capstone.bophelohaesoopen.HaesoAPI;

import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by Shaaheen on 8/8/2016.
 * Implemented by Jacob on 12/8/2016.
 * A class that will make audio recordings for our Haeso app.
 */
public class AudioRecorder {

    //In order to use Android MediaRecorder class, we first ned to create an instance of it.
    //MediaRecorder myAudioRecorder = new MediaRecorder();
    private String outputFile;
    MediaRecorder myAudioRecorder;

    public AudioRecorder(){
        myAudioRecorder = new MediaRecorder();
        this.outputFile = FileUtils.getAudioRecordingFileName();
    }

    /**
     *Prepare the application for recording.
     */
    public void prepareForRecording() {
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        myAudioRecorder.setOutputFile(outputFile);

    }

    /**
     * Prepare the audioRecorder and start recording
     * @throws IOException
     */
    public void startRecording() throws IOException {
        myAudioRecorder.prepare();
        myAudioRecorder.start();

        //Log playing of video
        if ( DatabaseUtils.isDatabaseSetup() ){
            LogEntry logEntry = new LogEntry(LogEntry.LogType.RECORDING,"Started recording");
            DatabaseUtils.getInstance().addLog(logEntry);
        }
    }

    /**
     * Stop recording and release the audionRecorder instance.
     */
    public void stopRecording() {
        myAudioRecorder.stop();

        //Log playing of video
        if ( DatabaseUtils.isDatabaseSetup() ){
            LogEntry logEntry = new LogEntry(LogEntry.LogType.RECORDING,"Stopped recording");
            DatabaseUtils.getInstance().addLog(logEntry);
        }

        myAudioRecorder.release();
        myAudioRecorder = null;
    }
}
