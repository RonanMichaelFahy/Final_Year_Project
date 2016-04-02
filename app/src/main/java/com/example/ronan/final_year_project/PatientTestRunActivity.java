package com.example.ronan.final_year_project;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class PatientTestRunActivity extends FragmentActivity {

    private static int rampUpTimeValue;
    private static int rampDownTimeValue;
    private static boolean up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_test_run);

        final Button saveRampUpTime = (Button) findViewById(R.id.saveRampUpTime);
        saveRampUpTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SeekBar rampUpTimeSeekBar = (SeekBar) findViewById(R.id.rampUpTimeSeekBar);
                setRampUpTimeValue(rampUpTimeSeekBar.getProgress());
                setUp(true);
                ConfirmationFragment confirmation = new ConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
            }
        });

        final Button saveRampDownTime = (Button) findViewById(R.id.saveRampDownTime);
        saveRampDownTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SeekBar rampDownTimeSeekBar = (SeekBar) findViewById(R.id.rampDownTimeSeekBar);
                setRampDownTimeValue(rampDownTimeSeekBar.getProgress());
                setUp(false);
                ConfirmationFragment confirmation = new ConfirmationFragment();
                confirmation.show(getSupportFragmentManager(), "fragment_confirmation");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_test_run, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public static int getRampUpTimeValue() {
        return rampUpTimeValue;
    }

    public static void setRampUpTimeValue(int rampUpTimeValue) {
        PatientTestRunActivity.rampUpTimeValue = rampUpTimeValue;
    }

    public static int getRampDownTimeValue() {
        return rampDownTimeValue;
    }

    public static void setRampDownTimeValue(int rampDownTimeValue) {
        PatientTestRunActivity.rampDownTimeValue = rampDownTimeValue;
    }

    public static boolean isUp() {
        return PatientTestRunActivity.up;
    }

    public static void setUp(boolean up) {
        PatientTestRunActivity.up = up;
    }
}
