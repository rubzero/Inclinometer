package com.example.rubze.inclinometer;

import android.hardware.Camera;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class Utilities {

    //Gets and transform rotation sensor into 3 size degrees array
    public static float[] getOrientations(SensorEvent sensorEvent){
        float[] orientations = new float[3];
        float[] rotationMatrix = new float[16];
        float[] remappedRotationMatrix = new float[16];

        SensorManager.getRotationMatrixFromVector(
                rotationMatrix, sensorEvent.values);
        SensorManager.remapCoordinateSystem(rotationMatrix,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z,
                remappedRotationMatrix);
        SensorManager.getOrientation(remappedRotationMatrix, orientations);
        for(int i = 0; i < 3; i++) {
            orientations[i] = (float)(Math.toDegrees(orientations[i]));
        }
        return orientations;
    }

    //Gets the Y degrees value (second position)
    public static int getDegrees(float[] degrees){
        return (int)degrees[2];
    }

    //Trys safely to open camera preview
    public static Camera getCameraInstance(){
        Camera camera = null;
        try {
            camera = Camera.open();
        }
        catch (Exception e){
            Log.d(TAG, e.toString());
        }
        return camera;
    }
}
