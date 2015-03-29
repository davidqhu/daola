package com.who.daola;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Created by hud on 3/22/15.
 */
public class RadiusPickerDialogFragment extends DialogFragment{

    private NumberPicker mRadiusPicker;
    /* The activity that creates an instance of this dialog fragment must
 * implement this interface in order to receive event callbacks.
 * Each method passes the DialogFragment in case the host needs to query it. */
    public interface RadiusPickerDialogFragmentListener {
        public void onRadiusSetClick(int radius);
    }

    // Use this instance of the interface to deliver action events
    RadiusPickerDialogFragmentListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (RadiusPickerDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement RadiusPickerDialogFragmentListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        System.out.println("#####show dialog");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.number_picker_dialog, null);

        builder.setView(view)
                .setTitle(R.string.dialog_pick_radius)
                .setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onRadiusSetClick(mRadiusPicker.getValue());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        mRadiusPicker = (NumberPicker)view.findViewById(R.id.radius_numberpicker);
        mRadiusPicker.setMaxValue(50);
        mRadiusPicker.setMinValue(5);
        mRadiusPicker.setValue(getArguments().getInt(AddFenceActivity.RADIUS_ARGUMENT));
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
