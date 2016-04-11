package com.example.ronan.final_year_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import java.util.UUID;

public class ConfirmationFragment extends DialogFragment {

    private static final String TAG = ConfirmationFragment.class.getSimpleName();
    private final UUID stimulationProfileServiceUUID = UUID.fromString("a6322521-0000-2000-9000-001122001122");

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("Stimulation_Parameters", Context.MODE_PRIVATE);
        final SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        final TestRunActivity parent = (TestRunActivity) getActivity();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        switch (parent.getStimulationParameters()){
            case RAMP_UP_TIME:
                builder.setMessage("You have chosen a value of "+ parent.getRampUpTimeValue() +" for ramp-up time.")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final UUID rampUpTimeUUID = UUID.fromString("40001000-0000-2000-9000-001122001122");
                                boolean written = parent.mBluetoothLeService.writeCharacteristic(stimulationProfileServiceUUID, rampUpTimeUUID,
                                        new byte[]{parent.getRampUpTimeValue()});

                                Log.i(TAG, "Ramp-up time written: " + written);
                                mEditor.putInt("Ramp_Up_Time", parent.getRampUpTimeValue());
                                mEditor.apply();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "Cancelled");
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();

            case RAMP_DOWN_TIME:
                builder.setMessage("You have chosen a value of "+parent.getRampDownTimeValue()+" for ramp-down time.")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                final UUID rampDownTimeUUID = UUID.fromString("80000002-0000-2000-9000-001122001122");
                                boolean written = parent.mBluetoothLeService.writeCharacteristic(stimulationProfileServiceUUID, rampDownTimeUUID,
                                        new byte[]{parent.getRampDownTimeValue()});

                                Log.i(TAG, "Ramp-down time written: "+written);
                                mEditor.putInt("Ramp_Down_Time", parent.getRampDownTimeValue());
                                mEditor.apply();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "Cancelled");
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();

            case PULSE_WIDTH:
                builder.setMessage("You have chosen a value of "+parent.getPulseWidthValue()+" for pulse width.")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final UUID pulseWidthUUID = UUID.fromString("D0200000-0000-2000-9000-001122001122");
                                boolean written = parent.mBluetoothLeService.writeCharacteristic(stimulationProfileServiceUUID, pulseWidthUUID,
                                        new byte[]{parent.getPulseWidthValue()});

                                Log.i(TAG, "Pulse width written: "+written);
                                mEditor.putInt("Pulse_Width", parent.getPulseWidthValue());
                                mEditor.apply();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "Cancelled");
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();

            case PULSE_FREQUENCY:
                builder.setMessage("You have chosen a value of "+parent.getPulseFrequencyValue()+" for pulse frequency.")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final UUID pulseFrequencyUUID = UUID.fromString("c0020000-0000-2000-9000-001122001122");
                                boolean written = parent.mBluetoothLeService.writeCharacteristic(stimulationProfileServiceUUID, pulseFrequencyUUID,
                                        new byte[]{parent.getPulseFrequencyValue()});

                                Log.i(TAG, "Pulse frequency written: "+written);
                                mEditor.putInt("Pulse_Frequency", parent.getPulseFrequencyValue());
                                mEditor.apply();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "Cancelled");
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();

            case THRESHOLD:
                builder.setMessage("You have chosen a value of "+parent.getThresholdValue()+" for threshold.")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final UUID serviceUUID = UUID.fromString("a6322521-0000-2000-9000-1122334455ff");
                                final UUID pulseFrequencyUUID = UUID.fromString("70010000-0000-2000-9000-1122334455FF");
                                //boolean written = parent.mBluetoothLeService.writeCharacteristic(serviceUUID, pulseFrequencyUUID,
                                //        new byte[]{parent.getThresholdValue()});

                                //Log.i(TAG, "Threshold written: "+written);
                                mEditor.putInt("Threshold", parent.getThresholdValue());
                                mEditor.apply();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Log.i(TAG, "Cancelled");
                            }
                        });
                // Create the AlertDialog object and return it
                return builder.create();

            default:
                break;
        }
        return null;
    }
}
