package com.example.ronan.final_year_project;

import android.app.Activity;
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

import com.example.ronan.final_year_project.BluetoothLeService.LocalBinder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PrescriptionSetupActivity extends Activity {

    private static final String TAG = PrescriptionSetupActivity.class.getSimpleName();
    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connected");
            LocalBinder localBinder = (LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            mBound = true;

            // TODO: 29/02/2016 get actual UUID to set deviceState to default
            UUID uuid = new UUID(0, 0);
            BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
            mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristic);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service disconnected");
            mBound = false;
        }
    };
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prescription_setup);

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

                if (mBluetoothLeService != null) {
                    // TODO: 29/02/2016 get actual UUID to set deviceState to INTENSITY_SETUP
                    //Set deviceState in firmware to INTENSITY_SETUP
                    UUID uuid = new UUID(0, 0);
                    BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristic);

                    //Start intensity setup activity
                    Intent intent = new Intent(PrescriptionSetupActivity.this, IntensitySetupActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(PrescriptionSetupActivity.this, "Please pair with CueStim device first", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        final Button cyclicModeButton = (Button) findViewById(R.id.cyclic_mode_button);
        cyclicModeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Set deviceState in firmware to CYCLIC_MODE
                if (mBluetoothLeService != null) {
                    // TODO: 29/02/2016 get actual UUID for setting deviceState to CYCLIC_MODE
                    UUID uuid = new UUID(0, 0);
                    BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristic);

                    Intent intent = new Intent(PrescriptionSetupActivity.this, CyclicModeActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(PrescriptionSetupActivity.this, "Please pair with CueStim device first", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        final Button testRunButton = (Button) findViewById(R.id.test_run_button);
        testRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBluetoothLeService != null) {
                    // TODO: 29/02/2016 get actual UUID to set deviceState to RUN_MODE
                    //Set deviceState in firmware to RUN_MODE
                    UUID uuid = new UUID(0, 0);
                    BluetoothGattCharacteristic bluetoothGattCharacteristic = new BluetoothGattCharacteristic(uuid, 0, 0);
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristic);

                    //Start test run activity
                    Intent intent = new Intent(PrescriptionSetupActivity.this, TestRunActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(PrescriptionSetupActivity.this, "Please pair with CueStim device first", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        final Button uploadUsageDataButton = (Button) findViewById(R.id.upload_usage_data_button);
        uploadUsageDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Upload the stimulation parameters which are stored locally
                mSharedPreferences = getSharedPreferences("Stimulation_Parameters", MODE_PRIVATE);
                Map<String, ?> parameters = mSharedPreferences.getAll();
                Log.i(TAG, "Parameters to be uploaded: "+parameters);
                for (final Map.Entry<String,?> entry : parameters.entrySet()) {
                    Log.i(TAG, "Entry: "+entry.toString());
                    ParseQuery<ParseObject> query = new ParseQuery("stimulation_parameters");
                    query.whereEqualTo("user", ParseUser.getCurrentUser());
                    query.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            Log.i(TAG, "Writing objects");
                            if (objects != null && objects.size() != 0) { //if there is an existing entry for this user
                                Log.i(TAG, "Updating existing entry for this user");
                                objects.get(0).put(entry.getKey(), entry.getValue());
                                objects.get(0).saveInBackground();
                            } else { //if there is no existing entry for this user
                                Log.i(TAG, "Creating new entry for this user");
                                ParseObject parseObject = new ParseObject("stimulation_parameters");
                                parseObject.put("user", ParseUser.getCurrentUser());
                                parseObject.put(entry.getKey(), entry.getValue());
                                parseObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null){
                                            Log.i(TAG, "Object saved");
                                            Toast toast = Toast.makeText(PrescriptionSetupActivity.this, "Object saved", Toast.LENGTH_SHORT);
                                            toast.show();
                                        } else {
                                            Log.e(TAG, "ParseException");
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
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
        } else if (id == R.id.action_logout) {
            ParseUser.getCurrentUser().logOut();
            /*Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);*/
            finish();
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
}
