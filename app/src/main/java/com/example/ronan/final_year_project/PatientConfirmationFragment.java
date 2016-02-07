package com.example.ronan.final_year_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PatientConfirmationFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(PatientTestRunActivity.isUp()){
            builder.setMessage("You have chosen a value of "+PatientTestRunActivity.getRampUpTimeValue()+" for ramp-up time.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences stimulationParameters = getActivity().getSharedPreferences("Stimulation_Parameters", 0);
                            SharedPreferences.Editor editor = stimulationParameters.edit();
                            editor.putInt("Ramp-up Time", PatientTestRunActivity.getRampUpTimeValue());
                            editor.commit();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
        else {
            builder.setMessage("You have chosen a value of "+PatientTestRunActivity.getRampDownTimeValue()+" for ramp-down time.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences stimulationParameters = getActivity().getSharedPreferences("Stimulation_Parameters", 0);
                            SharedPreferences.Editor editor = stimulationParameters.edit();
                            editor.putInt("Ramp-down Time", PatientTestRunActivity.getRampDownTimeValue());
                            editor.commit();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    public static PatientConfirmationFragment newInstance(){

        PatientConfirmationFragment patientConfirmationFragment = new PatientConfirmationFragment();
        Bundle bundle = new Bundle();
        patientConfirmationFragment.setArguments(bundle);

        return patientConfirmationFragment;
    }
}