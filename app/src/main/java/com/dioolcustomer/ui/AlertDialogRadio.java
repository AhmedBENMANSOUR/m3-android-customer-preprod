package com.dioolcustomer.ui;

import android.app.AlertDialog;


import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 28/04/2017.
 */

public class AlertDialogRadio extends DialogFragment {

    @Getter
    @Setter
   private static String[] code = new String[]{

    };




    /** Declaring the interface, to invoke a callback function in the implementing activity class */
    AlertPositiveListener alertPositiveListener;
    AlertNegativeListener alertNegativeListener;


    /** An interface to be implemented in the hosting activity for "OK" button click listener */
    public interface AlertPositiveListener {
        public void onPositiveClick(int position);
    }

    public interface AlertNegativeListener {
        public void onNegativeClick(int position);
    }


    /** This is a callback method executed when this fragment is attached to an activity.
     *  This function ensures that, the hosting activity implements the interface AlertPositiveListener
     * */
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        try{
            alertPositiveListener = (AlertPositiveListener) activity;
            alertNegativeListener = (AlertNegativeListener) activity;
        }catch(ClassCastException e){
            // The hosting activity does not implemented the interface AlertPositiveListener
            throw new ClassCastException(activity.toString() + " must implement AlertPositiveListener and AlertNegativeListener");
        }
    }


    /** This is the OK button listener for the alert dialog,
     *  which in turn invokes the method onPositiveClick(position)
     *  of the hosting activity which is supposed to implement it
     */
    OnClickListener positiveListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog)dialog;
            int position = alert.getListView().getCheckedItemPosition();
            alertPositiveListener.onPositiveClick(position);
        }
    };

    OnClickListener negativeListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlertDialog alert = (AlertDialog)dialog;
            int position = alert.getListView().getCheckedItemPosition();
            alertNegativeListener.onNegativeClick(position);
        }
    };

    /** This is a callback method which will be executed
     *  on creating this fragment
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        /** Getting the arguments passed to this fragment */
        Bundle bundle = getArguments();
        int position = bundle.getInt("position");


        /** Creating a builder for the alert dialog window */
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());


        /** Setting a title for the window */
        b.setTitle("Choisir votre boutique");


        /** Setting items to the alert dialog */
        b.setSingleChoiceItems(code, position, null);


        /** Setting a positive button and its listener */
        b.setPositiveButton("OK",positiveListener);

        /** Setting a positive button and its listener */
        b.setNegativeButton("Cancel", negativeListener);


        /** Creating the alert dialog window using the builder class */
        AlertDialog d = b.create();

        /** Return the alert dialog window */
        return d;
    }


}
