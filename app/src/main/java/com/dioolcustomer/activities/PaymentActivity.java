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
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.google.gson.Gson;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.BalanceResponse;
import com.dioolcustomer.models.SendRedeemResponse;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.intercom.android.sdk.Intercom;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and n avigation/system bar) with user interaction.
 */
public class PaymentActivity extends AppCompatActivity {
    public String TAG = getClass().getSimpleName();
    Button mBtnSend, cancelButton, redeemButton;
    private SharedPreferences shared;
    JsonObjectRequest jsonObjReq;
    Gson gson;
    UserProfile mUserProfile;
    Token mUserToken;

    String daBalance = "";
    String revBalance = "";
    String providerIdentifier = null;
    boolean isOperatorSelected = false;
    protected String mnoID;

    EditText txtMsisdn;
    EditText txtAmount;
    TextView txtDaBalance;
    TextView txtRevBalance;
    SharedPreferences.Editor editor;
    TextView textInfo;
    RadioGroup operatorssRadio;

    Encryption encryption  = new Encryption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.paymentmenu);
        setContentView(R.layout.activity_payment);


//        Analytics.with(this).screen("View", TAG);


        txtMsisdn = (EditText) findViewById(R.id.payment_msisdn_edit);
        txtAmount = (EditText) findViewById(R.id.payment_amount_edit);
        txtDaBalance = (TextView) findViewById(R.id.textView_da_balance);
        txtRevBalance = (TextView) findViewById(R.id.textView_rev_balance);
        textInfo = (TextView) findViewById(R.id.info_text);
        textInfo.setVisibility(View.GONE);
        operatorssRadio = (RadioGroup)findViewById(R.id.payment_operator_radio_layout);
        operatorssRadio.setVisibility(View.GONE);



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



        LinearLayout loyaltyLayout = (LinearLayout) findViewById(R.id.layout_loyalty);
        loyaltyLayout.setVisibility(View.GONE);
      //  getSupportActionBar().setTitle(R.string.cashoutmenu);


        cancelButton = (Button) findViewById(R.id.buttonCancel);
        redeemButton = (Button) findViewById(R.id.buttonRedeem);
        mBtnSend = (Button) findViewById(R.id.buttonSend);


        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MMMUtils.isConnectedTointernet(PaymentActivity.this)) {

                    final Dialog dialog = new Dialog(PaymentActivity.this);
                   /* if (!isOperatorSelected) {
                        Toast.makeText(PaymentActivity.this, "Opérateur non Spécifié", Toast.LENGTH_SHORT).show();

                    } else*/ if (txtAmount.getText().toString().equals("")) {
                        Toast.makeText(PaymentActivity.this, "Le montant doit être > 0", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!MMMUtils.isInteger(txtAmount.getText().toString())) {
                        Toast.makeText(PaymentActivity.this, "Le montant doit être un nombre entier", Toast.LENGTH_SHORT).show();
                        return;
                    } else if(txtMsisdn.getText().toString().equals("")){
                        Toast.makeText(PaymentActivity.this, "Numéro non valide", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {

                        dialog.dismiss();
                        startPayment(txtAmount.getText().toString());

                    }

                } else
                    Toast.makeText(PaymentActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();


                System.out.println("click sur le boutton envoyer");

            }
        });


        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MMMUtils.isConnectedTointernet(PaymentActivity.this)) {
                    final Dialog dialog = new Dialog(PaymentActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.layout_alert_redeem);
                    dialog.setTitle("DIOOL");


                    Button mSendButton = (Button) dialog.findViewById(R.id.reddem_button);
                    final EditText mText = (EditText) dialog.findViewById(R.id.text_montant_redeem);
                    mText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    mSendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isOperatorSelected) {
                                Toast.makeText(PaymentActivity.this, "Opérateur non Spécifié", Toast.LENGTH_SHORT).show();

                            } else if (mText.getText().toString().equals("")) {
                                Toast.makeText(PaymentActivity.this, "Le montant doit être > 0", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (!MMMUtils.isInteger(mText.getText().toString())) {
                                Toast.makeText(PaymentActivity.this, "Le montant doit être un nombre entier", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                dialog.dismiss();
                                startPayment(mText.getText().toString());
                            }
                        }
                    });
                    dialog.show();
                } else
                    Toast.makeText(PaymentActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();


                System.out.println("click sur le boutton envoyer");

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Encaissement" + "</font>")));

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
                Intent intent = new Intent(PaymentActivity.this,
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
               /* if (MMMUtils.isConnectedTointernet(AddFundsActivity.this))
                    getMerchantBalance();
                return true;*/

            default:
                return true;
        }
    }


    private void startPayment(final String mAmount) {


        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(PaymentActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();


        // Sending parameters
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("providerIdentifier",providerIdentifier);//62401
        params.put("amount", Float.parseFloat(mAmount));
        params.put("providerAccountID","237"+txtMsisdn.getText());
        params.put("currency",null);
        params.put("issuerComment", null);
        params.put("issuerLongitude", null);
        params.put("issuerLatitude", null);
        params.put("issuerDeviceID",null);
        params.put("issuerIPAddress",null);
        params.put("beVersion", getString(R.string.buck_end_version));
        params.put("channel","mobile");
        params.put("issuerParents",new String[1]);
        params.put("shopId",null);

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_PAYMENT, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        SendRedeemResponse mRedeem = new SendRedeemResponse().createRedeem(response);

                        if (String.valueOf(mRedeem.getCode()).equals("0")) {
                            SweetAlertDialog mSweet = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage());


                            mSweet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });

                            mSweet.show();


                            getBalance();
                            textInfo.setVisibility(View.GONE);
                            operatorssRadio.setVisibility(View.GONE);
                            providerIdentifier =null;
                        }else if(String.valueOf(mRedeem.getCode()).equals("27")){

                            new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText("You proceeded a transaction similar to the last transaction, Please confirm this operation.")
                                    .setCancelText("Cancel")
                                    .setConfirmText("Confirm")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            ///// send transfer after confirmation
                                            try {
                                                startConfirmPayment(mAmount);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                            sDialog.cancel();

                                        }
                                    }).show();

                        }else if(String.valueOf(mRedeem.getCode()).equals("53")){
                            new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage())
                                    .show();
                            textInfo.setVisibility(View.VISIBLE);
                            operatorssRadio.setVisibility(View.VISIBLE);
                            providerIdentifier ="62401";

                        }
                        else {
                            new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, PaymentActivity.class.getCanonicalName());
    }




    ///////// confirm payment
     public void startConfirmPayment(String mAmount) {


         // Personalize View when loading WS
         final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(PaymentActivity.this, R.style.myDialog))
                 .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                 .themeColor(Color.WHITE)
                 .text("Patientez...")
                 .fadeColor(Color.DKGRAY).build();
         dialog.setCancelable(false);
         dialog.show();

         // Sending parameters
         Map<String, Object> params = new HashMap<String, Object>();
         params.put("providerIdentifier",providerIdentifier);
         params.put("amount", Float.parseFloat(mAmount));
         params.put("providerAccountID","237"+txtMsisdn.getText());
         params.put("currency",null);
         params.put("issuerComment", null);
         params.put("issuerLongitude", null);
         params.put("issuerLatitude", null);
         params.put("issuerDeviceID",null);
         params.put("issuerIPAddress",null);
         params.put("beVersion", getString(R.string.buck_end_version));
         params.put("confirm",true);

         jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                 GlobalConstants.URL_PAYMENT, new JSONObject(params),
                 new Response.Listener<JSONObject>() {

                     @Override
                     public void onResponse(JSONObject response) {
                         Log.e(TAG, response.toString());
                         dialog.dismiss();
                         SendRedeemResponse mRedeem = new SendRedeemResponse().createRedeem(response);

                         if (String.valueOf(mRedeem.getCode()).equals("0")) {
                             SweetAlertDialog mSweet = new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                     .setTitleText("DIOOL")
                                     .setContentText(mRedeem.getMessage());


                             mSweet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                 @Override
                                 public void onDismiss(DialogInterface dialog) {
                                     finish();
                                 }
                             });

                               getBalance();
                         } else {
                             new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                 new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
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
         MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, PaymentActivity.class.getCanonicalName());
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, PaymentActivity.class.getCanonicalName());
    }

}
