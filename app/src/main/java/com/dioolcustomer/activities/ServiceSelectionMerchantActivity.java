package com.dioolcustomer.activities;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.dioolcustomer.models.MobileAppVersion;
import com.dioolcustomer.models.ShopsByOwnerId;
import com.dioolcustomer.ui.AlertDialogRadio;
import com.dioolcustomer.webservice.CheckMobileAppVersion;
import com.google.gson.Gson;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.BalanceResponse;
import com.dioolcustomer.models.SendRedeemResponse;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.intercom.android.sdk.Intercom;
import io.intercom.android.sdk.identity.Registration;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see
 */
public class ServiceSelectionMerchantActivity extends ActionBarActivity implements AlertDialogRadio.AlertPositiveListener
        ,AlertDialogRadio.AlertNegativeListener{
    public final String TAG = getClass().getSimpleName();


    Encryption encryption = new Encryption();

    //AlertDialog.Builder builder;
    Gson gson;
    Token mUserToken;
    UserProfile mUserProfile;
    String daBalance = "";
    String revBalance = "";

    TextView mUserNameText;
    TextView mTextBalance, mTextRevenu;
    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    BroadcastReceiver broadcastReceiverLogout;
    JsonObjectRequest jsonObjReq;
    StringRequest stringRequest;
    MobileAppVersion mMobileAppVersion = new MobileAppVersion();

    private ShopsByOwnerId mShops;
    //AlertDialog mAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setContentView(R.layout.activity_service_selection);

//        Analytics.with(this).screen("View", TAG);

        //Prepare Preferences
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();

        String jsonToken = null;
        try {
            jsonToken = shared.getString("USER_TOKEN", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        gson = new Gson();
        mUserToken = gson.fromJson(jsonToken, Token.class);

        String jsonProfile = null;






        CheckMobileAppVersion checkMobileAppVersion  = new CheckMobileAppVersion();
        try {
          //  checkVersion(mUserToken);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            jsonProfile = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_PROFILE", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUserProfile = gson.fromJson(jsonProfile, UserProfile.class);




        Intercom.initialize(MyMoneyMobileApplication.getInstance(), GlobalConstants.ANONYMOUS_ID2, GlobalConstants.ANONYMOUS_ID3);
       //// Intercom.client().registerIdentifiedUser(new Registration().withUserId( shared.getString("USER_NAME_MERCHANT","") + shared.getString("USER_LASTNAME_MERCHANT","")));
        try {
            Intercom.client().registerIdentifiedUser(new Registration().withUserId(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID", ""))  ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mUserNameText= (TextView) findViewById(R.id.user_name_text);
        try {
            mUserNameText.setText(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button airtimeBtn = (Button) findViewById(R.id.airtimebutton);
        Button cashinBtn = (Button) findViewById(R.id.cashinbutton);
        Button cashoutBtn = (Button) findViewById(R.id.cashoutbutton);
        Button paymentBtn = (Button) findViewById(R.id.paymentbutton);
        Button historyButton = (Button) findViewById(R.id.history_button);
        Button m3DioolButton = (Button) findViewById(R.id.transfert_diool_button);
        Button mRecuperationsButton = (Button) findViewById(R.id.history_redeem_button);
        Button mNetworkButton = (Button) findViewById(R.id.network_button);
        Button mAddFundsButton = (Button) findViewById(R.id.add_funds_button);
        Button mWithdrawFundsButton = (Button) findViewById(R.id.withdraw_funds_button);
        Button mQuoteRequestButton = (Button) findViewById(R.id.quote_request_button);
        //Button mPaymentButton = (Button) findViewById(R.id.)


        try {
            if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("super_merchant"))
                mNetworkButton.setVisibility(View.VISIBLE);
            else
                mNetworkButton.setVisibility(View.GONE);


            if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("merchant_airtime") || encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("customer")){
               cashoutBtn.setVisibility(View.GONE);
                if(!encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("customer")){
                    cashinBtn.setVisibility(View.GONE);
                }
                if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("customer")){
                    m3DioolButton.setVisibility(View.GONE);
                }
                paymentBtn.setVisibility(View.GONE);
            }else if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("merchant_payment")){
                cashoutBtn.setVisibility(View.GONE);
                cashinBtn.setVisibility(View.GONE);
            }


            //System.out.println(encryption.decrypt("USER_TYPE_MERCHANT : "+mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("super_merchant"));
            if(!encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("super_merchant")) {
               System.out.println("ASSIGNING_MODE : " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("ASSIGNING_MODE", "")));
                 if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("ASSIGNING_MODE", "")).equals("login")) {
                    startGetShopsByOwner();
                }
            }

            } catch (Exception e) {
            e.printStackTrace();
        }


        mTextBalance = (TextView) findViewById(R.id.textView_da_balance);
        mTextRevenu = (TextView) findViewById(R.id.textView_rev_balance);


            daBalance = (shared.getString("daBalance", ""));
            revBalance = (shared.getString("revBalance", ""));


        mTextBalance.setText(daBalance + "");
        mTextRevenu.setText(revBalance + "");


        mNetworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceSelectionMerchantActivity.this,
                        ListNetworkActivity.class);
                startActivity(intent);
            }
        });


        mRecuperationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceSelectionMerchantActivity.this,
                        RedeemHistoryActivity.class);
                startActivity(intent);
            }
        });


        airtimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceSelectionMerchantActivity.this,
                        AirtimeActivity.class);
                startActivity(intent);
                //finish();

            }
        });


        m3DioolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceSelectionMerchantActivity.this,
                        TransfertSoldeDioolActivity.class);
                startActivity(intent);
                //finish();

            }
        });
        cashinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ServiceSelectionMerchantActivity.this,
                        CashinActivity.class);
                startActivity(intent);
                //finish();


            }
        });
        cashoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceSelectionMerchantActivity.this,
                        CashoutActivity.class);
                startActivity(intent);
                //finish();


            }
        });


        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceSelectionMerchantActivity.this,
                        PaymentActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        mAddFundsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceSelectionMerchantActivity.this,
                        AddFundsActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        mWithdrawFundsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceSelectionMerchantActivity.this,
                        WithdrawFundsActivity.class);
                startActivity(intent);
                //finish();
            }
        });


        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceSelectionMerchantActivity.this, TransactionsHistoryActivity.class);
                startActivity(intent);
            }
        });

        mQuoteRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    System.out.println(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")));
                    if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("merchant_airtime") || encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("customer")){
                        //Intent intent = new Intent(ServiceSelectionMerchantActivity.this, TestMapActivity.class);
                        Intent intent = new Intent(ServiceSelectionMerchantActivity.this, MairchentAirtimeQuoteRequestActivity.class);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(ServiceSelectionMerchantActivity.this, QuoteRequestActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Intent intent = new Intent(ServiceSelectionMerchantActivity.this, TestMapActivity.class);


            }
        });


        Button redeemButton = (Button) findViewById(R.id.buttonRedeem);
        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MMMUtils.isConnectedTointernet(ServiceSelectionMerchantActivity.this)) {
                    final Dialog dialog = new Dialog(ServiceSelectionMerchantActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.layout_alert_redeem);
                    dialog.setTitle("My Msoney Mobile");

                    Button mSendButton = (Button) dialog.findViewById(R.id.reddem_button);
                    final EditText mText = (EditText) dialog.findViewById(R.id.text_montant_redeem);
                    mText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                    mSendButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mText.getText().toString().equals("")) {
                                Toast.makeText(ServiceSelectionMerchantActivity.this, "Le montant doit être > 0", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (!MMMUtils.isInteger(mText.getText().toString())) {
                                Toast.makeText(ServiceSelectionMerchantActivity.this, "Le montant doit être un nombre entier", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                dialog.dismiss();
                                getRedeem(mText.getText().toString());
                            }
                        }
                    });

                    dialog.show();


                } else
                    Toast.makeText(ServiceSelectionMerchantActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();

            }
        });


        if (MMMUtils.isConnectedTointernet(ServiceSelectionMerchantActivity.this))
            getMerchantBalance();
        else {

            mTextBalance.setText(daBalance + "");
            mTextRevenu.setText(revBalance + "");

        }


        broadcastReceiverLogout = MMMUtils.registerLogoutBroadcastReceiver(this);


       /* try {
            System.out.println(encryption.decrypt("USER_TYPE_MERCHANT : "+mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("super_merchant"));
            if(!encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("super_merchant")){
                System.out.println("ASSIGNING_MODE : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("ASSIGNING_MODE", "")));
                if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("ASSIGNING_MODE", "")).equals("login")){
                    startGetShopsByOwner();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

       getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("gg", "gg");

            mTextBalance.setText(shared.getString("daBalance", ""));
            mTextRevenu.setText(shared.getString("revBalance", ""));

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
                Intent intent = new Intent(ServiceSelectionMerchantActivity.this,
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
                if (MMMUtils.isConnectedTointernet(ServiceSelectionMerchantActivity.this))
                    getMerchantBalance();
                return true;

            default:
                return true;
        }
    }

    private void launchSharing() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Vous êtes un Marchand. Effectuez toutes les opérations dans votre boutique avec n'importe quel abonné mobile. Retraits, Transferts, Depôts. Disponible sur Android en utilisant votre connexion 2G/3G/4G. Télécharger " +
                "l'application DIOOL depuis Google play Store: https://play.google.com/store/apps/details?id=com.mymoneymobile&hl=fr ou Inscrivez-vous depuis ce lien:  https://emonize.mymoneytop.com/m3-home/";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "DIOOL");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiverLogout);
        Intercom.client().reset();
    }


    private void getMerchantBalance() {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(ServiceSelectionMerchantActivity.this, R.style.myDialog))
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
                            Toast.makeText(ServiceSelectionMerchantActivity.this, mBalnceResponse.getMessage(), Toast.LENGTH_LONG).show();
                            MMMUtils.logoutUser(ServiceSelectionMerchantActivity.this);
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


    private void getRedeem(String mAmount) {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(ServiceSelectionMerchantActivity.this, R.style.myDialog))
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

                        if(String.valueOf(mRedeem.getCode()).equals("0"))
                        {
                            new SweetAlertDialog(ServiceSelectionMerchantActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage())
                                    .show();

                            getMerchantBalance();
                        }

                        else
                        {
                            new SweetAlertDialog(ServiceSelectionMerchantActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                new SweetAlertDialog(ServiceSelectionMerchantActivity.this, SweetAlertDialog.ERROR_TYPE)
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




    public void checkVersion( final Token mUserToken) throws Exception {
        // Personalize View when loading WS


        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        stringRequest = new StringRequest(Request.Method.GET,
                GlobalConstants.GET_APP_VERSION,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.e(TAG, response.toString());

                           // mMobileAppVersion = new MobileAppVersion().createMobileAppVersion(response);
                            System.out.println(response);
                        System.out.println(getString(R.string.app_version));
                        if(!getString(R.string.app_version).equals(response)){
                            Toast.makeText(ServiceSelectionMerchantActivity.this, "Télécharger la nouvelle version SVP", Toast.LENGTH_LONG).show();
                            MMMUtils.logoutUser(ServiceSelectionMerchantActivity.this);
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());

            }
        }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("cache-control","no-cache");
                headers.put("Access-Control-Allow-Origin","*");
                try {
                    //System.out.println("encryption.decrypt(mUserToken.getIdToken(),shared.getString(\"USER_ID_TOKEN\", \"\")) : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                    headers.put("Authorization", mUserToken.getType() + " " + mUserToken.getIdToken());
                    System.out.println();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return headers;
            }


        };



        /// Adding request to request queue
      //  MyMoneyMobileApplication.getInstance().addToRequestQueue(stringObjReq, ServiceSelectionMerchantActivity.class.getCanonicalName());
        queue.add(stringRequest);



    }





    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        MyMoneyMobileApplication.getInstance().touch();
        Log.d(TAG, "User interaction to " + this.toString());
    }



    public void startGetShopsByOwner() throws Exception{

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(ServiceSelectionMerchantActivity.this, R.style.myDialog))
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
        params.put("ownerId",issuerParents[issuerParents.length - 1]);
        params.put("shopAssignations", false);
        JSONObject json  = new JSONObject(params);
        System.out.println(json.toString());
        System.out.println( String.valueOf(json));
        //System.out.println( String.valueOf(json.));

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_GET_SHOPS_BY_OWNER, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        mShops = new ShopsByOwnerId().createShopsByOwnerId(response);

                        if(String.valueOf(mShops.getCode()).equals("0"))
                        {
                           /* new SweetAlertDialog(ServiceSelectionMerchantActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mShops.getMessage())
                                    .show();*/

                           if(mShops.getMListShop().size() == 0){
                               MMMUtils.logoutUser(ServiceSelectionMerchantActivity.this);
                               Toast.makeText(ServiceSelectionMerchantActivity.this, "Vous devez avoir au moins un shop à sélectionner",
                                       Toast.LENGTH_LONG).show();
                           }else{
                               String[] strShop = new String[mShops.getMListShop().size()];
                               for(int i = 0; i < mShops.getMListShop().size(); i++){
                                   strShop[i] = mShops.getMListShop().get(i).getName();
                               }

                               AlertDialogRadio.setCode(strShop);
                               /** Getting the fragment manager */
                               FragmentManager manager = getFragmentManager();

                               /** Instantiating the DialogFragment class */
                               AlertDialogRadio alert = new AlertDialogRadio();

                               /** Creating a bundle object to store the selected item's index */
                               Bundle b  = new Bundle();

                               /** Storing the selected item's index in the bundle object */
                               b.putInt("position", position);

                               /** Setting the bundle object to the dialog fragment object */
                               alert.setArguments(b);

                               /** Creating the dialog fragment object, which will in turn open the alert dialog window */
                               alert.show(manager, "alert_dialog_radio");
                           }




                        }

                        else
                        {
                            new SweetAlertDialog(ServiceSelectionMerchantActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mShops.getMessage())
                                    .show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(ServiceSelectionMerchantActivity.this, SweetAlertDialog.ERROR_TYPE)
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















////////////////////////////////////////assign shop for merchant //////////////////////////////////////////


    public void startAssignShopForMerchant() throws Exception{

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(ServiceSelectionMerchantActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();




        // Sending parameters
        Map<String, Object> params = new HashMap<String, Object>();
       // params.put("owner_id",issuerParents[issuerParents.length - 1]);
        params.put("merchantId", encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID", "")));
        params.put("shopId", mShops.getMListShop().get(position).getId());
        params.put("deassignOthers", false);

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_ASSIGN_SHOP_FOR_MERCHANT, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        ShopsByOwnerId mShops = new ShopsByOwnerId().createShopsByOwnerId(response);

                        if(String.valueOf(mShops.getCode()).equals("200"))
                        {
                            new SweetAlertDialog(ServiceSelectionMerchantActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mShops.getMessage())
                                    .show();


                        }

                        else
                        {
                            new SweetAlertDialog(ServiceSelectionMerchantActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mShops.getMessage())
                                    .show();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(ServiceSelectionMerchantActivity.this, SweetAlertDialog.ERROR_TYPE)
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














    /** Stores the selected item's position */
    int position = 0;

    @Override
    public void onPositiveClick(int position) {

        this.position = position;

        try {
            startAssignShopForMerchant();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNegativeClick(int position) {
        MMMUtils.logoutUser(this);
    }
}
