package com.example.ronan.final_year_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class ClinicianConfirmationFragment extends DialogFragment {

    private static final String TAG = ClinicianConfirmationFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("Stimulation_Parameters", Context.MODE_PRIVATE);
        final SharedPreferences.Editor mEditor = mSharedPreferences.edit();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(TestRunActivity.isRamp()){
            if(TestRunActivity.isUpOrFrequency()){
                builder.setMessage("You have chosen a value of "+TestRunActivity.getRampUpTimeValue()+" for ramp-up time.")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "Setting ramp-up time");
                                mEditor.putInt("Ramp_Up_Time", TestRunActivity.getRampUpTimeValue());
                                mEditor.commit();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "Cancelled");
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();
            }
            else {
                builder.setMessage("You have chosen a value of "+TestRunActivity.getRampDownTimeValue()+" for ramp-down time.")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "Setting ramp-down time");
                                mEditor.putInt("Ramp_Down_Time", TestRunActivity.getRampDownTimeValue());
                                mEditor.commit();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "Cancelled");
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();
            }
        }
        else if (TestRunActivity.isUpOrFrequency()) {
            builder.setMessage("You have chosen a value of "+TestRunActivity.getPulseFrequencyValue()+" for pulse frequency.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i(TAG, "Setting pulse frequency");
                            mEditor.putInt("Pulse_Frequency", TestRunActivity.getPulseFrequencyValue());
                            mEditor.commit();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i(TAG, "Cancelled");
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
        else {
            builder.setMessage("You have chosen a value of "+TestRunActivity.getPulseWidthValue()+" for pulse width.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i(TAG, "Setting pulse width: " + TestRunActivity.getPulseWidthValue());
                            mEditor.putInt("Pulse_Width", TestRunActivity.getPulseWidthValue());
                            boolean written = mEditor.commit();
                            Log.i(TAG, "Written: "+written);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.i(TAG, "Cancelled");
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
