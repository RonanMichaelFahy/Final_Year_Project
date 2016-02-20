package com.example.ronan.final_year_project;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ronan.final_year_project.BluetoothLeService.LocalBinder;
import com.google.gson.Gson;

import java.util.UUID;

public class IntensitySetupActivity extends Activity {

    private int intensity;
    private BluetoothDevice mBluetoothDevice;
    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;
    private BluetoothGatt mBluetoothGatt;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocalBinder localBinder = (LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            mBound = true;

            if (mBluetoothDevice!=null)
                mBluetoothGatt = mBluetoothDevice.connectGatt(IntensitySetupActivity.this, false, mBluetoothLeService.mGattCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intensity_setup);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("device", "");
        mBluetoothDevice = gson.fromJson(json, BluetoothDevice.class);

        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

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
                intensityLevel.setText(intensity+"%");
            }
        });

        final Button testButton = (Button) findViewById(R.id.test);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 20/02/2016 figure out what most and least significant bits of UUID are
                UUID uuid = new UUID(10000001, 001122001122);
                BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
                mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristic);
            }
        });

        final Button sensoryThresholdButton = (Button) findViewById(R.id.setSensoryThresholdButton);
        sensoryThresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO: 20/02/2016 figure out what most and least significant bits of UUID are
                UUID uuid = new UUID(0, 0);
                BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
                bluetoothGattCharacteristic.setValue(Integer.toString(intensity));
                mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristic);

                /*UUID uuid = new UUID(42, 66);
                BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
                bluetoothGattCharacteristic.setValue(Integer.toString(intensity));
                mBluetoothLeService.writeCharacteristic(new BluetoothGattCharacteristic(uuid,0,0));
                ParseQuery<ParseObject> query = new ParseQuery("stimulation_parameters");
                query.whereEqualTo("user", ParseUser.getCurrentUser());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        objects.get(0).put("sensory_threshold", intensity);
                        objects.get(0).saveInBackground();
                    }
                });*/
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void setIntensity(int intensity){
        if(intensity >= 0 && intensity <= 100){
            this.intensity = intensity;
        }
    }

    @Override
    public void onPause() {
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onPause();
    }
}
