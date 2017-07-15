package com.dioolcustomer.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.View;

import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;


public class PermissionUtil {


    public static final int REQUESTCODE_APP_PERMISSION = 101;

    public static boolean isMarshmallowVersion() {
        boolean isMarshmallowVersion = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);

        return isMarshmallowVersion;
    }


    /**
     * Check whether permission is granted
     * Return the current state of the permissions.
     *
     * @param context
     * @param permission
     */


    @TargetApi(Build.VERSION_CODES.M)
    public static boolean checkPermissions(Context context, String permission) {
        if (isMarshmallowVersion() == true) {
            int permissionState = context.checkSelfPermission(permission);

            // Check if the permission is already available.
            return permissionState == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }


    /**
     * Request permission if it is not already granted
     *
     * @param activity
     * @param permissions
     * @param requestCode
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        activity.requestPermissions(permissions, requestCode);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void requestPermissions(Fragment activity, String[] permissions, int requestCode) {
        activity.requestPermissions(permissions, requestCode);
    }
    public static void showRequestPermissionDialog(final Activity activity, String[] permissionName,
                                                   final int requestCodeActivityResult,
                                                   final ICallbackDeniedPermissionDialog iCallbackDeniedPermissionDialog) {
        if (permissionName == null) {
            return;
        }
        String title =
                MyMoneyMobileApplication.getInstance().getResources().getString(R.string.dialog_permission_request_title);
        String content;
        if (permissionName.length == 1) {
            content =
                    MyMoneyMobileApplication.getContext().getString(R.string.dialog_permission_single_request_content,
                            permissionName[0], MyMoneyMobileApplication.getContext().getResources().getString(R.string.app_name));
        } else {

            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < permissionName.length - 1; i++) {
                stringBuffer.append("'");
                stringBuffer.append(permissionName[i]);
                stringBuffer.append("'");
                if (i < (permissionName.length - 2))
                    stringBuffer.append(",");
            }
            content =
                    MyMoneyMobileApplication.getContext().getString(R.string.dialog_permissions_two_request_content,
                            stringBuffer.toString(), permissionName[permissionName.length - 1], MyMoneyMobileApplication.getContext().getResources().getString(R.string.app_name));
        }
        DialogUtil.showStandardDialog(activity, title, content, R.string.dialog_permission_allow_button,
                R.string.dialog_permission_deny_button, new StandardAlertDialogFragment.OnClickListener() {

                    @Override
                    public boolean onClick(View v, boolean askAgain) {
                        Intent myAppSettings =
                                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"
                                        + activity.getPackageName()));
                        // myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                        // myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (requestCodeActivityResult > 0) {

                            activity.startActivityForResult(myAppSettings, requestCodeActivityResult);
                        } else {

                            activity.startActivity(myAppSettings);
                            activity.finish();
                        }
                        return false;
                    }
                }, new StandardAlertDialogFragment.OnClickListener() {

                    @Override
                    public boolean onClick(View v, boolean askAgain) {
                        if (iCallbackDeniedPermissionDialog != null) {
                            iCallbackDeniedPermissionDialog.onCallbackDeniedPermissionDialog();
                        }
                        return false;
                    }
                }, false, false);
    }

    public interface ICallbackDeniedPermissionDialog {

        void onCallbackDeniedPermissionDialog();
    }
}
