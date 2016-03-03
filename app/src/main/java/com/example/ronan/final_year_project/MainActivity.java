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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences mSharedPreferences;
    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mBluetoothDevice;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connected");
            BluetoothLeService.LocalBinder localBinder = (BluetoothLeService.LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            Log.i(TAG, "mBluetoothLeService: "+mBluetoothLeService.toString());
            mBound = true;

            /*if (mBluetoothDevice!=null)
                mBluetoothGatt = mBluetoothDevice.connectGatt(MainActivity.this, false, mBluetoothLeService.mGattCallback);*/
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

        // [Optional] Power your app with Local Datastore. For more info, go to
        // https://parse.com/docs/android/guide#local-datastore
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);

        final Button run_button = (Button) findViewById(R.id.run_button);
        run_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("MainActivity", "Run button clicked");
                if (mBluetoothLeService != null) {
                    // TODO: 29/02/2016 get actual UUID to set deviceState to RUN_MODE
                    UUID uuid = new UUID(0,0);
                    BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
                    boolean written = mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristic);
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

        final Button uploadUsageDataButton = (Button) findViewById(R.id.upload_usage_data_button);
        uploadUsageDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Upload the stimulation parameters which are stored locally
                mSharedPreferences = getSharedPreferences("Stimulation_Parameters", MODE_PRIVATE);
                Map<String, ?> parameters = mSharedPreferences.getAll();
                for (final Map.Entry<String,?> entry : parameters.entrySet()) {
                    Log.i(TAG, entry.getKey()+" : "+entry.getValue());
                    ParseQuery<ParseObject> query = new ParseQuery("stimulation_parameters");
                    query.whereEqualTo("user", ParseUser.getCurrentUser());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            objects.get(0).put(entry.getKey(), entry.getValue());
                            objects.get(0).saveInBackground();
                        }
                    });
                }
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
        } else if (id == R.id.action_sign_up) {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_logout) {
            ParseUser.getCurrentUser().logOut();
        } else if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.i("MainActivity", "Resumed");
        //Get BluetoothDevice object
        /*SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("device", "");
        Log.i("MainActivity", "json: "+json);
        mBluetoothDevice = gson.fromJson(json, BluetoothDevice.class);*/

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