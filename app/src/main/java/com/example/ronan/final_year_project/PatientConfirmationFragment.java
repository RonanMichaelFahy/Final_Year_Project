package com.example.ronan.final_year_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class PatientConfirmationFragment extends DialogFragment {

    private static final String EXTRA_STIMULATION_PARAMETERS = "EXTRA_STIMULATION_PARAMETERS";

    private StimulationParameters stimulationParameters;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        stimulationParameters = (StimulationParameters) getArguments().getSerializable("EXTRA_STIMULATION_PARAMETERS");

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if(PatientTestRunActivity.isUp()){
            builder.setMessage("You have chosen a value of "+PatientTestRunActivity.getRampUpTimeValue()+" for ramp-up time.")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            stimulationParameters.setRampUpTime(PatientTestRunActivity.getRampUpTimeValue());
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
                            stimulationParameters.setRampDownTime(PatientTestRunActivity.getRampDownTimeValue());
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

    public static PatientConfirmationFragment newInstance(StimulationParameters stimulationParameters){

        PatientConfirmationFragment patientConfirmationFragment = new PatientConfirmationFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_STIMULATION_PARAMETERS, stimulationParameters);
        patientConfirmationFragment.setArguments(bundle);

        return patientConfirmationFragment;
    }
}