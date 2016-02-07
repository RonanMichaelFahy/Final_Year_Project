package com.example.ronan.final_year_project;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class IntensitySetupActivity extends Activity {

    private int intensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intensity_setup);

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
                intensityLevel.setText(intensity+"%");
            }
        });

        final Button sensoryThresholdButton = (Button) findViewById(R.id.setSensoryThresholdButton);
        sensoryThresholdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ParseQuery<ParseObject> query = new ParseQuery("stimulation_parameters");
                //query.whereEqualTo("user", ParseUser.getCurrentUser());
                //query.findInBackground(new FindCallback<ParseObject>() {
                //    @Override
                //    public void done(List<ParseObject> objects, ParseException e) {
                //        objects.get(0).put("sensory_threshold", intensity);
                //        objects.get(0).saveInBackground();
                //    }
                //});
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inensity_setup, menu);
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

    public void setIntensity(int intensity){
        if(intensity >= 0 && intensity <= 100){
            this.intensity = intensity;
        }
    }
}
