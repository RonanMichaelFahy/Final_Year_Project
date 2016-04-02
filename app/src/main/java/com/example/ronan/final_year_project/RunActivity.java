package com.example.ronan.final_year_project;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.widget.NumberPicker;

public class RunActivity extends Activity implements SensorEventListener {

    private static final String TAG = RunActivity.class.getSimpleName();
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Vibrator mVibrator;
    private static int i;
    private int sensitivity = 10;
    private double[] gravity = new double[3];
    private double[] linear_acceleration = new double[3];
    private double sum;
    private double average;
    private double dt;
    private double t;
    private double tau;
    private static final double fc = 10d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.sensitivity);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                sensitivity = newVal;
                Log.i(TAG, "Changing sensitivity from " + oldVal + " to " + newVal);
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // alpha is calculated as tau / (tau + dt)
        // with t, the low-pass filter's time-constant
        // and dt, the event delivery rate

        if (t!=0){
            dt = (System.nanoTime()/1000000000d) - t;
            tau = 1f/(2*Math.PI*fc);
        }
        t = System.nanoTime()/1000000000d;

        final double alpha = tau / (tau + dt);
        Log.i(TAG, "alpha: "+alpha);

        gravity[0] = alpha * gravity[0] + (1d - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1d - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1d - alpha) * event.values[2];
        Log.i(TAG, "gravity[2]: "+gravity[2]);

        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
        Log.i(TAG, "linear_acceleration[2]: "+linear_acceleration[2]);

        double linear_acceleration_magnitude = Math.sqrt(linear_acceleration[0]*linear_acceleration[0] + linear_acceleration[1]*linear_acceleration[1] + linear_acceleration[2]*linear_acceleration[2]);
        Log.i(TAG, "linear_acceleration_magnitude: "+linear_acceleration_magnitude);

        sum += linear_acceleration_magnitude;
        Log.i(TAG, "sum: "+sum);
        if (i==9){
            average = sum/10d;
            Log.i(TAG, "average: "+average);
            if (average > sensitivity) {
                //mVibrator.vibrate(100);
                System.out.println("Stimulate!\t"+average+" > "+sensitivity);
            }
            i = 0;
            sum = 0;
        } else i++;
    }

    @Override
    protected void onPause() {
        /*xWriter.close();
        yWriter.close();
        zWriter.close();*/
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, sensor.getName()+" accuracy changed to "+accuracy);
    }
}
