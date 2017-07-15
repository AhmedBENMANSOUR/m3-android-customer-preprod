package com.dioolcustomer.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.auth0.core.Token;
import com.github.orangegangsters.lollipin.lib.managers.AppLockActivity;
import com.google.gson.Gson;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.fragments.PinTutoFragment;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;


/**
 * Created by sihem.messaoui on 09/06/2016.
 */
public class CustomPinActivity extends AppLockActivity{

    public static boolean isFromAuthActivity = false;
    private SharedPreferences shared;
    Gson gson;
    Token mUserToken;
    SharedPreferences.Editor editor;


    Encryption encryption = new Encryption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // addTutoFragment();

        mStepTextView = (TextView) this.findViewById(R.id.pin_code_step_textview);
        mForgotTextView = (TextView) this.findViewById(R.id.pin_code_forgot_textview);


      /*  if (MyMoneyMobileApplication.lockManager.getAppLock().checkPasscode("")){
            mStepTextView.setText("Créer votre code PIN");
            mForgotTextView.setVisibility(View.GONE);
        }
        else */if (MyMoneyMobileApplication.lockManager.getAppLock().isPasscodeSet())
            mStepTextView.setText("Taper votre code PIN");

        else {
            mStepTextView.setText("Créer votre code PIN");
            mForgotTextView.setVisibility(View.GONE);
        }


        mForgotTextView.setText("Code PIN oublié?");
        mStepTextView.setTextColor(getResources().getColor(R.color.violet));
        mForgotTextView.setTextColor(getResources().getColor(R.color.violet));

        mPinCodeRoundView.setVisibility(View.VISIBLE);
        mPinCodeRoundView.setEmptyDotDrawable(getResources().getDrawable(R.drawable.pin_code_round_empty));
        mPinCodeRoundView.setFullDotDrawable(getResources().getDrawable(R.drawable.pin_code_round_full));

        ImageView logoImage = ((ImageView) findViewById(R.id.pin_code_logo_imageview));
        logoImage.setImageResource(MyMoneyMobileApplication.lockManager.getAppLock().getLogoId());
        logoImage.setVisibility(View.VISIBLE);

    }

    private void addTutoFragment() {

        LayoutInflater inflater = getLayoutInflater();
        View mView = inflater.inflate(R.layout.container, null);
        getWindow().addContentView(mView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        final PinTutoFragment mFragment = new PinTutoFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mFragment, TAG)
                .commit();

    }


    @Override
    public void onPinCodeSuccess() {
        MyMoneyMobileApplication.getInstance().start();
        MyMoneyMobileApplication.getInstance().setmIsConnected(true);
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants. MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();
        gson = new Gson();


        Log.e("JSONException", isFromAuthActivity + "");
        if (isFromAuthActivity) {

            // Generate Refresh Token

            String jsonToken = null;
            try {
                jsonToken = shared.getString("USER_TOKEN", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            gson = new Gson();
            mUserToken = gson.fromJson(jsonToken, Token.class);

            Token mUserExistToken = new Gson().fromJson(jsonToken, Token.class);
            String mRefreshToken = MMMUtils.getRefreshToken(mUserExistToken.getIdToken(), CustomPinActivity.this);
            //String mRefreshToken = MMMUtils.getRefreshToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJJREVOVElUWSIsIkFDQ09VTlQiLCJNT05FVEFSWV9BQ0NPVU5UIiwiVFJBTlNGRVIiLCJJTlRFUk5BTF9UUkFOU0ZFUiIsIlNUQVRTX1RSQU5TRkVSIiwiRlVORFNfVFJBTlNGRVIiLCJQQVlNRU5UIiwiUVVPVEVfUkVRVUVTVF9SRVNQT05TRSIsIkFDQ09VTlRfTUdNVCIsIlNIT1BfTUdNVCIsIklERU5USVRZIiwiQUNDT1VOVCIsIk1PTkVUQVJZX0FDQ09VTlQiLCJUUkFOU0ZFUiIsIklOVEVSTkFMX1RSQU5TRkVSIiwiU1RBVFNfVFJBTlNGRVIiLCJGVU5EU19UUkFOU0ZFUiIsIlBBWU1FTlQiLCJRVU9URV9SRVFVRVNUX1JFU1BPTlNFIiwiQUNDT1VOVF9NR01UIiwiU0hPUF9NR01UIl0sInVzZXJfaWQiOiJhdXRoMHw1N2ZhMTk4MmMzMzViOWEyNDgzMzY0M2IiLCJlbWFpbCI6Im5pemFya2xpYmlAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInByb2ZpbCI6InN1cGVyX21lcmNoYW50IiwiaXNzIjoiaHR0cHM6Ly9teW1vbmV5bW9iaWxlLmV1LmF1dGgwLmNvbS8iLCJzdWIiOiJhdXRoMHw1N2ZhMTk4MmMzMzViOWEyNDgzMzY0M2IiLCJhdWQiOiJwM2FyY0FPdWNoMldRWDhqNXpMbmNibmRQTGtxOVliMyIsImV4cCI6MTQ4NTExMDIxOCwiaWF0IjoxNDg1MjU0NDE4fQ.h39CydjtRxvtnl5IAfjNFLa2arDG-vi8MP5e3BS5Anc", CustomPinActivity.this);
            if ((!mRefreshToken.equals(null)) && (!mRefreshToken.equals(""))) {
                try {
                    editor.putString("USER_ID_TOKEN",encryption.encrypt(mUserToken.getIdToken(), mRefreshToken));
                } catch (Exception e) {

                }
            }


            // Start menu Activity
            final Intent newIntent = new Intent(CustomPinActivity.this, ServiceSelectionMerchantActivity.class);
            startActivity(newIntent);
            finish();


        }

        isFromAuthActivity = false;

    }

    @Override
    public void showForgotDialog() {
        Resources res = getResources();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomPinActivity.this);
        alertDialogBuilder.setMessage("Vous serez déconnéctés pour réetablir un nouveau code PIN")
                .setTitle("DIOOL")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //MyMoneyMobileApplication.lockManager.getAppLock().setPasscode("");

                        MyMoneyMobileApplication.lockManager.getAppLock().disableAndRemoveConfiguration();

                        //MyMoneyMobileApplication.lockManager
                        MyMoneyMobileApplication.lockManager.getAppLock().enable();// à tester




                      //  MyMoneyMobileApplication.lockManager = LockManager.getInstance();
                      //  MyMoneyMobileApplication.lockManager.enableAppLock(CustomPinActivity.this, CustomPinActivity.class);
                     //   MyMoneyMobileApplication.lockManager.getAppLock().setLogoId(R.drawable.ic_launcher);
                     //   MyMoneyMobileApplication.lockManager.getAppLock().setFingerprintAuthEnabled(false);
                        MMMUtils.logoutUser(CustomPinActivity.this);
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).show();

    }

    @Override
    public void onPinFailure(int attempts) {
        Toast.makeText(CustomPinActivity.this, "Veuillez vérifier votre code PIN!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPinSuccess(int attempts) {
    }

    @Override
    public int getPinLength() {
        return super.getPinLength();
    }

  /*  @Override
    public int getContentView() {
        return R.layout.activity_custom_pin;
    }*/


    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        //finish();
    }

}
