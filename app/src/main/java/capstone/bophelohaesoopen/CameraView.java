package capstone.bophelohaesoopen;


import android.content.Context;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder holder;
    private Camera camera;

    public CameraView(Context context, Camera camera)
    {
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        try
        {
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch(IOException e)
        {
            System.out.println("Error setting camera preview : "+e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {
        if(holder.getSurface() == null)
        {
            return;
        }

        try
        {
            camera.stopPreview();
        }
        catch(Exception e)
        {

        }

        try
        {
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch(Exception e)
        {
            System.out.println("Error setting camera preview : "+e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        camera.release();
    }
}
