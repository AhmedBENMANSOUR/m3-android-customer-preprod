package com.dioolcustomer.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.MenuItem;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.digits.sdk.android.Digits;
import com.google.gson.JsonObject;
import com.dioolcustomer.R;
import com.dioolcustomer.callbacks.FutureCallback;
import com.dioolcustomer.callbacks.HttpTask;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.utils.MMMUtils;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.HashMap;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.fabric.sdk.android.Fabric;

//@DeepLink({"https://emonize.mymoneytop.com/client/#/signup/as/{userType}/email/{email}/verifcode/{verifCode}"})
@DeepLink({"http://signup.mymoneymobile.com/{signupstring}"})
public class SignupActivity extends ActionBarActivity {
    public final String TAG = getClass().getSimpleName();

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "CTpO3sLUDoMV3JtuRzcz54WjS";
    private static final String TWITTER_SECRET = "L96dtiUkkP9zG5EcA4APZqKwCvl75lx34jom9ITVWUJ4UQJ9CN";
    private String verifyWith = "email";
    private String userType = "merchant";
    public final String SIGNUP_FRAGMENT_TAG = "SignupFragment";

    BroadcastReceiver broadcastReceiverLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Analytics.with(this).screen("View", TAG);
        //Init DIGITS
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        TwitterCore twitterCoreInstance = new TwitterCore(authConfig);
        Fabric.with(this, twitterCoreInstance, new Digits());

        Digits.getSessionManager().clearActiveSession();

        Intent intent = getIntent();
        if (getIntent().getStringExtra("userType") != null) {
            userType = getIntent().getStringExtra("userType");
        }
        if (getIntent().getBooleanExtra(DeepLink.IS_DEEP_LINK, false)) {
            Bundle parameters = getIntent().getExtras();
            String signupString = parameters.getString("signupstring");

            executeUserEmailVerification(getSignupParameters(signupString));
        } else {
            callVerificationFragment();
        }
        broadcastReceiverLogout= MMMUtils.registerLogoutBroadcastReceiver(this);

//        startNetverifyButton = (Button) findViewById(R.id.startNetverifyButton);
//        startNetverifyButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                //since the NetverifySDK is a singleton internally, a new instance is not
//                //created here
//                initializeNetverifySDK();
//                checkPermissionsAndStart(netverifySDK);
//            }
//        });


    }

    public String getVerifyWith() {
        return verifyWith;
    }

    public void setVerifyWith(String verifyWith) {
        this.verifyWith = verifyWith;
    }

    public void callVerificationFragment() {
        //Call verification Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        SignupVerificationFragment signupVerificationFragment = SignupVerificationFragment.newInstance(userType);
        fragmentTransaction.add(R.id.fragment_signup_container, signupVerificationFragment);
        fragmentTransaction.commit();
    }

    public void callSignupMerchantFragment(String email, String verifCode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        SignupFragment signupFragment = SignupFragment.newInstance(null, email, verifCode, userType);

        fragmentTransaction.replace(R.id.fragment_signup_container, signupFragment, SIGNUP_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public void executeUserEmailVerification(final HashMap<String, String> params) {
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(SignupActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();
        String urlString = GlobalConstants.API_URL + "?action=verifyemail&email=" + params.get("email") + "&verifCode=" + params.get("verifCode");
        HttpTask httpTask = new HttpTask(getAssets(), GlobalConstants.cat1Timeout);
        httpTask.setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(JsonObject result) {
                Log.i(TAG, "result " + result.toString());
                if (("true").equals(result.get("success").getAsString())) {
//                    new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.SUCCESS_TYPE)
//                            .setTitleText("Succès")
//                            .setContentText("Email verifié")
//                            .show();

                    callSignupMerchantFragment(params.get("email"), params.get("verifCode"));
                } else {
                    new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Echec")
                            .setContentText("Email non verifié")
                            .show();
                    callVerificationFragment();
                }
                dialog.dismiss();
            }

            //@Override
            public void onTimeout() {
                new SweetAlertDialog(SignupActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText(getResources().getString(R.string.failed_connection_title))
                        .setContentText(getResources().getString(R.string.failed_connection))
                        .show();
                dialog.dismiss();
            }
        });
        httpTask.execute(urlString);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //case of deep link
        SignupFragment myFragment = (SignupFragment) fragmentManager.findFragmentByTag(SIGNUP_FRAGMENT_TAG);
        if(myFragment != null && myFragment.isVisible()){
            fragmentManager.popBackStackImmediate();
        }
        //general case
        if (!fragmentManager.popBackStackImmediate() ) {
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public HashMap<String, String> getSignupParameters(String signupString) {
        //Decode string
        signupString = signupString.replace("!!!at!!!", "@");
        signupString = signupString.replace("!!!dot!!!", ".");

        HashMap<String, String> params = new HashMap<>();
        String[] paramKeys = {"userType", "email", "verifCode"};
        String[] signupStringArray = signupString.split("!!!!");
        for (int i = 0; i < paramKeys.length; i++) {
            params.put(paramKeys[i], signupStringArray[i]);
        }
        userType = params.get("userType");
        return params;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
//                Intent homeIntent = new Intent(this, MainActivity.class);
//                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiverLogout);
        super.onDestroy();
    }
}
