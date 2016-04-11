package com.example.ronan.final_year_project;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.parse.ParseUser;

import java.util.UUID;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connected");
            BluetoothLeService.LocalBinder localBinder = (BluetoothLeService.LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            Log.i(TAG, "mBluetoothLeService: "+mBluetoothLeService.toString());
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service disconnected");
        }
    };
    private UUID ronanServiceUUID = UUID.fromString("a6322521-0000-2000-9000-1122334455FF");
    private boolean runDfsSet = false;
    private BluetoothGattCharacteristic characteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button run_button = (Button) findViewById(R.id.run_button);
        run_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //if (mBluetoothLeService != null && mBound) {

                    Intent intent = new Intent(MainActivity.this, RunActivity.class);
                    startActivity(intent);

                    /*if(!runDfsSet) {

                        List<BluetoothGattService> services = mBluetoothLeService.getSupportedGattServices();
                        for (BluetoothGattService service : services) {
                            Log.i(TAG, service.getUuid().toString());
                            if (service.getUuid().toString().equals("a6322521-0000-2000-9000-1122334455ff")) {
                                for (BluetoothGattCharacteristic c : service.getCharacteristics()) {
                                    Log.i(TAG, c.getUuid().toString());
                                    if (c.getUuid().toString().equals("60010000-0000-2000-9000-1122334455ff")) {
                                        Log.i(TAG, "Found it");
                                        characteristic = c;
                                    }
                                }
                            }
                        }

                        System.out.println((characteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                            boolean written = mBluetoothLeService.writeCharacteristic(characteristic, new byte[]{1});
                            Log.i(TAG, "written: "+written);
                        }

                    } else {
                        UUID runCharacteristicUUID = UUID.fromString("60010000-0000-2000-9000-1122334455FF");
                        runDfsSet = !mBluetoothLeService.writeCharacteristic(ronanServiceUUID, runCharacteristicUUID, new byte[]{0});
                        Log.i(TAG, "runDFS reset: " + runDfsSet);
                        run_button.setText(R.string.stop);
                    }*/
                //}

                /*else {
                    Toast toast = Toast.makeText(MainActivity.this, R.string.not_paired, Toast.LENGTH_LONG);
                    toast.show();
                }*/
            }
        });

        final Button pair_devices_button = (Button) findViewById(R.id.pair_devices_button_main);
        pair_devices_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                intent.putExtra("Calling_Activity", TAG);
                startActivity(intent);
            }
        });

        /*final Button run_mode_button = (Button) findViewById(R.id.runModeButton);
        run_mode_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewOutputActivity.class);
                startActivity(intent);
            }
        });*/
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

        if (id == R.id.action_login){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("Calling_Activity", TAG);
            startActivity(intent);
        } else if (id == R.id.action_sign_up){
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_logout){
            ParseUser.logOut();
        } else if (id == R.id.action_settings){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.i("MainActivity", "Resumed");

        if (BluetoothLeService.running) {
            Log.i(TAG, "Attempting to bind to service");
            Intent intent = new Intent(this, BluetoothLeService.class);
            mBound = bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        } else
            Log.i(TAG, "BluetoothService has not been started");

        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG, "Paused");
        if (mBluetoothLeService != null) {
            unbindService(mServiceConnection);
            mBound = false;
        }
        super.onPause();
    }
}