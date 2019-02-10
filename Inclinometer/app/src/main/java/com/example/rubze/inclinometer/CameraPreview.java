package com.example.rubze.inclinometer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Camera camera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.camera = camera;

        /*
        Install a SurfaceHolder. Callback so we get notified when the
        underlying surface is created and destroyed.
        */
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        /* Deprecated setting, but required on Android versions prior to 3.0 */
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        /* The Surface has been created, now tell the camera where to draw the preview. */
        try {
            if(camera == null)
                camera = Camera.open();
            camera.setPreviewDisplay(holder);
            camera.startPreview();

        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        /* Empty. Take care of releasing the Camera preview in your activity. */
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        /*
        If your preview can change or rotate, take care of those events here.
        Make sure to stop the preview before resizing or reformatting it.
         */

        if (surfaceHolder.getSurface() == null){
            /* Preview surface does not exist */
            return;
        }

        /* Stop preview before making changes */
        try {
            camera.stopPreview();
        } catch (Exception ignored){}

        /*
        Set preview size and make any resize, rotate or reformatting changes here
        */

        /* Start preview with new settings */
        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (Exception e){
            Log.d(TAG, e.toString());
        }
    }
}