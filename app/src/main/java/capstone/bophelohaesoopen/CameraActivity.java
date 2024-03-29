package capstone.bophelohaesoopen;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.hardware.Camera;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import capstone.bophelohaesoopen.HaesoAPI.Controller.DatabaseUtils;
import capstone.bophelohaesoopen.HaesoAPI.Controller.FileUtils;
import capstone.bophelohaesoopen.HaesoAPI.Model.Image;
import capstone.bophelohaesoopen.HaesoAPI.Model.LogEntry;
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

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (verifyStoragePermission()){
            setUpCamera();
        }
    }

    private void setUpCamera() {
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

    private boolean verifyStoragePermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        3);

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        3);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
            return false;
        }
        else{
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 3: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpCamera();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.w("Rec","Permission denied");
                    onBackPressed();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void initialize()
    {
        
        LogEntry logEntry = new LogEntry(LogEntry.LogType.PAGE_VISITS, "Camera", null);
        if(DatabaseUtils.isDatabaseSetup())
        {
            DatabaseUtils.getInstance().addLog(logEntry);
        }

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
        cameraView = null;
        cameraView = new CameraView(this, camera);

        FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
        preview.addView(cameraView);

        setCameraDisplayOrientation(this,0 ,camera);
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
    protected void onStop()
    {
        Log.i("BHO", "ON STOP");
        super.onStop();
    }


    @Override
    public void onBackPressed()
    {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        Log.i("BHO", "ON DESTROY");
        super.onDestroy();
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        finish();
        camera.release();
        return true;
    }
    //endregion

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation)
        {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else
        {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }
}
