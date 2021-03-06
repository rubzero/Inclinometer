package com.example.rubze.inclinometer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private static final int threeSixty = 360;

    private int firstDegrees, currentDegrees, degreesToMeasure
            , counter, value;
    private boolean measuring;

    private TextView lblDegrees, lblFirstDegrees, lblDegreesToMeasure
            , lblRealDegreesToMeasure, lblDegreesMeasured;
    private EditText txtDegreesToMeasure;
    private View verticalLine, horizontalLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();
        variableDeclaration();
        measuring = false;
    }

    private void previewClassDeclaration(){
        // Create an instance of Camera
        Camera camera = Utilities.getCameraInstance();

        // Create our preview view and set it as the content of our activity.
        CameraPreview cameraPreview = new CameraPreview(this, camera);
        FrameLayout previewFrameLayout = findViewById(R.id.camera_preview);
        previewFrameLayout.addView(cameraPreview);

        //Inflates and adds the view to the layout
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.camera_overlay, null);
        previewFrameLayout.addView(view);
    }

    /* Initiates all variable that will be used */
    private void variableDeclaration(){
        previewClassDeclaration();
        gyroscopeDeclaration();
        textDeclaration();
        buttonDeclaration();
        viewDeclaration();
    }

    private void viewDeclaration(){
        verticalLine = findViewById(R.id.v_main_vertical);
        horizontalLine = findViewById(R.id.v_main_horizontal);
    }

    private void textDeclaration(){
        lblDegrees = findViewById(R.id.lbl_degrees_main);
        lblFirstDegrees = findViewById(R.id.lbl_first_degrees_main);
        lblDegreesToMeasure = findViewById(R.id.lbl_degrees_to_measure_main);
        lblRealDegreesToMeasure = findViewById(R.id.lbl_real_degrees_to_measure_main);
        txtDegreesToMeasure = findViewById(R.id.txt_degreesToMeasure_main);
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
    /* End of variable declaration */

    /* Set all degree variables to 0 */
    private void restartDegrees(){
        counter = 0;
        value = 0;
        firstDegrees = 0;
        currentDegrees = 0;
        degreesToMeasure = 0;
        lblDegrees.setText(getResources().getString(R.string.zero));
        lblFirstDegrees.setText(getResources().getString(R.string.first_degrees));
        lblDegreesToMeasure.setText(getResources().getString(R.string.degrees_to_measure));
        lblRealDegreesToMeasure.setText(getResources().getString(R.string.real_degrees_to_measure));
        txtDegreesToMeasure.setText("");
    }

    /* Changes measurer line color depending on the degrees to measure and the phone position */
    private void changeGUIColor(){
        int current = currentDegrees;
        if(value==-1)
           current += threeSixty;

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

    /* Sets the labels text depending on the phone position */
    private void setDegreesToMeasure(){
        if(currentDegrees<0) value=-1;
        measuring = true;
        String degrees = txtDegreesToMeasure.getText().toString();

        formatFirstDegrees();
        degreesToMeasure = Integer.parseInt(txtDegreesToMeasure.getText().toString())+firstDegrees;
        int formatedRealDegrees = Integer.parseInt(degrees);

        /* Avoids negative values converting them */
        if(formatedRealDegrees<0) formatedRealDegrees += threeSixty;
        else formatedRealDegrees = degreesToMeasure;

        lblFirstDegrees.setText(getResources()
                .getString(R.string.first_degrees, firstDegrees));
        lblDegreesToMeasure.setText(getResources()
                .getString(R.string.degrees_to_measure, degrees));
        lblRealDegreesToMeasure.setText(getResources()
                .getString(R.string.real_degrees_to_measure,formatedRealDegrees));
    }

    /*
    Formats the label that shows the current degrees requires double check due to multiple formats
    */

    private void setDegrees(){
        int degreesToShow = -firstDegrees + currentDegrees;

        if(degreesToShow < -threeSixty)
            degreesToShow += threeSixty*2;

        else if(degreesToShow < 0)
            degreesToShow += threeSixty;

        String degrees = degreesToShow +"º";
        lblDegrees.setText(degrees);
    }

    private void formatFirstDegrees(){
        if(currentDegrees < 0)
            firstDegrees = currentDegrees + threeSixty;
        else firstDegrees = currentDegrees;
    }

    /*
    Uses a counter that determinate if it is the first or second click on the button.
    Then, calculates the difference between both degrees and sets the label.
    */

    private void setMeasuredDegrees(){
        if(counter == 0)
            setMeasuredDegreesFirstClick();

        else if(counter == 1)
            setMeasuredDegreesSecondClick();
    }

    private void setMeasuredDegreesFirstClick(){
        formatFirstDegrees();
        lblFirstDegrees.setText(getResources()
                .getString(R.string.first_degrees, firstDegrees));
        counter++;
    }

    private void setMeasuredDegreesSecondClick(){

        int current = (currentDegrees < 0) ? currentDegrees + threeSixty : currentDegrees;
        lblDegreesToMeasure.setText(getResources()
                .getString(R.string.final_degrees, current));
        int measured = -firstDegrees + currentDegrees;
        if(measured < 0 ) measured += threeSixty;
        lblDegreesMeasured.setText(getResources()
                .getString(R.string.degrees_measured, measured));
        counter = 0;
        measuring = false;
    }

    /* Call degrees and color format on phone rotation methods */
    @Override
    public void onSensorChanged(SensorEvent event) {
        currentDegrees = Utilities.getDegrees(getOrientations(event));
        setDegrees();
        if(measuring)
            changeGUIColor();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /*
    If the label txtDegreesToMeasure is empty, allows user to freely measure degrees.
    Else, permits to introduce a determinate degrees to measure
    */
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_degrees_main:

                if(!txtDegreesToMeasure
                        .getText().toString().equals(""))
                    setDegreesToMeasure();

                else setMeasuredDegrees();
                break;

            case R.id.btn_resetDegrees_main:
                restartDegrees();
                break;
        }
    }

    /* Request camera permission if needed */
    public void requestPermission(){

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.CAMERA)) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    }
        }
    }

    /* Gets and transform rotation sensor into 3 size degrees array */
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
}
