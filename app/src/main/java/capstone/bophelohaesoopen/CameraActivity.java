package capstone.bophelohaesoopen;

import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.hardware.Camera;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import capstone.bophelohaesoopen.HaesoAPI.Controller.FileUtils;
import capstone.bophelohaesoopen.HaesoAPI.Model.Image;
import capstone.bophelohaesoopen.HaesoAPI.Model.Media;

public class CameraActivity extends AppCompatActivity
{

    private Camera camera;
    private CameraView cameraView;
    FloatingActionButton capture;

    FileUtils fileUtils;

    RelativeLayout captureScreen;

    private static int CAPTURE_ANIM_DURATION = 300;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        String title = getResources().getString(R.string.title_activity_camera);
        setTitle(title);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fileUtils = new FileUtils(this);

        initialize();
    }

    private void initialize()
    {
        capture = (FloatingActionButton)findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                captureClick();
            }
        });

        captureScreen = (RelativeLayout)findViewById(R.id.captureScreen);

        setUpCameraView();
    }

    private void setUpCameraView()
    {
        camera = getCameraInstance();
        cameraView = new CameraView(this, camera);
        FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
        preview.addView(cameraView);
    }

    private void captureClick()
    {
        camera.takePicture(null, null, null, pictureCallback);
        animateCaptureScreen();

    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera)
        {
            camera.stopPreview();
            camera.startPreview();
            Log.i("APP", "Picture callback called");

            // An image file name will be created automatically
            fileUtils.saveMedia(bytes, Media.MediaType.IMAGE, "");

        }
    };

    public void savePicture(byte[] bytes)
    {
        File pictureFile = getOutputMediaFile();
        if(pictureFile != null)
        {
            Log.i("APP", "Picture file not null");
            try
            {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                fileOutputStream.write(bytes);
                Log.i("APP", "File saved");
                fileOutputStream.close();
            }
            catch(FileNotFoundException e)
            {
                e.printStackTrace();
                Log.i("APP", "File NOT saved");
            }
            catch(IOException e1)
            {
                e1.printStackTrace();
                Log.i("APP", "File NOT saved");
            }
        }
    }

    public static Camera getCameraInstance()
    {
        Camera c = null;
        try
        {
            c = Camera.open();
        }
        catch (Exception e)
        {

        }

        return c;
    }

    /** Create a File for saving an image */
    private File getOutputMediaFile()
    {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Bophelo Haeso");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
//                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }

    private void animateCaptureScreen()
    {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 0.6f);
        animator.setDuration(CAPTURE_ANIM_DURATION);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                captureScreen.setAlpha((float)valueAnimator.getAnimatedValue());
            }
        });
        animator.start();

//        captureScreen.setAlpha(0.6f);
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
//                captureScreen.setAlpha(0);
                ValueAnimator animator = ValueAnimator.ofFloat(0.6f, 0);
                animator.setDuration(CAPTURE_ANIM_DURATION);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator)
                    {
                        captureScreen.setAlpha((float)valueAnimator.getAnimatedValue());
                    }
                });
                animator.start();

            }
        };
        handler.postDelayed(runnable, 200);
    }

    //region Activity overrides

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        return true;
    }
    //endregion

    public class SaveAsync extends AsyncTask<Byte, Integer, Long>
    {

        @Override
        protected Long doInBackground(Byte... bytes)
        {
            savePicture(toPrimitiveByteArray(bytes));
            return null;
        }
    }

    public byte[] toPrimitiveByteArray(Byte[] bytes)
    {
        byte[] primArray = new byte[bytes.length];

        for(int i = 0; i < bytes.length; i++)
        {
            primArray[i] = bytes[i];
        }

        return primArray;
    }

    public Byte[] toByteArray(byte[] bytes)
    {
        Byte[] array = new Byte[bytes.length];

        for(int i = 0; i < bytes.length; i++)
        {
            array[i] = bytes[i];
        }

        return array;
    }


}
