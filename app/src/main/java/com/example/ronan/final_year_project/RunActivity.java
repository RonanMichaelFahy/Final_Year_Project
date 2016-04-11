package com.example.ronan.final_year_project;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.NumberPicker;

import com.parse.ParseUser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RunActivity extends Activity implements SensorEventListener {

    private static final String TAG = RunActivity.class.getSimpleName();
    private Sensor mAccelerometer;
    private Vibrator mVibrator;
    private String fileName = "accelerometerData.csv";
    private String fileName1 = "accelerometerData1.csv";
    private String fileName2 = "accelerometerData2.csv";
    private String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName;
    private String path1 = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName1;
    private String path2 = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + fileName2;
    private File file = new File(path);
    private File file1 = new File(path1);
    private File file2 = new File(path2);
    private FileWriter fileWriter;
    private FileWriter fileWriter1;
    private FileWriter fileWriter2;
    private static int i;
    private int sensitivity = 10;
    private double[] gravity = new double[]{0, 0, 0};
    private double[] linear_acceleration = new double[3];
    private double sum;
    private static final double fc = 0.5d;
    private Handler mHandler = new Handler();
    private float[] raw = new float[]{0, 0, 0};
    private Runnable sampler = new Runnable() {
        @Override
        public void run() {
            double tau = 1 / (2 * Math.PI * fc);
            final double alpha = tau / (tau + 0.02);

            gravity[0] = alpha * gravity[0] + (1d - alpha) * raw[0];
            gravity[1] = alpha * gravity[1] + (1d - alpha) * raw[1];
            gravity[2] = alpha * gravity[2] + (1d - alpha) * raw[2];
            //Log.i(TAG, "gravity[2]: "+gravity[2]);

            try {
                fileWriter.append((char) gravity[0]).append(", ");
                fileWriter1.append((char) gravity[1]).append(", ");
                fileWriter2.append((char) gravity[2]).append(", ");
            } catch (IOException e) {
                e.printStackTrace();
            }

            linear_acceleration[0] = raw[0] - gravity[0];
            linear_acceleration[1] = raw[1] - gravity[1];
            linear_acceleration[2] = raw[2] - gravity[2];
            //Log.i(TAG, "linear_acceleration[2]: "+linear_acceleration[2]);

            double linear_acceleration_magnitude = Math.sqrt(linear_acceleration[0]*linear_acceleration[0] + linear_acceleration[1]*linear_acceleration[1] + linear_acceleration[2]*linear_acceleration[2]);
            //Log.i(TAG, "linear_acceleration_magnitude: "+linear_acceleration_magnitude);

            sum += linear_acceleration_magnitude;
            Log.i(TAG, "sum: "+sum);
            Log.i(TAG, "i: "+i);
            if (i==9){
                double average = sum / 10d;
                Log.i(TAG, "average: "+ average);
                /*try {
                    fileWriter.append(Double.toString(average)).append(", ");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                if (average > sensitivity) {
                    //mVibrator.vibrate(100);
                    System.out.println("Stimulate!\t"+ average +" > "+sensitivity);
                }
                i = 0;
                sum = 0;
            } else i++;

            mHandler.postDelayed(this, 20);
        }
    };

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

        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, 20000);
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);

        try {
            boolean created = file.createNewFile();
            file1.createNewFile();
            file2.createNewFile();
            Log.i(TAG, "created: "+created);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (file.exists() && !file.isDirectory()) {
            try {
                fileWriter = new FileWriter(file, true);
                fileWriter1 = new FileWriter(file1, true);
                fileWriter2 = new FileWriter(file2, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sampler.run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_login:
                Intent i = new Intent(this, LoginActivity.class);
                i.putExtra("Calling_Activity", TAG);
                startActivity(i);
                break;
            case R.id.action_sign_up:
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.action_logout:
                ParseUser.logOut();
                break;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*@Override
    public void onSensorChanged(SensorEvent event) {

        // alpha is calculated as tau / (tau + dt)
        // with t, the low-pass filter's time-constant
        // and dt, the event delivery rate

        if (t!=0){
            dt = (System.nanoTime()/1000000000d) - t;
            Log.i(TAG, Double.toString(dt));
            tau = 1f/(2*Math.PI*fc);
            Log.i(TAG, Double.toString(tau));
        }
        t = System.nanoTime()/1000000000d;

        final double alpha = tau / (tau + dt);
        Log.i(TAG, "alpha: "+alpha);
        Log.i(TAG, "gravity[2]: "+gravity[2]);
        Log.i(TAG, "event.values[2]: "+event.values[2]);

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
    }*/

    @Override
    protected void onPause() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHandler.removeCallbacks(sampler);
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.i(TAG, "sensor changed");
        raw = event.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
