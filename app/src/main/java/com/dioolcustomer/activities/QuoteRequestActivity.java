package com.dioolcustomer.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.core.Token;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.adapters.MerchantQuoteRequestAdapter;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.constants.GlobalStatic;
import com.dioolcustomer.models.BalanceResponse;
import com.dioolcustomer.models.SendRedeemResponse;
import com.dioolcustomer.models.quoterequest.QuoteRequestRequest;
import com.dioolcustomer.models.quoterequest.QuoteRequestResponse;
import com.dioolcustomer.models.quoterequest.RegisterForQuoteRequest;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;
import com.dioolcustomer.utils.QuoteRequestWaiter;
import com.dioolcustomer.utils.Waiter;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.intercom.android.sdk.Intercom;

public class QuoteRequestActivity extends AppCompatActivity {

    public String TAG = getClass().getSimpleName();

    String daBalance = "";
    String revBalance = "";

    Button  cancelButton,redeemButton;
    private Switch mSwitch;
    private ArrayList<QuoteRequestRequest> mQouteRequestList = new ArrayList<QuoteRequestRequest>();

    EditText txtMsisdn;
    TextView txtDaBalance,mTextRevenu;
    TextView txtRevBalance;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    MerchantQuoteRequestAdapter mMerchantQuoteRequestAdapter;

    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    Token mUserToken;
    Gson gson;
    //String idToken;

    JsonObjectRequest jsonObjReq;


    private QuoteRequestWaiter waiter;


    GlobalStatic globalStatic = new GlobalStatic();
    Encryption encryption = new Encryption();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_request);


        mSwitch = (Switch) findViewById(R.id.switch_quote_request);

        txtMsisdn = (EditText) findViewById(R.id.payment_msisdn_edit);
        txtDaBalance = (TextView) findViewById(R.id.textView_da_balance);
        txtRevBalance = (TextView) findViewById(R.id.textView_rev_balance);
        mTextRevenu = (TextView) findViewById(R.id.textView_rev_balance);
        cancelButton = (Button) findViewById(R.id.buttonCancel);
        redeemButton = (Button) findViewById(R.id.buttonRedeem);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MyMoneyMobileApplication myMoneyMobileApplication = new MyMoneyMobileApplication();
                try{
                    MyMoneyMobileApplication.getInstance().cancelPendingRequests(TAG);
                }catch (Exception e){

                }

                finish();
            }
        });


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_quote_request);

        try {
//            daBalance = (shared.getString("daBalance", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
      //      revBalance = (shared.getString("revBalance", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }


        txtDaBalance.setText(daBalance + "");
        txtRevBalance.setText(revBalance + "");


        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_quote_request);
       // mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();

        gson = new Gson();
        String jsonToken = shared.getString("USER_TOKEN", "");
        mUserToken = gson.fromJson(jsonToken, Token.class);
       // idToken = shared.getString("USER_ID_TOKEN", "");

      /*  @Override
        public void onCheckedChanged(CompoundButton buttonView,
        boolean isChecked) {

            if(isChecked){
                switchStatus.setText("Switch is currently ON");
            }else{
                switchStatus.setText("Switch is currently OFF");
            }

        }*/
        verifEnregistrationForQuoteRequest();
        if(globalStatic.isQuoteRequestListChecked()){
           mSwitch.setChecked(true);
            globalStatic.setMListQuoteRequestCopy(new ArrayList<QuoteRequestResponse>());
            globalStatic.setSizeListQuoteRequest(0);
            mQouteRequestList = new ArrayList<>();
            getQuoteRequests();
        }
        System.out.println("la vie est belle");
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {

                    if (bChecked) {
                        // textView2.setText(switchOn);
                     //   if(!globalStatic.isQuoteRequestListChecked()){
                            System.out.println("the switch is cheked");
                            globalStatic.setQuoteRequestListChecked(true);
                            startRegisterForQuoteRequest();
                     /*   }else{

                            getQuoteRequests();
                        }*/


                    } else {
                        // textView2.setText(switchOff);
                        // System.out.println("the switch is not cheked");

                        startUnregisterForQuoteRequest();

                    }


            }
        });
        //startRegisterForQuoteRequest();
        //getQuoteRequests();

        //getQuoteRequests();

        /* QuoteRequestRequest quoteRequestRequest = new QuoteRequestRequest();
        quoteRequestRequest.setMerchantFirstName("Ahmed");
        quoteRequestRequest.setMerchantLastName("Ben Mansour");
        quoteRequestRequest.setServiceType("Deposit");
        quoteRequestRequest.setAmount(5000F);

       mQouteRequestList.add(quoteRequestRequest);
        mMerchantQuoteRequestAdapter = new MerchantQuoteRequestAdapter(mQouteRequestList, QuoteRequestActivity.this);

        mRecyclerView.setAdapter(mMerchantQuoteRequestAdapter);*/

        getBalance();

        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MMMUtils.isConnectedTointernet(QuoteRequestActivity.this)) {
                    final Dialog dialog = new Dialog(QuoteRequestActivity.this);
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
                                Toast.makeText(QuoteRequestActivity.this, "Le montant doit être > 0", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (!MMMUtils.isInteger(mText.getText().toString())) {
                                Toast.makeText(QuoteRequestActivity.this, "Le montant doit être un nombre entier", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                dialog.dismiss();
                                getRedeem(mText.getText().toString());
                            }
                        }
                    });
                    dialog.show();

                } else
                    Toast.makeText(QuoteRequestActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();

            }
        });


        new Thread(new Runnable() {

            private final String TAG = Waiter.class.getName();
            private long lastUsed;
            private long period;
            private boolean stop = false;

            public synchronized void touch() {
                lastUsed = System.currentTimeMillis();
            }
            public synchronized void forceInterrupt() {

            }

            // soft stopping of thread
            public synchronized void stopTimer() {
                stop = true;

            }


            public synchronized void setPeriod(long period) {
                this.period = period;
            }
            @Override
            public void run() {

                long idle = 0;
                this.touch();
                do {
                    idle = System.currentTimeMillis() - lastUsed;
                    Log.e(TAG, "Application is idle for " + idle + " ms");
                    try {
                        Thread.sleep(30000); // check every 5 seconds
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Waiter interrupted!");
                    }
                    if (idle > 30000) {
                        idle = 0;
                        // do something here - e.g. call popup or so
                        getQuoteRequests();
                        Log.e("Finish","zzz");
                    }
                } while (!stop);
                Log.d(TAG, "Finishing Waiter thread");

            }
        }).start();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Quote Request" + "</font>")));
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
                Intent intent = new Intent(QuoteRequestActivity.this,
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




   /* public  void startGetQuoteRequests() throws InterruptedException {

        while(globalStatic.isQuoteRequestListChecked()){

            System.out.println("startGetQuoteRequests");
          //  Thread.sleep(60000);

            getQuoteRequests();
        }

    }*/










    public void startRegisterForQuoteRequest() {


        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(QuoteRequestActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();

        // Sending parameters
        final Map<String, Object> params = new HashMap<String, Object>();

        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                GlobalConstants.URL_REGISTER_FOR_QUOTE_REQUEST, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();

                        try {


                        RegisterForQuoteRequest mRegisterForQuoteRequest = new RegisterForQuoteRequest().createRegisterForQuoteRequest(response);;



                            if(mRegisterForQuoteRequest.getCode() != 0){
                                new SweetAlertDialog(QuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("DIOOL")
                                        .setContentText(mRegisterForQuoteRequest.getMessage())
                                        .show();

                                mSwitch.setChecked(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(QuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, PaymentActivity.class.getCanonicalName());
    }





    public void startUnregisterForQuoteRequest() {


        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(QuoteRequestActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();

        // Sending parameters
        final Map<String, Object> params = new HashMap<String, Object>();

        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                GlobalConstants.URL_UNREGISTER_FOR_QUOTE_REQUEST, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();

                        try {


                        RegisterForQuoteRequest mRegisterForQuoteRequest = new RegisterForQuoteRequest().createRegisterForQuoteRequest(response);



                            if(mRegisterForQuoteRequest.getCode() == 0){
                                ArrayList<QuoteRequestResponse> mListQuoteRequest = new ArrayList<>();



                                mMerchantQuoteRequestAdapter = new MerchantQuoteRequestAdapter(mListQuoteRequest, QuoteRequestActivity.this,mUserToken,shared);

                                mRecyclerView.setAdapter(mMerchantQuoteRequestAdapter);
                                globalStatic.setQuoteRequestListChecked(false);
                            }else{
                                new SweetAlertDialog(QuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("DIOOL")
                                        .setContentText(mRegisterForQuoteRequest.getMessage())
                                        .show();
                                mSwitch.setChecked(true);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                      //  SendRedeemResponse mRedeem = new SendRedeemResponse().createRedeem(response);

                      //  if (String.valueOf(mRedeem.getCode()).equals("0")) {}







                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(QuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                    headers.put("Authorization", mUserToken.getType() + " " +encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
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






///////////////////////////getresponse//////////////////////////////


    public void getQuoteRequests() {

        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                GlobalConstants.URL_GET_QUOTE_REQUEST, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());

                        ArrayList<QuoteRequestResponse> mListQuoteRequest = new ArrayList<>();
                        mListQuoteRequest.clear();


                        try {
                            mListQuoteRequest = new QuoteRequestResponse().createQuoteRequestResponse(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(mListQuoteRequest != null){
                            if((mListQuoteRequest.size() != globalStatic.getSizeListQuoteRequest())){
                                boolean b =true;
                                int i = 1;


                                // for(int i =0; i< mListQuoteRequest.size(); i++){
                                /*for(int j = 0; j < globalStatic.getMListQuoteRequestCopy().size();j++){

                                }*/
                                if(globalStatic.getMListQuoteRequestCopy().size() == 0 || mListQuoteRequest.size() == 0){
                                    b = false;
                                }
                                while(b== true && i <= mListQuoteRequest.size() && globalStatic.getMListQuoteRequestCopy().size() !=0){
                                    System.out.println("mListQuoteRequest.size() : "+mListQuoteRequest.size());
                                    System.out.println("globalStatic.getMListQuoteRequestCopy().size() : "+globalStatic.getMListQuoteRequestCopy().size());
                                    try{
                                        if(!mListQuoteRequest.get(i).equals(globalStatic.getMListQuoteRequestCopy().get(i))){
                                            b=false;
                                        }

                                    }catch (Exception e){

                                    }
                                    i++;
                                }
                                if(!b){// && !globalStatic.isUseList()){
                                    System.out.println("globalStatic.getIdQRPassList().size() : "+globalStatic.getIdQRPassList().size());
                                    for(int k = 0; k < globalStatic.getIdQRPassList().size();k++){
                                        for(int  j = 0; j < mListQuoteRequest.size(); j++){
                                            if(globalStatic.getIdQRPassList().get(k).equals(mListQuoteRequest.get(j).getId())){
                                                mListQuoteRequest.remove(j);
                                            }
                                        }
                                    }
                                    System.out.println("globalStatic.getIdQRTokenList().size() : "+globalStatic.getIdQRTokenList().size());

                                    for(int k = 0; k < globalStatic.getIdQRTokenList().size();k++){
                                        for(int  j = 0; j < mListQuoteRequest.size(); j++){
                                            if(globalStatic.getIdQRTokenList().get(k).equals(mListQuoteRequest.get(j).getId())){
                                                mListQuoteRequest.remove(j);
                                            }
                                        }
                                    }

                                    mMerchantQuoteRequestAdapter = new MerchantQuoteRequestAdapter(mListQuoteRequest, QuoteRequestActivity.this,mUserToken,shared);

                                    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_quote_request);
                                    mRecyclerView.setAdapter(mMerchantQuoteRequestAdapter);
                                    globalStatic.setUseList(true);
                                    System.out.println("size : "+mListQuoteRequest.size());
                                    // mMerchantQuoteRequestAdapter.updateData(mListQuoteRequest);
                                    mMerchantQuoteRequestAdapter.notifyDataSetChanged();
                                    globalStatic.setMListQuoteRequestCopy(mListQuoteRequest);
                                    globalStatic.setSizeListQuoteRequest(mListQuoteRequest.size());
                                }
                           /* if(!b && globalStatic.isUseList()){
                                mMerchantQuoteRequestAdapter.updateData(mListQuoteRequest);
                              //  mMerchantQuoteRequestAdapter.notifyDataSetChanged();
                            }*/

                            }else{
                                boolean b =true;
                                int i = 0;
                                while(b== true && i <= mListQuoteRequest.size() && globalStatic.getMListQuoteRequestCopy().size() !=0){
                                    System.out.println("mListQuoteRequest.size() : "+mListQuoteRequest.size());
                                    System.out.println("globalStatic.getMListQuoteRequestCopy().size() : "+globalStatic.getMListQuoteRequestCopy().size());

                                    if(!mListQuoteRequest.get(i).equals(globalStatic.getMListQuoteRequestCopy().get(i))){
                                        b=false;
                                    }
                                    i++;
                                }

                                if(!b){// && !globalStatic.isUseList()){
                                    System.out.println("globalStatic.getIdQRPassList().size() : "+globalStatic.getIdQRPassList().size());

                                    for(int k = 0; k < globalStatic.getIdQRPassList().size();k++){
                                        System.out.println("globalStatic.getIdQRPassList() : "+globalStatic.getIdQRPassList().get(k));
                                        for(int  j = 0; j < mListQuoteRequest.size(); j++){
                                            System.out.println("mListQuoteRequest : "+mListQuoteRequest.get(j).getId());
                                            if(globalStatic.getIdQRPassList().get(k).equals(mListQuoteRequest.get(j).getId())){
                                                mListQuoteRequest.remove(j);
                                            }
                                        }
                                    }


                                    System.out.println("globalStatic.getIdQRTokenList().size() : "+globalStatic.getIdQRTokenList().size());

                                    for(int k = 0; k < globalStatic.getIdQRTokenList().size();k++){
                                        for(int  j = 0; j < mListQuoteRequest.size(); j++){
                                            if(globalStatic.getIdQRTokenList().get(k).equals(mListQuoteRequest.get(j).getId())){
                                                mListQuoteRequest.remove(j);
                                            }
                                        }
                                    }


                                    mMerchantQuoteRequestAdapter = new MerchantQuoteRequestAdapter(mListQuoteRequest, QuoteRequestActivity.this,mUserToken,shared);


                                    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_quote_request);
                                    mRecyclerView.setAdapter(mMerchantQuoteRequestAdapter);
                                    globalStatic.setUseList(true);
                                    //mMerchantQuoteRequestAdapter.updateData(mListQuoteRequest);
                                    mMerchantQuoteRequestAdapter.notifyDataSetChanged();
                                    globalStatic.setMListQuoteRequestCopy(mListQuoteRequest);
                                    globalStatic.setSizeListQuoteRequest(mListQuoteRequest.size());
                                }
                               /* if(!b && globalStatic.isUseList()){
                                mMerchantQuoteRequestAdapter.updateData(mListQuoteRequest);
                               // mMerchantQuoteRequestAdapter.notifyDataSetChanged();
                            }*/
                            }
                        }







                        /* QuoteRequestRequest quoteRequestRequest = new QuoteRequestRequest();
        quoteRequestRequest.setMerchantFirstName("Ahmed");
        quoteRequestRequest.setMerchantLastName("Ben Mansour");
        quoteRequestRequest.setServiceType("Deposit");
        quoteRequestRequest.setAmount(5000F);

       mQouteRequestList.add(quoteRequestRequest);
        mMerchantQuoteRequestAdapter = new MerchantQuoteRequestAdapter(mQouteRequestList, QuoteRequestActivity.this);

        mRecyclerView.setAdapter(mMerchantQuoteRequestAdapter);*/



                        if(GlobalStatic.isQuoteRequestListChecked()){
                           /* try {
                                Thread.sleep(60000);
;
                                getQuoteRequests();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/

                          /*  try {
                                synchronized (this){
                                    wait(60000);
                                    getQuoteRequests();
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }*/
                           // getQuoteRequests();

                        }



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
                   // headers.put("Authorization", "bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJJREVOVElUWSIsIkFDQ09VTlQiLCJNT05FVEFSWV9BQ0NPVU5UIiwiVFJBTlNGRVJfTU9CSUxFTU9ORVkiLCJQQVlNRU5UIiwiVFJBTlNGRVJfQUlSVElNRSIsIlRSQU5TRkVSX0RJT09MIiwiU1RBVFNfVFJBTlNGRVIiLCJGVU5EU19UUkFOU0ZFUiIsIlBBWU1FTlQiLCJRVU9URV9SRVFVRVNUX1JFU1BPTlNFIiwiQUNDT1VOVF9NR01UIiwiU0hPUF9NR01UIiwiU0hPUF9WSUVXIiwiU0hPUF9BU1NJR04iLCJUUkFOU0ZFUiIsIlJFREVFTV9CTE9DSyJdLCJ1c2VyX2lkIjoiYXV0aDB8NThiNTkzYWQ0NGU3MDMwYzBiZTE3OWQzIiwicHJvZmlsIjoic3VwZXJfbWVyY2hhbnQiLCJlbWFpbCI6Im5pemFya2xpYmlAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInVzZXJuYW1lIjoibml6YXJrbGliaSIsImlzcyI6Imh0dHBzOi8vbXltb25leW1vYmlsZS5ldS5hdXRoMC5jb20vIiwic3ViIjoiYXV0aDB8NThiNTkzYWQ0NGU3MDMwYzBiZTE3OWQzIiwiYXVkIjoicDNhcmNBT3VjaDJXUVg4ajV6TG5jYm5kUExrcTlZYjMiLCJleHAiOjE0OTI1NTI0ODUsImlhdCI6MTQ5MjUyMzY4NX0.lyuH2zkj5y7D61-BH253Fj2TTpkXIGJ7bZ6idI7fsQ8");
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





    ////////////////////select quote request////////////////////////////////


    public void startSelectQuoteRequest(String idRequest) {


        // Personalize View when loading WS
       /* final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();*/

        // Sending parameters
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("quoteRequestId",idRequest);

        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_SELECT_QUOTE_REQUEST, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.e(TAG, response.toString());
                        // dialog.dismiss();
                        //  SendRedeemResponse mRedeem = new SendRedeemResponse().createRedeem(response);

                        //  if (String.valueOf(mRedeem.getCode()).equals("0")) {}

                        System.out.println("le quote request est selectioné");





                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
               /* Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();*/

               /* new SweetAlertDialog(QuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DIOOL")
                        .setContentText("L'opération a échoué")
                        .show();*/
                System.out.println("erreur lors de la selection");
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
                    headers.put("Authorization", mUserToken.getType() + " " +encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, QuoteRequestResponse.class.getCanonicalName());
    }



    ////////////////////////////////////////// verif enregistration for quote request /////////////////////////////////////////////


    public void verifEnregistrationForQuoteRequest() {

        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                GlobalConstants.URL_GET_QUOTE_REQUEST, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());

                        ArrayList<QuoteRequestResponse> mListQuoteRequest = new ArrayList<>();
                        mListQuoteRequest.clear();


                        try {
                            mListQuoteRequest = new QuoteRequestResponse().createQuoteRequestResponse(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        System.out.println("mListQuoteRequest : "+mListQuoteRequest);
                        if(mListQuoteRequest != null){
                            mSwitch.setChecked(true);
                            getQuoteRequests();
                        }
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
                    // headers.put("Authorization", "bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJJREVOVElUWSIsIkFDQ09VTlQiLCJNT05FVEFSWV9BQ0NPVU5UIiwiVFJBTlNGRVJfTU9CSUxFTU9ORVkiLCJQQVlNRU5UIiwiVFJBTlNGRVJfQUlSVElNRSIsIlRSQU5TRkVSX0RJT09MIiwiU1RBVFNfVFJBTlNGRVIiLCJGVU5EU19UUkFOU0ZFUiIsIlBBWU1FTlQiLCJRVU9URV9SRVFVRVNUX1JFU1BPTlNFIiwiQUNDT1VOVF9NR01UIiwiU0hPUF9NR01UIiwiU0hPUF9WSUVXIiwiU0hPUF9BU1NJR04iLCJUUkFOU0ZFUiIsIlJFREVFTV9CTE9DSyJdLCJ1c2VyX2lkIjoiYXV0aDB8NThiNTkzYWQ0NGU3MDMwYzBiZTE3OWQzIiwicHJvZmlsIjoic3VwZXJfbWVyY2hhbnQiLCJlbWFpbCI6Im5pemFya2xpYmlAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInVzZXJuYW1lIjoibml6YXJrbGliaSIsImlzcyI6Imh0dHBzOi8vbXltb25leW1vYmlsZS5ldS5hdXRoMC5jb20vIiwic3ViIjoiYXV0aDB8NThiNTkzYWQ0NGU3MDMwYzBiZTE3OWQzIiwiYXVkIjoicDNhcmNBT3VjaDJXUVg4ajV6TG5jYm5kUExrcTlZYjMiLCJleHAiOjE0OTI1NTI0ODUsImlhdCI6MTQ5MjUyMzY4NX0.lyuH2zkj5y7D61-BH253Fj2TTpkXIGJ7bZ6idI7fsQ8");
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








    ////////////////////getRedeem


    private void getRedeem(String mAmount) {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(QuoteRequestActivity.this, R.style.myDialog))
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
                            new SweetAlertDialog(QuoteRequestActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage())
                                    .show();

                            getBalance();
                        } else {
                            new SweetAlertDialog(QuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                new SweetAlertDialog(QuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
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




    public Context getContext(){
        return QuoteRequestActivity.this;
    }



}
