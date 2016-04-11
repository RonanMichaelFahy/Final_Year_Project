package com.example.ronan.final_year_project;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class CyclicModeActivity extends Activity {

    private static final String TAG = CyclicModeActivity.class.getSimpleName();
    private BluetoothLeService mBluetoothLeService;
    private boolean mBound;
    private Timer mTimer;
    private int period = 8000; // default value of 8 seconds
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothLeService.LocalBinder localBinder = (BluetoothLeService.LocalBinder) service;
            mBluetoothLeService = localBinder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cyclic_mode);

        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);

        final Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound) {
                    mTimer = new Timer();
                    mTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            new Stimulator().execute();
                        }
                    }, 0, period);
                }
            }
        });

        final Button stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimer.cancel();
            }
        });

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.np);
        numberPicker.setMaxValue(20);
        numberPicker.setMinValue(1);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                period = newVal*1000;
            }
        });
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this, BluetoothLeService.class);
        mBound = bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mTimer != null)
            mTimer.cancel();
        if (mBound)
            unbindService(mConnection);
        mBound = false;
        super.onPause();
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

    private class Stimulator extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            UUID serviceUUID = UUID.fromString("a6322521-0000-2000-9000-001122001122");
            UUID characteristicUUID = UUID.fromString("01000030-0000-2000-9000-001122001122");
            boolean written = mBluetoothLeService.writeCharacteristic(serviceUUID, characteristicUUID, new byte[]{4});
            Log.i(TAG, "Written: " + written);
            return null;
        }
    }
}