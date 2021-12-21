package com.example.rotatephone;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private TextView textView;
    private ImageView imageView;

    private SensorManager sensorManager;
    private Sensor accelerometerSensor, magnetometerSensor;

    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    boolean isLastAccelerometerArrayCopied = false;
    boolean isLastMagnetometerArrayCopied = false;

    long lastUpdateTime = 0;
    float currentDegree = 0f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == accelerometerSensor){
            System.arraycopy(event.values,0,lastAccelerometer,0,event.values.length);
            isLastAccelerometerArrayCopied = true;
        }
        else if(event.sensor == magnetometerSensor){
            System.arraycopy(event.values,0,lastMagnetometer,0,event.values.length);
            isLastMagnetometerArrayCopied = true;
        }

        if(isLastAccelerometerArrayCopied && isLastMagnetometerArrayCopied && System.currentTimeMillis() - lastUpdateTime >250){
            SensorManager.getRotationMatrix(rotationMatrix,null,lastAccelerometer,lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix,orientation);

            float rotationInRadians = orientation[0];
            float rotationInDegree = (float)Math.toDegrees(rotationInRadians);

            RotateAnimation rotateAnimation =
                    new RotateAnimation(currentDegree, -rotationInDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            imageView.startAnimation(rotateAnimation);

            currentDegree = -rotationInDegree;
            lastUpdateTime = System.currentTimeMillis();

            if(rotationInDegree < 0)
            {
                rotationInDegree = 360 - (rotationInDegree * (-1));
            }

            int x = (int)rotationInDegree;
            textView.setText(x+"°");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this,accelerometerSensor);
        sensorManager.unregisterListener(this,magnetometerSensor);
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onClick(View v) {

    }
}