package com.example.ronan.final_year_project;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.parse.ParseUser;

import java.util.UUID;

public class TestRunActivity extends FragmentActivity {

    private static final String TAG = TestRunActivity.class.getSimpleName();
    private byte thresholdValue;

    public enum StimulationParameters {
        RAMP_UP_TIME, RAMP_DOWN_TIME, PULSE_WIDTH, PULSE_FREQUENCY, THRESHOLD
    }
    private StimulationParameters mStimulationParameters;
    private byte rampUpTimeValue;
    private byte rampDownTimeValue;
    private byte pulseWidthValue;
    private byte pulseFrequencyValue;
    private boolean mBound;
    public BluetoothLeService mBluetoothLeService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connected");
            BluetoothLeService.LocalBinder localBinder = (BluetoothLeService.LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            mBound = true;

            UUID serviceUUID = UUID.fromString("a6322521-0000-2000-9000-1122334455ff");
            UUID characteristicUUID = UUID.fromString("60010000-0000-2000-9000-1122334455ff");
            boolean written = mBluetoothLeService.writeCharacteristic(serviceUUID, characteristicUUID, new byte[]{1});
            Log.i(TAG, "runDFS set: "+written);
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

        SharedPreferences sharedPreferences = getSharedPreferences("Stimulation_Parameters", MODE_PRIVATE);

        final SeekBar pulseWidthSeekBar = (SeekBar) findViewById(R.id.pulse_width_seek_bar);
        final SeekBar pulseFrequencySeekBar = (SeekBar) findViewById(R.id.pulse_frequency_seek_bar);
        final SeekBar rampUpTimeSeekBar = (SeekBar) findViewById(R.id.ramp_up_time_seek_bar);
        final SeekBar rampDownTimeSeekBar = (SeekBar) findViewById(R.id.ramp_down_time_seek_bar);
        final SeekBar thresholdSeekBar = (SeekBar) findViewById(R.id.threshold_seek_bar);

        pulseWidthSeekBar.setProgress(sharedPreferences.getInt("pulse_width", 0));
        pulseFrequencySeekBar.setProgress(sharedPreferences.getInt("pulse_width", 0));
        rampUpTimeSeekBar.setProgress(sharedPreferences.getInt("ramp_up_time", 0));
        rampDownTimeSeekBar.setProgress(sharedPreferences.getInt("ramp_down_time", 0));

        final Button savePulseWidthButton = (Button) findViewById(R.id.save_pulse_width_button);
        savePulseWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final SeekBar pulseWidthSeekBar = (SeekBar) findViewById(R.id.pulse_width_seek_bar);
                setPulseWidthValue((byte) pulseWidthSeekBar.getProgress());
                TestRunActivity.this.mStimulationParameters = StimulationParameters.PULSE_WIDTH;
                //setRamp(false);
                //setUpOrFrequency(false);
                ConfirmationFragment confirmation = new ConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
            }
        });

        final Button savePulseFrequencyButton = (Button) findViewById(R.id.save_pulse_frequency_button);
        savePulseFrequencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final SeekBar pulseFrequencySeekBar = (SeekBar) findViewById(R.id.pulse_frequency_seek_bar);
                setPulseFrequencyValue((byte) pulseFrequencySeekBar.getProgress());
                TestRunActivity.this.mStimulationParameters = StimulationParameters.PULSE_FREQUENCY;
                //setRamp(false);
                //setUpOrFrequency(true);
                ConfirmationFragment confirmation = new ConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
            }
        });

        final Button saveRampUpTimeButton = (Button) findViewById(R.id.save_ramp_up_time_button);
        saveRampUpTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final SeekBar rampUpTimeSeekBar = (SeekBar) findViewById(R.id.ramp_up_time_seek_bar);
                setRampUpTimeValue((byte) rampUpTimeSeekBar.getProgress());
                TestRunActivity.this.mStimulationParameters = StimulationParameters.RAMP_UP_TIME;
                //setRamp(true);
                //setUpOrFrequency(true);
                ConfirmationFragment confirmation = new ConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
            }
        });

        final Button saveRampDownTimeButton = (Button) findViewById(R.id.save_ramp_down_time_button);
        saveRampDownTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //final SeekBar rampDownTimeSeekBar = (SeekBar) findViewById(R.id.ramp_down_time_seek_bar);
                setRampDownTimeValue((byte) rampDownTimeSeekBar.getProgress());
                TestRunActivity.this.mStimulationParameters = StimulationParameters.RAMP_DOWN_TIME;
                //setRamp(true);
                //setUpOrFrequency(false);
                ConfirmationFragment confirmation = new ConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
            }
        });

        final Button saveThresholdButton = (Button) findViewById(R.id.save_threshold_button);
        saveThresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setThresholdValue((byte) thresholdSeekBar.getProgress());
                TestRunActivity.this.mStimulationParameters = StimulationParameters.THRESHOLD;
                ConfirmationFragment confirmation = new ConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
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
            mBound = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            Log.i(TAG, "Bound: "+mBound);
        }
        super.onResume();
    }

    public void setRampUpTimeValue(byte rampUpTimeValue) {
        this.rampUpTimeValue = rampUpTimeValue;
    }

    public void setRampDownTimeValue(byte rampDownTimeValue) {
        this.rampDownTimeValue = rampDownTimeValue;
    }

    public void setPulseWidthValue(byte pulseWidthValue) {
        this.pulseWidthValue = pulseWidthValue;
    }

    public void setPulseFrequencyValue(byte pulseFrequencyValue) {
        this.pulseFrequencyValue = pulseFrequencyValue;
    }

    public byte getRampUpTimeValue() {
        return rampUpTimeValue;
    }

    public byte getRampDownTimeValue() {
        return rampDownTimeValue;
    }

    public byte getPulseFrequencyValue() {
        return pulseFrequencyValue;
    }

    public byte getPulseWidthValue() {
        return pulseWidthValue;
    }

    public StimulationParameters getStimulationParameters() {
        return this.mStimulationParameters;
    }

    public void setThresholdValue(byte thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public byte getThresholdValue() {
        return this.thresholdValue;
    }
}
