package com.example.ronan.final_year_project;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class TestRunActivity extends FragmentActivity {

    private static final String TAG = TestRunActivity.class.getSimpleName();
    private Button savePulseWidthButton;
    private Button savePulseFrequencyButton;
    private Button saveRampUpTimeButton;
    private Button saveRampDownTimeButton;

    private static boolean ramp;
    private static boolean upOrFrequency;
    private static int rampUpTimeValue;
    private static int rampDownTimeValue;
    private static int pulseWidthValue;
    private static int pulseFrequencyValue;
    private boolean mBound;
    public BluetoothLeService mBluetoothLeService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connected");
            BluetoothLeService.LocalBinder localBinder = (BluetoothLeService.LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            mBound = true;

            // TODO: 29/02/2016 get actual UUID to set deviceState to default
            boolean written = mBluetoothLeService.writeCharacteristic(null, null, new byte[0]);
            Log.i(TAG, "Written: "+written);
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
        setContentView(R.layout.activity_test_run);

        savePulseWidthButton = (Button) findViewById(R.id.save_pulse_width_button);
        savePulseWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SeekBar pulseWidthSeekBar = (SeekBar) findViewById(R.id.pulse_width_seek_bar);
                setPulseWidthValue(pulseWidthSeekBar.getProgress());
                setRamp(false);
                setUpOrFrequency(false);
                ClinicianConfirmationFragment confirmation = new ClinicianConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
            }
        });

        savePulseFrequencyButton = (Button) findViewById(R.id.save_pulse_frequency_button);
        savePulseFrequencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SeekBar pulseFrequencySeekBar = (SeekBar) findViewById(R.id.pulse_frequency_seek_bar);
                setPulseFrequencyValue(pulseFrequencySeekBar.getProgress());
                setRamp(false);
                setUpOrFrequency(true);
                ClinicianConfirmationFragment confirmation = new ClinicianConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
            }
        });

        saveRampUpTimeButton = (Button) findViewById(R.id.save_ramp_up_time_button);
        saveRampUpTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SeekBar rampUpTimeSeekBar = (SeekBar) findViewById(R.id.ramp_up_time_seek_bar);
                setRampUpTimeValue(rampUpTimeSeekBar.getProgress());
                setRamp(true);
                setUpOrFrequency(true);
                ClinicianConfirmationFragment confirmation = new ClinicianConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
            }
        });

        saveRampDownTimeButton = (Button) findViewById(R.id.save_ramp_down_time_button);
        saveRampDownTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SeekBar rampDownTimeSeekBar = (SeekBar) findViewById(R.id.ramp_down_time_seek_bar);
                setRampDownTimeValue(rampDownTimeSeekBar.getProgress());
                setRamp(true);
                setUpOrFrequency(false);
                ClinicianConfirmationFragment confirmation = new ClinicianConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_run, menu);
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

    public static void setRampUpTimeValue(int rampUpTimeValue) {
        TestRunActivity.rampUpTimeValue = rampUpTimeValue;
    }

    public static void setRampDownTimeValue(int rampDownTimeValue) {
        TestRunActivity.rampDownTimeValue = rampDownTimeValue;
    }

    public static void setRamp(boolean ramp) {
        TestRunActivity.ramp = ramp;
    }

    public static boolean isRamp() {
        return TestRunActivity.ramp;
    }

    public static void setUpOrFrequency(boolean upOrFrequency) {
        TestRunActivity.upOrFrequency = upOrFrequency;
    }

    public static boolean isUpOrFrequency() {
        return TestRunActivity.upOrFrequency;
    }

    public static void setPulseWidthValue(int pulseWidthValue) {
        TestRunActivity.pulseWidthValue = pulseWidthValue;
    }

    public static void setPulseFrequencyValue(int pulseFrequencyValue) {
        TestRunActivity.pulseFrequencyValue = pulseFrequencyValue;
    }

    public static int getRampUpTimeValue() {
        return rampUpTimeValue;
    }

    public static int getRampDownTimeValue() {
        return rampDownTimeValue;
    }

    public static int getPulseFrequencyValue() {
        return pulseFrequencyValue;
    }

    public static int getPulseWidthValue() {
        return pulseWidthValue;
    }
}
