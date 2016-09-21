package capstone.bophelohaesoopen;


import android.content.Context;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder holder;
    private Camera camera;

    Camera.Size previewSize;

    private List<Camera.Size> supportedPreviewSizes;

    public CameraView(Context context, Camera camera)
    {
        super(context);
        this.camera = camera;
        holder = getHolder();
        holder.addCallback(this);

        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (supportedPreviewSizes != null)
        {
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        Log.i("BHO", "Surface created");
        try
        {
            camera.setDisplayOrientation(90);

            camera.setPreviewDisplay(surfaceHolder);
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            camera.setParameters(parameters);

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
        Log.i("BHO", "Surface changed");
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

            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(previewSize.width, previewSize.height);
            camera.setParameters(parameters);
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
        Log.i("BHO", "Surface destroyed");
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h)
    {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
        {
            return null;
        }

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes)
        {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
            {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff)
            {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null)
        {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes)
            {
                if (Math.abs(size.height - targetHeight) < minDiff)
                {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
