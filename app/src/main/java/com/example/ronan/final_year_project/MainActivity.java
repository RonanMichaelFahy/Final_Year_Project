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
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.Parse;
import com.parse.ParseUser;

import java.util.UUID;

public class MainActivity extends Activity {

    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("Service connected");
            BluetoothLeService.LocalBinder localBinder = (BluetoothLeService.LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            mBound = true;

            if (mBluetoothDevice!=null)
                mBluetoothGatt = mBluetoothDevice.connectGatt(MainActivity.this, false, mBluetoothLeService.mGattCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // [Optional] Power your app with Local Datastore. For more info, go to
        // https://parse.com/docs/android/guide#local-datastore
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        //Get BluetoothDevice object
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("device", "");
        mBluetoothDevice = gson.fromJson(json, BluetoothDevice.class);
        if (mBluetoothDevice != null) {
            System.out.println("Connected to "+mBluetoothDevice.getName());
        }

        //Bind to BluetoothService object
        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

        final Button run_button = (Button) findViewById(R.id.run_button);
        run_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothGatt != null) {
                    // TODO: 29/02/2016 get actual UUID
                    UUID uuid = new UUID(0,0);
                    BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
                    mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                }

                else {
                    Toast toast = Toast.makeText(MainActivity.this, "Please connect to CueStim device first", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });

        final Button pair_devices_button = (Button) findViewById(R.id.pair_devices_button_main);
        pair_devices_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_sign_up) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_logout) {
            ParseUser.getCurrentUser().logOut();
        }

        return super.onOptionsItemSelected(item);
    }
}