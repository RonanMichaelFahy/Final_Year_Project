package com.example.ronan.final_year_project;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
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

import com.example.ronan.final_year_project.BluetoothLeService.LocalBinder;
import com.google.gson.Gson;

public class PrescriptionSetupActivity extends Activity {

    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;
    private Object mBluetoothGatt;
    private android.bluetooth.BluetoothDevice mBluetoothDevice;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("Service connected");
            LocalBinder localBinder = (LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            mBound = true;

            if (mBluetoothDevice!=null)
                mBluetoothGatt = mBluetoothDevice.connectGatt(PrescriptionSetupActivity.this, false, mBluetoothLeService.mGattCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_setup);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("device", "");
        mBluetoothDevice = gson.fromJson(json, BluetoothDevice.class);

        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        final Button pairDevicesButton = (Button) findViewById(R.id.pair_devices_button);
        pairDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrescriptionSetupActivity.this, DeviceScanActivity.class);
                startActivity(intent);
            }
        });

        final Button intensityLevelsButton = (Button) findViewById(R.id.intensity_levels_button);
        intensityLevelsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrescriptionSetupActivity.this, IntensitySetupActivity.class);
                startActivity(intent);
            }
        });

        final Button cyclicModeButton = (Button) findViewById(R.id.cyclic_mode_button);
        cyclicModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrescriptionSetupActivity.this, CyclicModeActivity.class);
                startActivity(intent);
            }
        });

        final Button testRunButton = (Button) findViewById(R.id.test_run_button);
        testRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrescriptionSetupActivity.this, TestRunActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prescription_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onPause();
    }
}
