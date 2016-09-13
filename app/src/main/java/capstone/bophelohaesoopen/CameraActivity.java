package capstone.bophelohaesoopen;

import android.hardware.Camera;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.File;

public class CameraActivity extends AppCompatActivity
{

    private Camera camera;
    private CameraView cameraView;
//    FrameLayout preview;
    FloatingActionButton capture;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        camera = getCameraInstance();

        cameraView = new CameraView(this, camera);

        FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
        capture = (FloatingActionButton)findViewById(R.id.capture);

//        ViewGroup parent = (ViewGroup)preview.getParent(;
//        parent.removeView(preview);
//        parent.addView(cameraView);
        preview.addView(cameraView);
        System.out.println("Camera activity created");
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

    private Camera.PictureCallback picture = new Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera)
        {
//            File pictureFile = get
        }
    };

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
}
