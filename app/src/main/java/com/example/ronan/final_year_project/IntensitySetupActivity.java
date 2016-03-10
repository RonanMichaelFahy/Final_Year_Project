package com.example.ronan.final_year_project;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ronan.final_year_project.BluetoothLeService.LocalBinder;

import java.nio.ByteBuffer;
import java.util.UUID;

public class IntensitySetupActivity extends Activity {

    private static final String TAG = IntensitySetupActivity.class.getSimpleName();
    private int intensity;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connected");
            LocalBinder localBinder = (LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service disconnected");
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intensity_setup);

        mSharedPreferences = getSharedPreferences("Stimulation_Parameters", MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        final TextView intensityLevel = (TextView) findViewById(R.id.intensity_level);
        intensity = Integer.parseInt(intensityLevel.getText().subSequence(0, intensityLevel.getText().length()-1).toString());

        final Button plusButton = (Button) findViewById(R.id.plus_button);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntensity(intensity + 1);
                intensityLevel.setText(intensity+"%");
            }
        });

        final Button minusButton = (Button) findViewById(R.id.minus_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntensity(intensity - 1);
                intensityLevel.setText(intensity + "%");
            }
        });

        final Button testButton = (Button) findViewById(R.id.test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 20/02/2016 figure out what most and least significant bits of UUID are
                boolean written = mBluetoothLeService.writeCharacteristic(null, null, new byte[0]);
                Log.i(TAG, "Written: "+written);
            }
        });

        final Button sensoryThresholdButton = (Button) findViewById(R.id.setSensoryThresholdButton);
        sensoryThresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UUID serviceUUID = UUID.fromString("a6322521-0000-2000-9000-001122001122");
                // TODO: 20/02/2016 include characteristic UUID when characteristic has been created in device firmware
                UUID characteristicUUID = UUID.fromString("");
                boolean written = mBluetoothLeService.writeCharacteristic(serviceUUID, characteristicUUID, ByteBuffer.allocate(4).putInt(intensity).array());
                Log.i(TAG, "Written: "+written);

                mEditor.putString("Sensory_Threshold", Integer.toString(intensity));
                mEditor.commit();
            }
        });

        final Button motorThresholdButton = (Button) findViewById(R.id.set_motor_threshold_button);
        motorThresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID serviceUUID = UUID.fromString("a6322521-0000-2000-9000-001122001122");
                // TODO: 20/02/2016 include characteristic UUID when characteristic has been created in device firmware
                UUID characteristicUUID = UUID.fromString("");
                boolean written = mBluetoothLeService.writeCharacteristic(serviceUUID, characteristicUUID,
                        ByteBuffer.allocate(4).putInt(intensity).array());
                Log.i(TAG, "Written: "+written);

                mEditor.putString("Motor_Threshold", Integer.toString(intensity));
                mEditor.commit();
            }
        });

        final Button painThresholdButton = (Button) findViewById(R.id.set_pain_threshold_button);
        painThresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID serviceUUID = UUID.fromString("a6322521-0000-2000-9000-001122001122");
                // TODO: 20/02/2016 include characteristic UUID when characteristic has been created in device firmware
                UUID characteristicUUID = UUID.fromString("");
                boolean written = mBluetoothLeService.writeCharacteristic(serviceUUID, characteristicUUID,
                        ByteBuffer.allocate(4).putInt(intensity).array());
                Log.i(TAG, "Written: "+written);

                mEditor.putString("Pain_Threshold", Integer.toString(intensity));
                mEditor.commit();
            }
        });

        final Button balancedDorsiflexionLevelButton = (Button) findViewById(R.id.set_balanced_dorsiflexion_level_button);
        balancedDorsiflexionLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID serviceUUID = UUID.fromString("a6322521-0000-2000-9000-001122001122");
                // TODO: 20/02/2016 include characteristic UUID when characteristic has been created in device firmware
                UUID characteristicUUID = UUID.fromString("");
                boolean written = mBluetoothLeService.writeCharacteristic(serviceUUID, characteristicUUID,
                        ByteBuffer.allocate(4).putInt(intensity).array());
                Log.i(TAG, "Written: "+written);

                mEditor.putString("Balanced_Dorsiflexion_Level", Integer.toString(intensity));
                mEditor.commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inensity_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home){
            Intent intent = new Intent(this, PrescriptionSetupActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        Log.i(TAG, "Paused");
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "Resumed");
        if (BluetoothLeService.running) {
            Log.i(TAG, "Attempting to bind to service");
            Intent intent = new Intent(this, BluetoothLeService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        super.onResume();
    }

    public void setIntensity(int intensity){
        if(intensity >= 0 && intensity <= 100){
            this.intensity = intensity;
        }
    }
}
