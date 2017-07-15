package com.dioolcustomer.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;


public class DialogUtil {

    /**
     * General OK key
     */
    public static final int DIALOG_OK = 0;
    /**
     * General Cancel key
     */
    public static final int DIALOG_CANCEL = 1;


    public static AlertDialog getSelectionListDialog(Context context, String title, String[] arrayList,
                                                     int defaultPosition, int positiveResId, int negativeResId,
                                                     OnClickListener onClickListener, OnClickListener onConfirmListener) {
        Builder builder = resolveDialogTheme(context);
        builder.setTitle(title);

        if (arrayList != null) {
            builder.setSingleChoiceItems(arrayList, defaultPosition, onClickListener);
        }

        if (onConfirmListener != null) {
            setClickEvent(builder, onConfirmListener, positiveResId, negativeResId);
        }

        AlertDialog dialog = builder.create();
        return dialog;
    }

    /**
     * Get dialog style according to the local version. So the dialog theme is
     * not fixed.
     *
     * @param mContext Create dialog context
     * @return builder Dialog builder by different Android version
     */
    @SuppressLint("NewApi")
    public static Builder resolveDialogTheme(Context mContext) {
        Builder builder = null;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB) {
            builder = new Builder(mContext);
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = new Builder(mContext, AlertDialog.THEME_HOLO_DARK);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = new Builder(mContext, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        }

        return builder;
    }

    /**
     * Set positive and negative click event for standard dialog
     */
    private static void setClickEvent(Builder builder, final OnClickListener listener, int positiveResId,
                                      int negativeResId) {
        if (positiveResId > 0) {
            builder.setPositiveButton(positiveResId, new OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    listener.onClick(dialog, DIALOG_OK);
                }
            });
        }
        if (negativeResId > 0) {
            builder.setNegativeButton(negativeResId, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    listener.onClick(dialog, DIALOG_CANCEL);
                }
            });
        }
    }

    /**
     * get standard dialog with title, content, OK, Cancel
     * <p/>
     * Application context.
     *
     * @param title            title string resource id.
     * @param content          content string resource id.
     * @param positiveResId    positive button string resource id.
     * @param negativeResId    negative button string resource id.
     *                         click listener for positive button and negative button.
     * @param positiveListener
     * @param negativeListener
     * @param isCancelable
     * @return generated dialog
     */
    public static void showStandardDialog(Activity activity, String title, String content, int positiveResId,
                                          int negativeResId, StandardAlertDialogFragment.OnClickListener positiveListener,
                                          StandardAlertDialogFragment.OnClickListener negativeListener, boolean withAskAgainOption, boolean isCancelable) {

        FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
        Fragment prevFragment = activity.getFragmentManager()
                .findFragmentByTag(StandardAlertDialogFragment.class.getCanonicalName());

        if (prevFragment != null) {
            fragmentTransaction.remove(prevFragment);
        }
        fragmentTransaction.addToBackStack(null);
        StandardAlertDialogFragment dialog = StandardAlertDialogFragment.newInstance(title, content,
                positiveResId, negativeResId, positiveListener, negativeListener, withAskAgainOption);
        FragmentManager fragmentManager = activity.getFragmentManager();
        dialog.setCancelable(isCancelable);
        dialog.show(fragmentManager, StandardAlertDialogFragment.class.getCanonicalName());

    }


}
