package com.dioolcustomer.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.BalanceResponse;
import com.dioolcustomer.models.SendRedeemResponse;
import com.dioolcustomer.models.TransfertArgentResponse;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.intercom.android.sdk.Intercom;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and n avigation/system bar) with user interaction.
 *
 * @see
 */
public class CashinActivity extends AppCompatActivity {
    public String TAG = getClass().getSimpleName();
    Button mBtnSend, cancelButton, redeemButton;
    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    JsonObjectRequest jsonObjReq;
    Gson gson;
    UserProfile mUserProfile;
    Token mUserToken;

    String daBalance = "";
    String revBalance = "";
    boolean isOperatorSelected = false;
    protected String mnoID = null;


    EditText txtMsisdn;
    EditText txtAmount;
    TextView txtDaBalance, mTextRevenu, mTextPoints;
    TextView txtRevBalance;
    TextView textInfo;
    RadioGroup operatorssRadio;

    private File pdfFile;
    File myFile;
    BaseFont bfBold;

    Encryption encryption = new Encryption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        setContentView(R.layout.activity_cashin);
        //super.initCommonUI();
//        Analytics.with(this).screen("View", TAG);


        //Prepare Preferences
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();

        gson = new Gson();
        String jsonToken = shared.getString("USER_TOKEN", "");
        mUserToken = gson.fromJson(jsonToken, Token.class);


            daBalance = (shared.getString("daBalance", ""));
            revBalance = (shared.getString("revBalance", ""));



        String jsonProfile = null;
        try {
            jsonProfile = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_PROFILE", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mUserProfile = gson.fromJson(jsonProfile, UserProfile.class);



        // Find Views
        txtMsisdn = (EditText) findViewById(R.id.cashin_msisdn_edit);
        txtAmount = (EditText) findViewById(R.id.cashin_amount_edit);
        txtDaBalance = (TextView) findViewById(R.id.textView_da_balance);
        txtRevBalance = (TextView) findViewById(R.id.textView_loyalty_points);
        mTextPoints = (TextView) findViewById(R.id.textView_rev_balance_label);
        mTextRevenu = (TextView) findViewById(R.id.textView_rev_balance);
        txtDaBalance.setText(daBalance + "");
        txtRevBalance.setText(revBalance + "");
        mTextRevenu.setText(revBalance + "");
        textInfo = (TextView) findViewById(R.id.info_text);
        textInfo.setVisibility(View.GONE);
        operatorssRadio = (RadioGroup)findViewById(R.id.cashin_operator_radio_layout);
        operatorssRadio.setVisibility(View.GONE);


        cancelButton = (Button) findViewById(R.id.buttonCancel);
        redeemButton = (Button) findViewById(R.id.buttonRedeem);
        mBtnSend = (Button) findViewById(R.id.buttonSend);


        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateTransferArgentForm()) {
                    if (MMMUtils.isConnectedTointernet(CashinActivity.this))
                        try {
                            startTransfertArgent();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    else
                        Toast.makeText(CashinActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();

                }

            }
        });

        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MMMUtils.isConnectedTointernet(CashinActivity.this)) {
                    final Dialog dialog = new Dialog(CashinActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.layout_alert_redeem);
                    dialog.setTitle("DIOOL");


                    Button mSendButton = (Button) dialog.findViewById(R.id.reddem_button);
                    final EditText mText = (EditText) dialog.findViewById(R.id.text_montant_redeem);
                    mText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

                    mSendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mText.getText().toString().equals("")) {
                                Toast.makeText(CashinActivity.this, "Le montant doit être > 0", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (!MMMUtils.isInteger(mText.getText().toString())) {
                                Toast.makeText(CashinActivity.this, "Le montant doit être un nombre entier", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                dialog.dismiss();
                                getRedeem(mText.getText().toString());
                            }
                        }
                    });
                    dialog.show();

                } else
                    Toast.makeText(CashinActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();

            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


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
        getSupportActionBar().setTitle(R.string.cashinmenu);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Dépôts" + "</font>")));


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
                Intent intent = new Intent(CashinActivity.this,
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
                /*if (MMMUtils.isConnectedTointernet(AddFundsActivity.this))
                    getMerchantBalance();
                return true;*/

            default:
                return true;
        }
    }


    private boolean validateTransferArgentForm() {
       /* if (!isOperatorSelected) {
            Toast.makeText(CashinActivity.this, "Opérateur non Spécifié", Toast.LENGTH_SHORT).show();

            return false;
        } else*/ if (txtAmount.getText().length() <= 0) {
            Toast.makeText(CashinActivity.this, "Montant non Spécifié", Toast.LENGTH_SHORT).show();

            return false;
        } else if (txtMsisdn.getText().length() < getResources().getInteger(R.integer.number_length)) {
            Toast.makeText(CashinActivity.this, "Numéro non valide", Toast.LENGTH_SHORT).show();

            return false;
        } else
            return true;

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


    private void startTransfertArgent() throws Exception {
        Log.e("aaaa", encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", "")).toString());

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(CashinActivity.this, R.style.myDialog))
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
        params.put("senderProviderIdentifier", "DIOOL");
        params.put("senderServiceType", "MOBILE_MONEY");
        params.put("senderProviderAccountID", "");
      //  params.put("senderUserIdentifier", shared.getString("USER_EMAIL_MERCHANT","").toString());
        params.put("senderUserIdentifier",mUserProfile.getId());
        params.put("amount", txtAmount.getText().toString());
        params.put("recipientProviderIdentifier", mnoID);
        params.put("recipientServiceType", "MOBILE_MONEY");
        params.put("recipientProviderAccountID", "237" + txtMsisdn.getText().toString());
        params.put("recipientUserIdentifier", "");
        params.put("senderComment", "");
        params.put("senderLongitude", "");
        params.put("senderLatitude", "");
        params.put("senderDeviceID", "");
        params.put("beVersion", getString(R.string.buck_end_version));
        try{
            params.put("shopId", encryption.decrypt(mUserToken.getIdToken(),shared.getString("SHOP_ID", "")));
        }catch (Exception e){
            e.printStackTrace();
        }
        params.put("channel","Mobile");
        params.put("issuerParents",issuerParents);


        Log.e("params CASFIN", params.toString() + "");

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_TRNSFERT, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        final TransfertArgentResponse mTransfertArgentResponse = new TransfertArgentResponse().createTransfertObject(response);

                        if (String.valueOf(mTransfertArgentResponse.getCode()).equals("0")) {

                            getBalance();

                            if (shared.getBoolean("Saving_Pdf", false)) {
                                if (MMMUtils.launchRequestPermissionProcess(CashinActivity.this)) {

                                } else {
                                    try {
                                        createPdf(txtAmount.getText().toString(), txtMsisdn.getText().toString());
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                        Toast.makeText(CashinActivity.this, "Echec dans lors du sauvegarde de la fiche opération !", Toast.LENGTH_SHORT).show();
                                    } catch (DocumentException e) {
                                        e.printStackTrace();
                                        Toast.makeText(CashinActivity.this, "Echec dans lors du sauvegarde de la fiche opération !", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            SweetAlertDialog mSweet =
                                    new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("DIOOL")
                                            .setContentText(mTransfertArgentResponse.getMessage());


                            mSweet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });

                            mSweet.show();

                          //  txtMsisdn.setText("");
                          //  txtAmount.setText("");


                            Properties transferProperties = new Properties();
                            transferProperties.put("user_id",mUserProfile.getId());
                            transferProperties.put("req_amount",txtAmount.getText().toString());
                            transferProperties.put("req_channel","mobile");
                            transferProperties.put("req_recipient_provider_account_id","237" + txtMsisdn.getText().toString());
                            transferProperties.put("req_sender_provider_account_id","");
                            transferProperties.put("senderProviderIdentifier","DIOOL");
                            transferProperties.put("",mUserProfile.getId());
                            Analytics.with(CashinActivity.this).track("transfer",transferProperties);
                            textInfo.setVisibility(View.GONE);
                            operatorssRadio.setVisibility(View.GONE);
                            mnoID = null;


                        }  else if(String.valueOf(mTransfertArgentResponse.getCode()).equals("27")){
                            new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.WARNING_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mTransfertArgentResponse.getMessage())
                                    .setCancelText("Cancel")
                                    .setConfirmText("Confirm")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            ///// send transfer after confirmation
                                            try {
                                                startConfirmedTransfertArgent();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }


                                        }
                                    })
                                    .show();


                        }else if(String.valueOf(mTransfertArgentResponse.getCode()).equals("53")){

                            new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mTransfertArgentResponse.getMessage())
                                    .show();
                            textInfo.setVisibility(View.VISIBLE);
                            operatorssRadio.setVisibility(View.VISIBLE);
                           // providerIdentifier ="62401";

                        } else {
                            new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mTransfertArgentResponse.getMessage())
                                    .show();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                try {
                    Log.e("Autorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    headers.put("Authorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return headers;
            }


        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, CashinActivity.class.getCanonicalName());
    }







    ////send confirmed transfer


    private void startConfirmedTransfertArgent() throws Exception {
        Log.e("aaaa", shared.getString("USER_EMAIL_MERCHANT","").toString());

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(CashinActivity.this, R.style.myDialog))
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
        params.put("senderProviderIdentifier", "DIOOL");
        params.put("senderServiceType", "MOBILE_MONEY");
        params.put("senderProviderAccountID", "");
        //  params.put("senderUserIdentifier", shared.getString("USER_EMAIL_MERCHANT","").toString());
        params.put("senderUserIdentifier",mUserProfile.getId());
        params.put("amount", txtAmount.getText().toString());
        params.put("recipientProviderIdentifier", mnoID);
        params.put("recipientServiceType", "MOBILE_MONEY");
        params.put("recipientProviderAccountID", "237" + txtMsisdn.getText().toString());
        params.put("recipientUserIdentifier", "");
        params.put("senderComment", "");
        params.put("senderLongitude", "");
        params.put("senderLatitude", "");
        params.put("senderDeviceID", "");
        params.put("beVersion", getString(R.string.buck_end_version));
        try{
            params.put("shopId", encryption.decrypt(mUserToken.getIdToken(),shared.getString("SHOP_ID", "")));
        }catch (Exception e){
            e.printStackTrace();
        }
        params.put("channel","Mobile");
        params.put("issuerParents",issuerParents);
        params.put("confirm",true);


        Log.e("params CASFIN", params.toString() + "");

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_TRNSFERT, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        final TransfertArgentResponse mTransfertArgentResponse = new TransfertArgentResponse().createTransfertObject(response);

                        if (String.valueOf(mTransfertArgentResponse.getCode()).equals("0")) {

                            getBalance();

                            if (shared.getBoolean("Saving_Pdf", false)) {
                                if (MMMUtils.launchRequestPermissionProcess(CashinActivity.this)) {

                                } else {
                                    try {
                                        createPdf(txtAmount.getText().toString(), txtMsisdn.getText().toString());
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                        Toast.makeText(CashinActivity.this, "Echec dans lors du sauvegarde de la fiche opération !", Toast.LENGTH_SHORT).show();
                                    } catch (DocumentException e) {
                                        e.printStackTrace();
                                        Toast.makeText(CashinActivity.this, "Echec dans lors du sauvegarde de la fiche opération !", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            SweetAlertDialog mSweet =
                                    new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("DIOOL")
                                            .setContentText(mTransfertArgentResponse.getMessage());


                            mSweet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });

                            mSweet.show();

                          //  txtMsisdn.setText("");
                          //  txtAmount.setText("");

                            Properties transferProperties = new Properties();
                            transferProperties.put("user_id",mUserProfile.getId());
                            transferProperties.put("req_amount",txtAmount.getText().toString());
                            transferProperties.put("req_channel","mobile");
                            transferProperties.put("req_recipient_provider_account_id","237" + txtMsisdn.getText().toString());
                            transferProperties.put("req_sender_provider_account_id","");
                            transferProperties.put("senderProviderIdentifier","DIOOL");
                            transferProperties.put("",mUserProfile.getId());
                            Analytics.with(CashinActivity.this).track("transfer",transferProperties);
                            textInfo.setVisibility(View.GONE);
                            operatorssRadio.setVisibility(View.GONE);
                            mnoID = null;

                        }else if(String.valueOf(mTransfertArgentResponse.getCode()).equals("53")){

                            new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mTransfertArgentResponse.getMessage())
                                    .show();
                            textInfo.setVisibility(View.VISIBLE);
                            operatorssRadio.setVisibility(View.VISIBLE);
                            // providerIdentifier ="62401";

                        } else {
                            new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mTransfertArgentResponse.getMessage())
                                    .show();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                try {
                    Log.e("Autorization", mUserToken.getType() + " " +encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    headers.put("Authorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return headers;
            }


        };

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, CashinActivity.class.getCanonicalName());
    }










    private void getRedeem(String mAmount) {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(CashinActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();

        // Sending parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("amount", mAmount);

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_REDEEM, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        SendRedeemResponse mRedeem = new SendRedeemResponse().createRedeem(response);

                        if (String.valueOf(mRedeem.getCode()).equals("0")) {
                            new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage())
                                    .show();

                            getBalance();
                        } else {
                            new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                new SweetAlertDialog(CashinActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, CashinActivity.class.getCanonicalName());
    }


    private void createHeadings(PdfContentByte cb, float x, float y, String text) {

        cb.beginText();
        cb.setFontAndSize(bfBold, 8);
        cb.setTextMatrix(x, y);
        cb.showText(text.trim());
        cb.endText();

    }


    private void createPdf(String mMontant, String mNumero) throws Exception {

        try {
            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

        } catch (DocumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Echec dans lors du sauvegarde de la fiche opération !", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Echec dans lors du sauvegarde de la fiche opération !", Toast.LENGTH_SHORT).show();

        }

        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MMM");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.e(TAG, "Pdf Directory created");
        }

        //Create time stamp
        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        myFile = new File(pdfFolder + timeStamp + ".pdf");

        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        Document document = new Document();

        //Step 2
        PdfWriter docWriter = PdfWriter.getInstance(document, output);

        //Step 3
        document.open();

        //Step 4 Add content
        //list all the products sold to the customer
        float[] columnWidths = {0.5f, 0.5f, 0.5f, 0.5f, 0.5f};
        //create PDF table with the given widths
        PdfPTable table = new PdfPTable(columnWidths);
        // set table width a percentage of the page width
        table.setTotalWidth(500f);


        PdfPCell cell = new PdfPCell(new Phrase("Date"));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Numéro"));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Opérateur"));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Montant"));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("Opération"));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
        table.setHeaderRows(1);


        // Init Date
        SimpleDateFormat showinDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        Calendar newCalendar = Calendar.getInstance();
        table.addCell(showinDateFormat.format(newCalendar.getTime()));

        // Init Numéro
        table.addCell(mNumero);
        // Init Opérateur
        if (GlobalConstants.OCM_VALUE.equals(mnoID))
            table.addCell("Orange");
        else if (GlobalConstants.MTNC_VALUES.equals(mnoID))
            table.addCell("MTN");
        if (GlobalConstants.NXTL_VALUE.equals(mnoID))
            table.addCell("NXTL");

        table.addCell(mMontant);
        table.addCell("CASHIN");

        //absolute location to print the PDF table from
        PdfContentByte mContentByte = docWriter.getDirectContent();
        table.writeSelectedRows(0, -1, document.leftMargin(), 650, mContentByte);

        //creating a sample invoice with some customer data
        createHeadings(mContentByte, 400, 780, "Utilisateur: " +  encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", "")));
        createHeadings(mContentByte, 400, 765, "Service Client : helpdesk@mymoneymobile.com");


        // Adding MMM Logo
        try {
            InputStream inputStream = getAssets().open("logowithtext.png");
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image companyLogo = null;
            companyLogo = Image.getInstance(stream.toByteArray());
            companyLogo.setAbsolutePosition(25, 700);
            companyLogo.scalePercent(25);
            document.add(companyLogo);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //Step 5: Close the document
        document.close();
        Toast.makeText(this, "La fiche du transfer est sauvegardée avec succés!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        MyMoneyMobileApplication.getInstance().touch();
        Log.d(TAG, "User interaction to " + this.toString());
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
                        mTextRevenu.setText(mBalnceResponse.getRevenueAccountBalance() + "");


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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, CashinActivity.class.getCanonicalName());
    }

}
