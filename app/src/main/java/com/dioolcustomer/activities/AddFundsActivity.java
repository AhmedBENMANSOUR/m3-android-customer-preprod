package com.dioolcustomer.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.BalanceResponse;
import com.dioolcustomer.models.SendRedeemResponse;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.intercom.android.sdk.Intercom;

public class AddFundsActivity extends AppCompatActivity {

    public String TAG = getClass().getSimpleName();
    Button mBtnSend, cancelButton;
    private SharedPreferences shared;
    JsonObjectRequest jsonObjReq;
    Gson gson;
    UserProfile mUserProfile;
    Token mUserToken;

    String daBalance = "";
    String revBalance = "";
    boolean isOperatorSelected = false;
    protected String mnoID;

    EditText txtMsisdn;
    EditText txtAmount;
    TextView txtDaBalance;
    TextView txtRevBalance;
    TextView mTextBalance, mTextRevenu;
    SharedPreferences.Editor editor;

    Encryption encryption  = new Encryption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.addfundsmenu);
        setContentView(R.layout.activity_add_funds);


//        Analytics.with(this).screen("View", TAG);


      //  txtMsisdn = (EditText) findViewById(R.id.add_funds_msisdn_edit);
        txtAmount = (EditText) findViewById(R.id.add_funds_amount_edit);
        txtDaBalance = (TextView) findViewById(R.id.textView_da_balance);
        txtRevBalance = (TextView) findViewById(R.id.textView_rev_balance);

        mTextBalance = (TextView) findViewById(R.id.textView_da_balance);
        mTextRevenu = (TextView) findViewById(R.id.textView_rev_balance);



        //Prepare Preferences
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();
        gson = new Gson();
        String jsonToken = shared.getString("USER_TOKEN", "");
        mUserToken = gson.fromJson(jsonToken, Token.class);

        try {
            daBalance = (shared.getString("daBalance", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            revBalance = (shared.getString("revBalance", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }


        txtDaBalance.setText(daBalance + "");
        txtRevBalance.setText(revBalance + "");

        String jsonProfile = null;
        try {
            jsonProfile = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_PROFILE", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mUserProfile = gson.fromJson(jsonProfile, UserProfile.class);



        LinearLayout revenueLayout = (LinearLayout) findViewById(R.id.layout_revenue);
        LinearLayout loyaltyLayout = (LinearLayout) findViewById(R.id.layout_loyalty);
        try {
        if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("customer")){
            revenueLayout.setVisibility(View.GONE);
        }else{
            loyaltyLayout.setVisibility(View.GONE);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //revenueLayout.setVisibility(View.GONE);
        //getSupportActionBar().setTitle(R.string.cashoutmenu);


        cancelButton = (Button) findViewById(R.id.buttonCancel_addFunds);
        mBtnSend = (Button) findViewById(R.id.buttonSend_addFunds);


        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MMMUtils.isConnectedTointernet(AddFundsActivity.this)) {

                    final Dialog dialog = new Dialog(AddFundsActivity.this);
                   if (txtAmount.getText().toString().equals("")) {
                        Toast.makeText(AddFundsActivity.this, "Le montant doit être > 0", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!MMMUtils.isInteger(txtAmount.getText().toString())) {
                        Toast.makeText(AddFundsActivity.this, "Le montant doit être un nombre entier", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                            dialog.dismiss();
                       try {
                           startAddFunds(txtAmount.getText().toString());
                       } catch (Exception e) {
                           e.printStackTrace();
                       }

                   }

                } else
                    Toast.makeText(AddFundsActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();


///                System.out.println("click sur le boutton envoyer");

            }
        });




        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\" face=\"normal|bold|italic\">" + "Add Funds" + "</font>")));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Handle
        switch (item.getItemId()) {

           /* case R.id.share:
                launchSharing();
                return true;*/

            case R.id.settings:
                Intent intent = new Intent(AddFundsActivity.this,
                        SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                MMMUtils.logoutUser(this);

                return true;
            case R.id.intercom:
                //Intercom.initialize(MyMoneyMobileApplication.getInstance(), GlobalConstants.INTERCOM_API_KEY, GlobalConstants.INTERCOM_APP_ID);
                Intercom.client().displayConversationsList();
                return true;

            case R.id.refresh:
                if (MMMUtils.isConnectedTointernet(AddFundsActivity.this))
                    getMerchantBalance();
                return true;

            default:
                return true;
        }
    }


    private void startAddFunds(final String mAmount) throws Exception {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(AddFundsActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();


        //create table issuerParents
        List<String> parentsIdList = new ArrayList<>();
        Object [] issuerParents;
        JSONObject jsnobject = new JSONObject("{parent_ids:"+encryption.decrypt(mUserToken.getIdToken(),shared.getString("PARENT_IDS", ""))+"}");

        JSONArray jsonArray = jsnobject.getJSONArray("parent_ids");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            if(explrObject.has("parent_id")){
                parentsIdList.add(explrObject.getString("parent_id"));
            }
        }
        issuerParents  = parentsIdList.toArray();

        // Sending parameters
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", Float.parseFloat(mAmount));
        params.put("shopId", encryption.decrypt(mUserToken.getIdToken(),shared.getString("SHOP_ID","")));
        params.put("issuerParents",issuerParents);
        params.put("beVersion", getString(R.string.buck_end_version));
        params.put("channel","mobile");

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_ADD_FUNDS, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        SendRedeemResponse mRedeem = new SendRedeemResponse().createRedeem(response);

                        if (String.valueOf(mRedeem.getCode()).equals("0")) {
                            getBalance();
                            SweetAlertDialog mSweet = new SweetAlertDialog(AddFundsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage())
                                    ;


                            mSweet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });

                            mSweet.show();


                        }else if (String.valueOf(mRedeem.getCode()).equals("27")){
                            new SweetAlertDialog(AddFundsActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage())
                                    .setCancelText("Cancel")
                                    .setConfirmText("Confirm")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            ///// send transfer after confirmation
                                            try {
                                                startConfirmAddFunds(mAmount);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (GeneralSecurityException e) {
                                                e.printStackTrace();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    })
                                    .show();

                        }
                        else {
                            new SweetAlertDialog(AddFundsActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage())
                                    .show();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(AddFundsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DIOOL")
                        .setContentText("L'opération a échoué")
                        .show();
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", mUserToken.getType() + " " + mUserToken.getIdToken());
                return headers;
            }


        };

        // Define time out request
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.cat2Timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, AddFundsActivity.class.getCanonicalName());
    }



    //confirm add funds



    private void startConfirmAddFunds(String mAmount) throws Exception {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(AddFundsActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();


        //create table issuerParents
        List<String> parentsIdList = new ArrayList<>();
        Object [] issuerParents;
        JSONObject jsnobject = new JSONObject("{parent_ids:"+encryption.decrypt(mUserToken.getIdToken(),shared.getString("PARENT_IDS", ""))+"}");

        JSONArray jsonArray = jsnobject.getJSONArray("parent_ids");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            if(explrObject.has("parent_id")){
                parentsIdList.add(explrObject.getString("parent_id"));
            }
        }
        issuerParents  = parentsIdList.toArray();

        // Sending parameters
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", Float.parseFloat(mAmount));
        params.put("shopId", encryption.decrypt(mUserToken.getIdToken(),shared.getString("SHOP_ID", "")));
        params.put("issuerParents",issuerParents);
        params.put("beVersion", getString(R.string.buck_end_version));
        params.put("channel","mobile");
        params.put("confirm",true);

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_ADD_FUNDS, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        SendRedeemResponse mRedeem = new SendRedeemResponse().createRedeem(response);

                        if (String.valueOf(mRedeem.getCode()).equals("0")) {
                            SweetAlertDialog mSweet = new SweetAlertDialog(AddFundsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage())
                                    ;

                            getBalance();

                            mSweet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });
                            mSweet.show();
                        }else {
                            new SweetAlertDialog(AddFundsActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage())
                                    .show();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(AddFundsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DIOOL")
                        .setContentText("L'opération a échoué")
                        .show();
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Authorization", mUserToken.getType() + " " + mUserToken.getIdToken());
                return headers;
            }


        };

        // Define time out request
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.cat2Timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, AddFundsActivity.class.getCanonicalName());
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        isOperatorSelected = true;
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButtonOrange:
                if (checked) {
                    mnoID = GlobalConstants.OCM_VALUE;
                    ;
                }
                break;
            case R.id.radioButtonMTN:
                if (checked) {
                    mnoID = GlobalConstants.MTNC_VALUES;
                }
                break;
        }
    }

    private void getBalance() {

        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                GlobalConstants.URL_BALANCE, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());

                        BalanceResponse mBalnceResponse = new BalanceResponse().createBalanceObject(response);


                        editor.putString("daBalance", mBalnceResponse.getDepositAccountBalance() + "");
                        editor.putString("revBalance", mBalnceResponse.getRevenueAccountBalance() + "");
                        editor.commit();

                        txtDaBalance.setText(mBalnceResponse.getDepositAccountBalance() + "");
                        txtRevBalance.setText(mBalnceResponse.getRevenueAccountBalance() + "");
                        //txtRevenu.setText(mBalnceResponse.getRevenueAccountBalance() + "");


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());


            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                try {
                    headers.put("Authorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return headers;
            }


        };

        // Define time out request
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.cat2Timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, AddFundsActivity.class.getCanonicalName());
    }




    private void getMerchantBalance() {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(AddFundsActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();


        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                GlobalConstants.URL_BALANCE, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();

                        BalanceResponse mBalnceResponse = new BalanceResponse().createBalanceObject(response);
                        System.out.println("mBalnceResponse.getCode() : "+mBalnceResponse.getCode());
                        if(mBalnceResponse.getCode()== 41){
                            Toast.makeText(AddFundsActivity.this, mBalnceResponse.getMessage(), Toast.LENGTH_LONG).show();
                            MMMUtils.logoutUser(AddFundsActivity.this);
                        }


                        editor.putString("daBalance", mBalnceResponse.getDepositAccountBalance() + "");
                        editor.putString("revBalance", mBalnceResponse.getRevenueAccountBalance() + "");
                        editor.commit();

                        mTextBalance.setText(mBalnceResponse.getDepositAccountBalance() + "");
                        mTextRevenu.setText(mBalnceResponse.getRevenueAccountBalance() + "");

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                try {
                    headers.put("Authorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return headers;
            }


        };

        // Define time out request
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.cat2Timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, ServiceSelectionMerchantActivity.class.getCanonicalName());
    }

}
