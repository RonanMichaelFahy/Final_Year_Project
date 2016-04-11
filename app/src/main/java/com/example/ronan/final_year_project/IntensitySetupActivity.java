package com.example.ronan.final_year_project;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ronan.final_year_project.BluetoothLeService.LocalBinder;
import com.parse.ParseUser;

import java.util.UUID;

public class IntensitySetupActivity extends Activity {

    private static final String TAG = IntensitySetupActivity.class.getSimpleName();
    private int intensity;
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

        final SharedPreferences mSharedPreferences = getSharedPreferences("Stimulation_Parameters", MODE_PRIVATE);
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
                UUID serviceUUID = UUID.fromString("a6322521-0000-2000-9000-001122001122");
                UUID characteristicUUID = UUID.fromString("20000010-0000-2000-9000-001122001122");
                boolean written = mBluetoothLeService.writeCharacteristic(serviceUUID, characteristicUUID, new byte[]{(byte)intensity});
                Log.i(TAG, "intensity written: "+written);

                android.os.Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        UUID serviceUUID = UUID.fromString("a6322521-0000-2000-9000-001122001122");
                        UUID characteristicUUID = UUID.fromString("01000030-0000-2000-9000-001122001122");
                        boolean written = mBluetoothLeService.writeCharacteristic(serviceUUID, characteristicUUID, new byte[]{4});
                        Log.i(TAG, "stimulation written: " + written);
                    }
                }, 1000);
            }
        });

        final Button sensoryThresholdButton = (Button) findViewById(R.id.setSensoryThresholdButton);
        sensoryThresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mEditor.putString("Sensory_Threshold", String.valueOf(intensity));
                mEditor.apply();*/

                mEditor.putInt("sensory_threshold", intensity);
                mEditor.apply();
            }
        });

        final Button motorThresholdButton = (Button) findViewById(R.id.set_motor_threshold_button);
        motorThresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mEditor.putString("Motor_Threshold", String.valueOf(intensity));
                mEditor.apply();*/

                mEditor.putInt("motor_threshold", intensity);
                mEditor.apply();
            }
        });

        final Button painThresholdButton = (Button) findViewById(R.id.set_pain_threshold_button);
        painThresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mEditor.putString("Pain_Threshold", String.valueOf(intensity));
                mEditor.apply();*/

                mEditor.putInt("pain_threshold", intensity);
                mEditor.apply();
            }
        });

        final Button balancedDorsiflexionLevelButton = (Button) findViewById(R.id.set_balanced_dorsiflexion_level_button);
        balancedDorsiflexionLevelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*mEditor.putString("Balanced_Dorsiflexion_Level", String.valueOf(intensity));
                mEditor.apply();*/

                mEditor.putInt("balanced_dorsiflexion", intensity);
                mEditor.apply();
            }
        });

        final Button operatingIntensityButton = (Button) findViewById(R.id.operating_intensity_button);
        operatingIntensityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intensity < Integer.parseInt(mSharedPreferences.getString("Pain_Threshold", "0"))) {
                    UUID serviceUUID = UUID.fromString("a6322521-0000-2000-9000-001122001122");
                    UUID characteristicUUID = UUID.fromString("20000010-0000-2000-9000-001122001122");
                    boolean written = mBluetoothLeService.writeCharacteristic(serviceUUID, characteristicUUID, new byte[]{(byte) intensity});
                    Log.i(TAG, "Written: "+written);

                    mEditor.putInt("operating_intensity", intensity);
                    mEditor.apply();
                } else {
                    Toast t = Toast.makeText(IntensitySetupActivity.this, "The selected intensity is greater than the pain threshold", Toast.LENGTH_SHORT);
                    t.show();
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
