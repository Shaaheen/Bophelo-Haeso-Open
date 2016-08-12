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
    MediaRecorder myAudioRecorder = new MediaRecorder();
    private String outputFile;

    public AudioRecorder(String OutPutFileName){
        //The name of the file being created.
        this.outputFile = OutPutFileName;
    }

    /**
     *
     *
     *
     */
    public void recordAudio() throws IOException {
        //To save time it takes for the recording to be in motion, this will later move to the constructor.
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        //Prepare and start recording.
        myAudioRecorder.prepare();
        myAudioRecorder.start();
    }

    /**
     * Stop recording and release the audionRecorder instance.
     */
    public void stopRecording() {
        myAudioRecorder.start();
        myAudioRecorder.release();
    }
}
