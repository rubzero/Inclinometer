package com.example.rubze.inclinometer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private TextView lblDegrees, lblFirstDegrees, lblDegreesToMeasure, lblRealDegreesToMeasure, lblDegreesMeasured;
    private int firstDegrees, currentDegrees, degreesToMeasure, counter;
    private View verticalLine, horizontalLine;
    private EditText txt_degreesToMeasure_main;
    private boolean measuring;
    private int value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewClassDeclaration();
        gyroscopeDeclaration();
        textDeclaration();
        buttonDeclaration();
        viewDeclaration();
        measuring = false;
    }

    private void previewClassDeclaration(){
        // Create an instance of Camera
        Camera camera = Utilities.getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        CameraPreview cameraPreview = new CameraPreview(this, camera);
        FrameLayout previewFrameLayout = findViewById(R.id.camera_preview);
        previewFrameLayout.addView(cameraPreview);

        //Inflates and adds the view to the layout
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.camera_overlay, null);
        previewFrameLayout.addView(view);
    }

    //Variables declaration
    private void viewDeclaration(){
        verticalLine = findViewById(R.id.v_main_vertical);
        horizontalLine = findViewById(R.id.v_main_horizontal);
    }

    private void textDeclaration(){
        lblDegrees = findViewById(R.id.lbl_degrees_main);
        lblFirstDegrees = findViewById(R.id.lbl_first_degrees_main);
        lblDegreesToMeasure = findViewById(R.id.lbl_degrees_to_measure_main);
        lblRealDegreesToMeasure = findViewById(R.id.lbl_real_degrees_to_measure_main);
        txt_degreesToMeasure_main = findViewById(R.id.txt_degreesToMeasure_main);
        lblDegreesMeasured = findViewById(R.id.lbl_degrees_measured_main);
    }

    private void buttonDeclaration(){
        Button btnDegrees = findViewById(R.id.btn_degrees_main);
        Button btnResetDegrees = findViewById(R.id.btn_resetDegrees_main);

        btnDegrees.setOnClickListener(this);
        btnResetDegrees.setOnClickListener(this);
    }

    private void gyroscopeDeclaration(){
        SensorManager sensorManager =
                (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sensorManager.registerListener(this, rotationVector
                , SensorManager.SENSOR_DELAY_NORMAL);
    }
    //End of variables declaration

    //set all holders to 0
    private void restartDegrees(){
        value = 0;
        firstDegrees = 0;
        currentDegrees = 0;
        degreesToMeasure = 0;
        lblDegrees.setText(getResources().getString(R.string.zero));
        lblFirstDegrees.setText(getResources().getString(R.string.first_degrees));
        lblDegreesToMeasure.setText(getResources().getString(R.string.degrees_to_measure));
        lblRealDegreesToMeasure.setText(getResources().getString(R.string.real_degrees_to_measure));
        txt_degreesToMeasure_main.setText("");
    }

    //Changes measurer line color depending on the degrees to measure and the phone position
    private void changeGUIColor(){
        int current = currentDegrees;
        if(value==-1)
           current+=360;
        else current = currentDegrees;

        if (current < firstDegrees || current > degreesToMeasure){
            lblDegrees.setTextColor(getResources().getColor(R.color.colorRed));
            verticalLine.setBackgroundColor(getResources().getColor(R.color.colorRed));
            horizontalLine.setBackgroundColor(getResources().getColor(R.color.colorRed));
        }

        else if (current == degreesToMeasure){
            lblDegrees.setTextColor(getResources().getColor(R.color.colorGreen));
            verticalLine.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            horizontalLine.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        }

        else{
            lblDegrees.setTextColor(getResources().getColor(R.color.colorBlack));
            verticalLine.setBackgroundColor(getResources().getColor(R.color.colorBlack));
            horizontalLine.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        }
    }

    //Sets the labels text depending on the phone position
    @SuppressLint("SetTextI18n")
    private void setDegreesToMeasure(){
        if(currentDegrees<0) value=-1;
        measuring = true;
        String degreesStr = txt_degreesToMeasure_main.getText().toString();
        formatFirstDegrees();
        degreesToMeasure = Integer.parseInt(txt_degreesToMeasure_main.getText().toString())+firstDegrees;
        int formatedRealDegrees = Integer.parseInt(degreesStr);
        //Avoids negative values converting them
        if(formatedRealDegrees<0)formatedRealDegrees += 360;
        else formatedRealDegrees = degreesToMeasure;

        lblFirstDegrees.setText(getResources().getString(R.string.first_degrees)+" "+firstDegrees+"º");
        lblDegreesToMeasure.setText(getResources().getString(R.string.degrees_to_measure)+" "+degreesStr+"º");
        lblRealDegreesToMeasure.setText(getResources().getString(R.string.real_degrees_to_measure)+" "+formatedRealDegrees+"º");
    }

    //formats the label that shows the current degrees
    @SuppressLint("SetTextI18n")
    private void setDegrees(){
        int degreesToShow = -firstDegrees + currentDegrees;
        if(degreesToShow <0)
            degreesToShow += 360;
        lblDegrees.setText(degreesToShow +"º");
    }

    private void formatFirstDegrees(){
        if(currentDegrees<0) firstDegrees = currentDegrees+360;
        else firstDegrees = currentDegrees;
    }

    //Receives a counter that determinates that if is the first or second click on the size button
    //then, calculates the difference between both degrees
    @SuppressLint("SetTextI18n")
    private void setMeasuredDegrees(){
        if(counter == 0){
            formatFirstDegrees();
            lblFirstDegrees.setText(getResources().getString(R.string.first_degrees)+" "+firstDegrees+"º");
            counter++;
        }

        else if(counter == 1){
            lblDegreesToMeasure.setText(getResources().getString(R.string.final_degrees)+" "+currentDegrees+"º");
            int measured = -firstDegrees + currentDegrees;
            if(measured < 0 ) measured += 360;
            lblDegreesMeasured.setText(getResources().getString(R.string.degrees_measured)+" "+measured+"º");
            counter = 0;
            measuring = false;
        }
    }

    //Call degrees and color format on phone rotation
    @Override
    public void onSensorChanged(SensorEvent event) {
        currentDegrees = Utilities.getDegrees(Utilities.getOrientations(event));
        setDegrees();
        if(measuring)
            changeGUIColor();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //if the label txt_degreesToMeasure_main is empty, allows user to freely measure degrees
    //else, permits to introduce a determinate degrees to measure
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_degrees_main:
                String degrees = txt_degreesToMeasure_main.getText().toString();
                if(!degrees.equals(""))
                    setDegreesToMeasure();
                else setMeasuredDegrees();
                break;

            case R.id.btn_resetDegrees_main:
                restartDegrees();
                break;
        }
    }

    /*public void requestPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
        else {
        }
    }*/

    /*private void releaseCameraAndPreview() {
        mPreview.setmCamera(null);
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }*/
}
