package com.dioolcustomer.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.dioolcustomer.models.ChildrensEmails;
import com.dioolcustomer.webservice.GetChildrensEmails;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.BalanceResponse;
import com.dioolcustomer.models.SendRedeemResponse;
import com.dioolcustomer.models.TransfertArgentResponse;
import com.dioolcustomer.models.UserIdByEmail;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.intercom.android.sdk.Intercom;

public class TransfertSoldeDioolActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Encryption encryption = new Encryption();


    public String TAG = getClass().getSimpleName();

    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    String daBalance = "";
    String revBalance = "";
    TransfertArgentResponse mTransfertCreditResponse;
    UserIdByEmail mUserIdByEmail;

    JsonObjectRequest jsonObjReq;
    Gson gson;
    UserProfile mUserProfile;
    Token mUserToken;
    Spinner txtMsisdn;
    EditText txtEmail;
    EditText txtAmount;
    TextView txtDaBalance, mTextPoints;
    TextView txtRevenu, txtRevBalance;
    Button mBtnSend, cancelButton, redeemButton;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setContentView(R.layout.activity_transfert_solde_diool);

//        Analytics.with(this).screen("View", TAG);
        //Prepare Preferences
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();

        gson = new Gson();
        String jsonToken = null;
        try {
            jsonToken = shared.getString("USER_TOKEN", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
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



        txtEmail = (EditText) findViewById(R.id.diool_msisdn_edit);
        txtMsisdn = (Spinner) findViewById(R.id.diool_msisdn_spinner);
        txtMsisdn.setOnItemSelectedListener(TransfertSoldeDioolActivity.this);
        txtAmount = (EditText) findViewById(R.id.diool_amount_edit);
        txtDaBalance = (TextView) findViewById(R.id.textView_da_balance);
        txtRevBalance = (TextView) findViewById(R.id.textView_loyalty_points);
        txtRevenu = (TextView) findViewById(R.id.textView_rev_balance);
        mTextPoints = (TextView) findViewById(R.id.textView_rev_balance_label);

        txtDaBalance.setText(daBalance + "");
        txtRevBalance.setText(revBalance + "");
        txtRevenu.setText(revBalance + "");


        txtEmail.setVisibility(View.GONE);

        String jsonProfile = null;
        try {
            jsonProfile = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_PROFILE", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mUserProfile = gson.fromJson(jsonProfile, UserProfile.class);


        LinearLayout revenueLayout = (LinearLayout) findViewById(R.id.layout_revenue);
        LinearLayout loyaltyLayout = (LinearLayout) findViewById(R.id.layout_loyalty);
        TextView messageText = (TextView) findViewById(R.id.transfer_message);
        try {
            if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("customer")){
                revenueLayout.setVisibility(View.GONE);
                txtMsisdn.setVisibility(View.GONE);
                txtEmail.setVisibility(View.VISIBLE);
                messageText.setText("Transférer à un utilisateur");
            }else{
                loyaltyLayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getSupportActionBar().setTitle(R.string.transfertdioolmenu);


        cancelButton = (Button) findViewById(R.id.buttonCancel);
        redeemButton = (Button) findViewById(R.id.buttonRedeem);
        mBtnSend = (Button) findViewById(R.id.buttonSend);


        redeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MMMUtils.isConnectedTointernet(TransfertSoldeDioolActivity.this)) {
                    final Dialog dialog = new Dialog(TransfertSoldeDioolActivity.this);
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
                                Toast.makeText(TransfertSoldeDioolActivity.this, "Le montant doit être > 0", Toast.LENGTH_SHORT).show();
                                return;
                            } else if (!MMMUtils.isInteger(mText.getText().toString())) {
                                Toast.makeText(TransfertSoldeDioolActivity.this, "Le montant doit être un nombre entier", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                dialog.dismiss();
                                getRedeem(mText.getText().toString());
                            }
                        }
                    });

                    dialog.show();


                } else
                    Toast.makeText(TransfertSoldeDioolActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();


            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateTransferCreditForm()) {
                    if (MMMUtils.isConnectedTointernet(TransfertSoldeDioolActivity.this))
                        try {
                            transfertDiool();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //transfertDiool();
                    else
                        Toast.makeText(TransfertSoldeDioolActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();

                }


                txtMsisdn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        // your code here
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }

                });

            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        GetChildrensEmails getChildrensEmails  = new GetChildrensEmails();
        String[] parentsTab = null;
        String[] childrenTab = null;
        String parentId = "";
        try {
            String childrenStr = encryption.decrypt(mUserToken.getIdToken(),shared.getString("CHILDREN", ""));
            String parentsStr  = encryption.decrypt(mUserToken.getIdToken(),shared.getString("PARENT_IDS", ""));
            childrenStr = childrenStr.substring(1, childrenStr.length()-1);
            parentsStr = parentsStr.substring(1, parentsStr.length()-1);

            childrenTab = childrenStr.split(",");
            for(int i =0; i <childrenTab.length; i++){
                childrenTab[i] = childrenTab[i].replace("\"","");
            }
            parentsTab = parentsStr.split(",");

            for(int i =0; i <parentsTab.length; i++){
                parentsTab[i] = parentsTab[i].substring(1, parentsTab[i].length()-1);
                parentsTab[i] = parentsTab[i].replace("\"","");
                String[] parentIdTab = parentsTab[i].split(":");
                parentId = parentIdTab[1];

            }
            System.out.println("childrenStr : "+childrenStr);
            System.out.println("parentId : "+parentId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("////// debut de test ////////");
            System.out.println("encryption.decrypt(mUserToken.getIdToken(),shared.getString(\"USER_TYPE_MERCHANT\", \"\") : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")));
            if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).equals("super_merchant")){
                if(parentId.equals("0")){
                    parentId = null;
                }
                String tab = getChildrensEmails(mUserProfile.getId(),mUserToken,childrenTab,parentId);
                System.out.println("tab : "+tab);
            } else if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")).startsWith("merchant")){
                if(parentId.equals("0")){
                    parentId = null;
                }
                childrenTab = null;
                String tab = getChildrensEmails(null,mUserToken,childrenTab,parentId);

                System.out.println("tab : "+tab);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }





        txtMsisdn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
               /* Toast.makeText(parentView.getContext(),
                        "OnItemSelectedListener : " + parentView.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();*/

                if(parentView.getItemAtPosition(position).toString().equals("autre")){
                    txtMsisdn.setVisibility(View.GONE);
                    txtEmail.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
               /* Toast.makeText(parentView.getContext(),
                        "OnItemSelectedListener : " + parentView.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();*/
            }

        });

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Transfert de solde" + "</font>")));


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
                Intent intent = new Intent(TransfertSoldeDioolActivity.this,
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
             /*   if (MMMUtils.isConnectedTointernet(AddFundsActivity.this))
                    getMerchantBalance();
                return true;*/

            default:
                return true;
        }
    }






    private boolean validateTransferCreditForm() {

        if(txtMsisdn.getVisibility() == View.VISIBLE){
            if (txtMsisdn.getSelectedItem().toString().equals("")) {
                Toast.makeText(TransfertSoldeDioolActivity.this, "Identifiant non Spécifié", Toast.LENGTH_SHORT).show();
                return false;
            } else if (txtAmount.getText().length() <= 0) {
                Toast.makeText(TransfertSoldeDioolActivity.this, "Montant non Spécifié", Toast.LENGTH_SHORT).show();
                return false;
            } else
                return true;
        }else {
            if (txtEmail.getText().toString().equals("")) {
                Toast.makeText(TransfertSoldeDioolActivity.this, "Identifiant non Spécifié", Toast.LENGTH_SHORT).show();
                return false;
            } else if (txtAmount.getText().length() <= 0) {
                Toast.makeText(TransfertSoldeDioolActivity.this, "Montant non Spécifié", Toast.LENGTH_SHORT).show();
                return false;
            } else
                return true;

        }



    }


    private void getRedeem(String mAmount) {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(TransfertSoldeDioolActivity.this, R.style.myDialog))
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
                            new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mRedeem.getMessage())
                                    .show();

                            getBalance();
                        } else {
                            new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, TransfertSoldeDioolActivity.class.getCanonicalName());
    }


   public void transfertDiool() throws JSONException ,GeneralSecurityException, UnsupportedEncodingException{

       final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(TransfertSoldeDioolActivity.this, R.style.myDialog))
               .direction(ACProgressConstant.DIRECT_CLOCKWISE)
               .themeColor(Color.WHITE)
               .text("Patientez...")
               .fadeColor(Color.DKGRAY).build();
       dialog.setCancelable(false);
       dialog.show();

       Map<String, Object> params = new HashMap<String, Object>();


       if(txtMsisdn.getVisibility() == View.VISIBLE){
           params.put("email", txtMsisdn.getSelectedItem().toString());
       }else {
           params.put("email", txtEmail.getText().toString());
       }


       jsonObjReq = new JsonObjectRequest(Request.Method.POST,
               GlobalConstants.URL_GET_USER_ID_BY_EMAIL, new JSONObject(params),
               new Response.Listener<JSONObject>() {

                   @Override
                   public void onResponse(JSONObject response) {

                       Log.e(TAG, response.toString());
                       dialog.dismiss();
                       mUserIdByEmail = new UserIdByEmail().createUserIdByEmail(response);

                       if (String.valueOf(mUserIdByEmail.getCode()).equals("0")) {

                           try {
                               startTransfertDiool(mUserIdByEmail.getResult());
                           } catch (JSONException e) {
                               e.printStackTrace();
                           } catch (GeneralSecurityException e) {
                               e.printStackTrace();
                           }catch (UnsupportedEncodingException e){
                               e.printStackTrace();
                           }catch (Exception e){
                               e.printStackTrace();
                           }

                       } else {
                           new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.ERROR_TYPE)
                                   .setTitleText("DIOOL")
                                   .setContentText(mUserIdByEmail.getMessage())
                                   .show();
                       }


                   }
               }, new Response.ErrorListener() {

           @Override
           public void onErrorResponse(VolleyError error) {
               Log.e(TAG, "Error: " + error.getMessage());
               dialog.dismiss();

               new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.ERROR_TYPE)
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
       MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, TransfertSoldeDioolActivity.class.getCanonicalName());
   }













    private void startTransfertDiool(final String userId) throws Exception {
        // Personalize View when loading WS
       /* final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(TransfertSoldeDioolActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();*/

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

        System.out.println("shared.getString(\"SHOP_ID\", \"\") : "+shared.getString("SHOP_ID", ""));
        System.out.println("shared.getString(\"SHOP_ID\", \"\").toString() : "+shared.getString("SHOP_ID", "").toString());


        //System.out.println("Encryption.decrypt(mUserToken.getIdToken(),b) : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("SHOP_ID", "")));
        // Sending parameters
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("senderProviderIdentifier", "DIOOL");
        params.put("senderServiceType", "MOBILE_MONEY");
        params.put("senderProviderAccountID", "");
        //params.put("senderUserIdentifier", shared.getString("USER_EMAIL_MERCHANT", "").toString());
       // params.put("senderUserIdentifier", userId);
        params.put("senderUserIdentifier", mUserProfile.getId());
        params.put("amount", txtAmount.getText().toString());
        params.put("recipientProviderIdentifier", "DIOOL");
        params.put("recipientServiceType", "MOBILE_MONEY");
        params.put("recipientProviderAccountID", "");
        params.put("recipientUserIdentifier", userId);
        //params.put("recipientUserIdentifier", "auth0|582ef53cd236e90e2738fc9d");
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

        params.put("channel", "Mobile");
        params.put("issuerParents",issuerParents);


        Log.e("params TRASFERT", params.toString() + "");



                jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        GlobalConstants.URL_TRNSFERT, new JSONObject(params),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                Log.e(TAG, response.toString());
                                //dialog.dismiss();
                                mTransfertCreditResponse = new TransfertArgentResponse().createTransfertObject(response);

                                if (String.valueOf(mTransfertCreditResponse.getCode()).equals("0")) {

                                  // txtMsisdn.setText("");
                                    txtAmount.setText("");

                                    getBalance();

                                    SweetAlertDialog mSweet = new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("DIOOL")
                                            .setContentText(mTransfertCreditResponse.getMessage());


                                    mSweet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            finish();
                                        }
                                    });

                                    mSweet.show();

                                    Properties transferProperties = new Properties();
                                    transferProperties.put("user_id",mUserProfile.getId());
                                    transferProperties.put("req_amount",txtAmount.getText().toString());
                                    transferProperties.put("req_channel","mobile");
                                    transferProperties.put("req_recipient_provider_account_id","");
                                    transferProperties.put("req_sender_provider_account_id","");
                                    transferProperties.put("senderProviderIdentifier","DIOOL");
                                    transferProperties.put("",mUserProfile.getId());
                                    Analytics.with(TransfertSoldeDioolActivity.this).track("transfer",transferProperties);


                                }  else if(String.valueOf(mTransfertCreditResponse.getCode()).equals("27")){
                                    new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("DIOOL")
                                            .setContentText(mTransfertCreditResponse.getMessage())
                                            .setCancelText("Cancel")
                                            .setConfirmText("Confirm")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    ///// send transfer after confirmation
                                                    try {
                                                        startConfirmedTransfertDiool(userId);
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

                                }else {
                                    new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("DIOOL")
                                            .setContentText(mTransfertCreditResponse.getMessage())
                                            .show();
                                }




                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error: " + error.getMessage());
                        //dialog.dismiss();

                        new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, TransfertSoldeDioolActivity.class.getCanonicalName());
    }





    /// send confirmed transfer diool



    private void startConfirmedTransfertDiool(final String userId) throws Exception {
        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(TransfertSoldeDioolActivity.this, R.style.myDialog))
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
        //byte[] b = shared.getString("SHOP_ID", "").toString().getBytes(Charset.forName("UTF-8"));
     //   System.out.println("Encryption.decrypt(mUserToken.getIdToken(),b) : "+encryption.decrypt(mUserToken.getIdToken(),b));

        // Sending parameters
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("senderProviderIdentifier", "DIOOL");
        params.put("senderServiceType", "MOBILE_MONEY");
        params.put("senderProviderAccountID", "");
        //params.put("senderUserIdentifier", shared.getString("USER_EMAIL_MERCHANT", "").toString());
        // params.put("senderUserIdentifier", userId);
        params.put("senderUserIdentifier", mUserProfile.getId());
        params.put("amount", txtAmount.getText().toString());
        params.put("recipientProviderIdentifier", "DIOOL");
        params.put("recipientServiceType", "MOBILE_MONEY");
        params.put("recipientProviderAccountID", "");
        params.put("recipientUserIdentifier", userId);
        //params.put("recipientUserIdentifier", "auth0|582ef53cd236e90e2738fc9d");
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
        params.put("channel", "Mobile");
        params.put("issuerParents",issuerParents);
        params.put("confirm", true);


        Log.e("params TRASFERT", params.toString() + "");



        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_TRNSFERT, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        mTransfertCreditResponse = new TransfertArgentResponse().createTransfertObject(response);

                        if (String.valueOf(mTransfertCreditResponse.getCode()).equals("0")) {

                          //  txtMsisdn.setText("");
                            txtAmount.setText("");

                            getBalance();

                            SweetAlertDialog mSweet = new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mTransfertCreditResponse.getMessage());


                            mSweet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    finish();
                                }
                            });
                            mSweet.show();



                            Properties transferProperties = new Properties();
                            transferProperties.put("user_id",mUserProfile.getId());
                            transferProperties.put("req_amount",txtAmount.getText().toString());
                            transferProperties.put("req_channel","mobile");
                            transferProperties.put("req_recipient_provider_account_id","");
                            transferProperties.put("req_sender_provider_account_id","");
                            transferProperties.put("senderProviderIdentifier","DIOOL");
                            transferProperties.put("",mUserProfile.getId());
                            Analytics.with(TransfertSoldeDioolActivity.this).track("transfer",transferProperties);


                        } else {
                            new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mTransfertCreditResponse.getMessage())
                                    .show();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(TransfertSoldeDioolActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, TransfertSoldeDioolActivity.class.getCanonicalName());
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
                        txtRevenu.setText(mBalnceResponse.getRevenueAccountBalance() + "");


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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, TransfertSoldeDioolActivity.class.getCanonicalName());
    }





    ///////// get children emails



    ChildrensEmails mChildrensEmails;
    private String getChildrensEmails(final String userId,final Token mUserToken,String[] children,String parentId) throws Exception {
        // Personalize View when loading WS



        //create table issuerParents
        List<String> parentsIdList = new ArrayList<>();

        // Sending parameters
        Map<String, Object> params = new HashMap<String, Object>();


     //   System.out.println("children : "+children.toString());
        params.put("userId",userId);
        params.put("children",children);
        params.put("parentId",parentId);


        Log.e("params TRASFERT", params.toString() + "");



        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.GET_CHILDREN_S_EMAIL, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, response.toString());
                        try {
                            mChildrensEmails = new ChildrensEmails().createChildrensEmails(response);
                            System.out.println("code : "+mChildrensEmails.getCode());
                            System.out.println("result : "+mChildrensEmails.getResult());
                            String[] mResultTab = null;
                            String mResult  = mChildrensEmails.getResult();
                            if(!mResult.equals("null")){

                                mResult = mResult.substring(1,mResult.length() - 1);
                                mResultTab = mResult.split(",");
                                for(int i = 0; i < mResultTab.length; i++){
                                    mResultTab[i] = mResultTab[i].replace("\"","");
                                }

                                System.out.println("tester resultat");
                                List<String> list;


                                list = new LinkedList<String>(Arrays.asList(mResultTab));

                                list.add(mResultTab.length,"autre");


                                ArrayAdapter<String> adapter;

                                adapter = new ArrayAdapter<String>(TransfertSoldeDioolActivity.this,
                                        android.R.layout.simple_spinner_dropdown_item, list);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                txtMsisdn.setAdapter(adapter);
                            }else{
                                txtMsisdn.setVisibility(View.GONE);
                                txtEmail.setVisibility(View.VISIBLE);
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
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

                try {
                    System.out.println("encryption.decrypt(mUserToken.getIdToken(),shared.getString(\"USER_ID_TOKEN\", \"\")) : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, GetChildrensEmails.class.getCanonicalName());
        return mChildrensEmails.getResult();
    }






    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("TransfertSoldeDiool Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
