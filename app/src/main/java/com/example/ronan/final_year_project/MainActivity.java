package com.example.ronan.final_year_project;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseUser;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final Button run_button = (Button) findViewById(R.id.run_button);
        run_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("MainActivity", "Run button clicked");
                if (mBluetoothLeService != null) {
                    // TODO: 29/02/2016 get actual UUID to set deviceState to RUN_MODE
                    boolean written = mBluetoothLeService.writeCharacteristic(null, null, new byte[0]);
                    Log.i(TAG, "Written: "+written);
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

        if (id == R.id.action_login){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_sign_up){
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_logout){
            ParseUser.getCurrentUser().logOut();
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
        }
        super.onPause();
    }
}