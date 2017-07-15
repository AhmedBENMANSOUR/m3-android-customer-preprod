package com.dioolcustomer.utils;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;

import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.callback.RefreshIdTokenCallback;
import com.auth0.lock.Lock;
import com.auth0.lock.LockContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.activities.AuthActivity;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.permission.PermissionUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mypc on 02/02/2016.
 */
public class MMMUtils {
    public static String prepareOperationUrl(String... params){
        //param keys should be listed here by order
        String[] paramKeys={"action", "agentID", "agentPwd", "mnoID", "msisdn", "amount", "ProductType", "validationStep", "validationValue"};
        //
        String operationUrl= GlobalConstants.API_URL;
        //first parameter which is action
        operationUrl+= "?"+paramKeys[0]+"="+params[0];
        for(int i=1; i< params.length; i++){
            operationUrl += "&"+paramKeys[i]+"="+params[i];
        }

        return operationUrl;
    }
    public static String prepareSignupUrl(String... params){
        //param keys should be listed here by order
        String[] paramKeys={"action", "usertype","email","verifCode","verifyWith","name","lastName","cityarea","cityname","phone","password","password2","companyname","companytype","registrationnumber","taxcard","identitycardnumber","identitycardvaliditydate", "subDistID"};
        //
        String operationUrl= GlobalConstants.API_URL;
        //first parameter which is action
        operationUrl+= "?"+paramKeys[0]+"="+params[0];
        for(int i=1; i< params.length; i++){
            operationUrl += "&"+paramKeys[i]+"="+Uri.encode(params[i]);
        }
        return operationUrl;
    }

    public static String prepareLoadOpenSubdistsUrl(String... params){
        //param keys should be listed here by order
        String[] paramKeys={"action"};
        //
        String operationUrl= GlobalConstants.API_URL;
        //first parameter which is action
        operationUrl+= "?"+paramKeys[0]+"="+params[0];
        for(int i=1; i< params.length; i++){
            operationUrl += "&"+paramKeys[i]+"="+Uri.encode(params[i]);
        }
        return operationUrl;
    }

    public static String prepareChangePasswordUrl(String... params){
        //param keys should be listed here by order
        String[] paramKeys={"action", "agentID", "agentPwd","newPwd"};
        //
        String operationUrl= GlobalConstants.API_URL;
        //first parameter which is action
        operationUrl+= "?"+paramKeys[0]+"="+params[0];
        for(int i=1; i< params.length; i++){
            operationUrl += "&"+paramKeys[i]+"="+params[i];
        }

        return operationUrl;
    }

    public static void logoutUser(Activity activity) {
        SharedPreferences.Editor editor = activity.getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();

        editor.remove("USER_PROFILE");
        editor.remove("USER_TOKEN");
        editor.remove("USER_ID_TOKEN");


        editor.remove("USER_NAME_MERCHANT");
        editor.remove("USER_LASTNAME_MERCHANT");
        editor.remove("USER_PHONE_MERCHANT");
        editor.remove("USER_EMAIL_MERCHANT");
        editor.remove("USER_TAX_CARD_MERCHANT");
        editor.remove("USER_BUSINESS_REGISTRATION_MERCHANT");
        editor.remove("USER_BUSINESS_ADRESS_MERCHANT");
        editor.remove("USER_BUSINESSNAME_MERCHANT");
        editor.remove("SHOP_ID");
        editor.remove("USER_ID_CARD_MERCHANT");
        editor.remove("USER_ADRESS_MERCHANT");
        editor.remove("PARENT_IDS");
        editor.remove("CHILDREN");
        editor.remove("USER_ID");




        editor.commit();
        MyMoneyMobileApplication.getInstance().setmIsConnected(false);

        activity.finish();

        Intent intent2 = new Intent(activity, AuthActivity.class);
        activity.startActivity(intent2);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(GlobalConstants.ACTION_LOGOUT);
        activity.sendBroadcast(broadcastIntent);
    }

    public static BroadcastReceiver registerLogoutBroadcastReceiver(final Activity activity){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GlobalConstants.ACTION_LOGOUT);
        BroadcastReceiver broadcastReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive","Logout in progress");
                //At this point you should start the login activity and finish this one
                activity.finish();
            }
        };
        activity.registerReceiver(broadcastReceiver, intentFilter);
        return broadcastReceiver;
    }

    public static void unRegisterLogoutBroadcastReceiver(final Activity activity, BroadcastReceiver broadcastReceiver){
        activity.unregisterReceiver(broadcastReceiver);
    }

    public static LinkedHashMap<String, String> JsonToSubdistHashMap(JsonObject jsonObject){
        LinkedHashMap<String, String>  result=new LinkedHashMap<String, String>();
        JsonArray jsonArray=jsonObject.getAsJsonArray("subdistributors");
        result.put("nop","Pas de rattachement");
        for (int i=0; i<jsonArray.size(); i++) {
            result.put(jsonArray.get(i).getAsJsonObject().get("id").getAsString(),
                    jsonArray.get(i).getAsJsonObject().get("name").getAsString());
        }

        return result;
    }

    public static String loadJSONFromAsset(Context context) {
        StringBuilder buf=new StringBuilder();
        String str="";

        try{
            InputStream json=context.getAssets().open("transactions2.json");
            BufferedReader in=
                    new BufferedReader(new InputStreamReader(json, "UTF-8"));
            while ((str=in.readLine()) != null) {
                buf.append(str);
            }

            in.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return  buf.toString();
    }
    public static Calendar dateCalendarFromUTCString(String dateString){
        Date date=new Date();
        Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC")); // creates a new calendar instance

        SimpleDateFormat sdfDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);
        try {
            date = sdfDate.parse(dateString);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static String prapareTimeString(int hours, int mins){
        String timeString="";
        if(hours<10){
            timeString+="0"+hours;
        }else{
            timeString+=hours;
        }
        timeString+=":";
        if(mins<10){
            timeString+="0"+mins;
        }else{
            timeString+=mins;
        }
        return timeString;
    }

    public static String prapareDateString(int day, int month){
        String dateString="";
        if(day<10){
            dateString+="0"+day;
        }else{
            dateString+=day;
        }
        dateString+="/";
        if(month<10){
            dateString+="0"+month;
        }else{
            dateString+=month;
        }
        return dateString;
    }

    public static boolean isConnectedTointernet(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }


    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }


    public static boolean launchRequestPermissionProcess(Activity activity) {

        ArrayList<String> mListOfPermision;
        ArrayList<String> mListOfPermisionName;
        boolean storagePesmissionGraneted;

        boolean requestPermision = false;
        storagePesmissionGraneted =
                PermissionUtil.checkPermissions(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);


        mListOfPermision = new ArrayList<>();
        mListOfPermisionName = new ArrayList<>();


        if (!storagePesmissionGraneted) {
            mListOfPermision.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            mListOfPermisionName.add(activity.getString(R.string.require_permission_stockage));
        }


        if (mListOfPermision.size() > 0) {
            String[] stockArr = new String[mListOfPermision.size()];
            stockArr = mListOfPermision.toArray(stockArr);

            PermissionUtil.requestPermissions(activity,
                    stockArr,
                    PermissionUtil.REQUESTCODE_APP_PERMISSION);
            requestPermision = true;
        }

        return requestPermision;

    }




   public static String getRefreshToken(String idToken, final Activity activity)
    {
        final String[] mToken = {""};
        Lock lock = LockContext.getLock(activity);
        AuthenticationAPIClient client = lock.getAuthenticationAPIClient();
        client.delegationWithIdToken(idToken).start(new RefreshIdTokenCallback() {
            @Override
            public void onSuccess(String idToken, String tokenType, int expiresIn) {
                //SUCCESS
                Log.e("Refresh",idToken+"");
                mToken[0] =idToken;
                /*if((! mToken[0].equals(null))&&( !mToken[0].equals("")))
                {

                }*/;


            }

            @Override
            public void onFailure(Throwable error) {
                //FAILURE
                logoutUser(activity);
            }
        });

        return mToken[0];
    }



    public static void addFragment(Fragment frag, Bundle bundle, String backstack,
                                   boolean forceReplace, FragmentActivity _activity, int id_container) {

        FragmentManager fm = _activity.getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(id_container);
        FragmentTransaction ft = fm.beginTransaction();
        //ft.setCustomAnimations(R.anim.enter_animation, 0, 0, R.anim.exit_animation);
        if (bundle != null) {
            frag.setArguments(bundle);
        }
        if (fragment != null || forceReplace) {
            ft.replace(id_container, frag, backstack);
            ft.addToBackStack(backstack);
        } else {
            ft.add(id_container, frag, backstack);
            ft.addToBackStack(backstack);
        }
        ft.commitAllowingStateLoss();
    }


    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        boolean isLocationEnabled = false;
        if (context != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                try {
                    locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

                } catch (Settings.SettingNotFoundException e) {
                    e.printStackTrace();
                }

                isLocationEnabled = locationMode != Settings.Secure.LOCATION_MODE_OFF;

            } else {
                locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                isLocationEnabled = !TextUtils.isEmpty(locationProviders);
            }
        }
        return isLocationEnabled;
    }


    public static boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }


}