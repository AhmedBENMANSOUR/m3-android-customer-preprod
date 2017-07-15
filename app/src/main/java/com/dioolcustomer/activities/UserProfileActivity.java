package com.dioolcustomer.activities;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.dioolcustomer.fragments.TransfertFragment;
import com.dioolcustomer.models.ShopsByOwnerId;
import com.dioolcustomer.ui.AlertDialogRadio;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.UserUpdated;
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

public class UserProfileActivity extends ActionBarActivity implements AlertDialogRadio.AlertPositiveListener
        ,AlertDialogRadio.AlertNegativeListener{

    TextView mTextFirstName, mTextLastName, mTextAddres, mTextPhone, mTextEmail, mTextBusinessName;
    TextView mTextNumIdentification, mTextTypeIdentification, mTextValiditeIdentite,mTextProfil;
    TextView mTextShopName, mTextCampanyName, mTextBusinessRegistration,mTextCity, mTextCountry;
    Button mValidateButton;

    public String TAG = getClass().getSimpleName();

    Encryption encryption = new Encryption();


    JsonObjectRequest jsonObjReq;
    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    UserProfile mUserProfile;
    Token mUserToken;
    Gson gson;
    //JSONObject mPersonalData, mUserMetaData, mUserPersonalData, mUserBusinessData;

    String mFirstName = "";
    String mLastName = "";
    String mPhone = "";
    String mAdresseMail = "";
    String mAdress = "";
    String mBusinessName,mIdCard,mBusinessAdress,mBusinessRegistration, mTaxCard= "";


    ImageView mPhotoUser;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private TextView mTextIdCard,mTextExpiration,mTextTaxCard, mTextBusinessAdress,mTextRegistration;

    private ShopsByOwnerId mShops;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle(R.string.profile);

        //Prepare Preferences
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();


        gson = new Gson();
        String jsonToken = shared.getString("USER_TOKEN", "");
        mUserToken = gson.fromJson(jsonToken, Token.class);
        String jsonProfile = null;
        try {
            jsonProfile = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_PROFILE", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* String jsonToken = null;
        try {
            jsonToken = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TOKEN", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        mUserProfile = gson.fromJson(jsonProfile, UserProfile.class);
        mUserToken = gson.fromJson(jsonToken, Token.class);


        mPhotoUser = (ImageView) findViewById(R.id.image_profile);
       // mTextBusinessName = (TextView) findViewById(R.id.businessname_text);
        mTextEmail = (TextView) findViewById(R.id.email_text);
        mTextFirstName = (TextView) findViewById(R.id.firstname_text);
        mTextLastName = (TextView) findViewById(R.id.lastname_text);
        mTextPhone = (TextView) findViewById(R.id.phone_text);
        mTextNumIdentification = (TextView) findViewById(R.id.num_identification_text);
        mTextTypeIdentification = (TextView) findViewById(R.id.type_identification_text);
        mTextValiditeIdentite = (TextView) findViewById(R.id.validate_identitie_text);
        mTextProfil = (TextView) findViewById(R.id.profil_text);
        mTextCampanyName = (TextView)findViewById(R.id.nom_entreprise_text);
        mTextBusinessRegistration = (TextView)findViewById(R.id.businesregistration_text);
        mTextCity = (TextView)findViewById(R.id.city_text);
        mTextCountry = (TextView)findViewById(R.id.country_text);

     //   mValidateButton = (Button) findViewById(R.id.update_action_button);

      //  mTextIdCard= (TextView) findViewById(R.id.id_card_text);
      //  mTextExpiration = (TextView) findViewById(R.id.date_expiration_text);
        mTextRegistration = (TextView) findViewById(R.id.businesregistration_text);
        mTextTaxCard= (TextView) findViewById(R.id.taxcard_text);
        mTextBusinessAdress = (TextView) findViewById(R.id.adresse_entreprise_text);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_change_shop);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                try {
                    startGetShopsByOwner();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        Glide.with(this).load(mUserProfile.getPictureURL().toString()).into(mPhotoUser);

        try {
            mFirstName = "Prénom : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mLastName = "Nom : "+ encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_LASTNAME_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mPhone = "Num Tel : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_PHONE_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mAdress = "Adresse : "+ encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ADRESS_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mAdresseMail = "Email : "+ encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mBusinessName= "Nom commercial : "+  encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            mTextNumIdentification.setText("Numéro d'identification : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_CARD_MERCHANT", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mTextTypeIdentification.setText("Type d'identification : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("ID_TYPE", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mTextValiditeIdentite.setText("Validité d'identité : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("ID_VALIDITY_DATE", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mTextProfil.setText("Profile : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TYPE_MERCHANT", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mTextCampanyName.setText("Nom de l'entreprise : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_BUSINESSNAME_MERCHANT", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mTextCity.setText("Ville : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_BUSINESS_CITY_MERCHANT", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mTextCountry.setText("Pays : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_BUSINESS_COUNTRY_MERCHANT", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_BUSINESS_ADRESS_MERCHANT", "")));
            mTextBusinessAdress.setText("Adresse de l'entreprise : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_BUSINESS_ADRESS_MERCHANT", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mIdCard = "Carte Id : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_CARD_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mBusinessAdress= "Adresse commercial : "+ encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_BUSINESS_ADRESS_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mBusinessRegistration= "Registre de commerce : "+ encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_BUSINESS_REGISTRATION_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            mTaxCard= "Carte de Contribuable : "+ encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TAX_CARD_MERCHANT", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTextFirstName.setText(mFirstName);
        mTextLastName.setText(mLastName);
        mTextPhone.setText(mPhone);
      //  mTextAddres.setText(mAdress);
        mTextEmail.setText(mAdresseMail);
    //    mTextBusinessName.setText(mBusinessName);

       // mTextIdCard.setText(mIdCard);
       // mTextBusinessAdress.setText(mBusinessAdress);
        mTextTaxCard.setText(mTaxCard);
        mTextRegistration.setText(mBusinessRegistration);




      /*  mValidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckFiels()) {
                    if (MMMUtils.isConnectedTointernet(UserProfileActivity.this))
                        updateProfile();
                    else
                        Toast.makeText(UserProfileActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();

                }

            }
        });*/







        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Profil" + "</font>")));

    }

    private boolean CheckFiels() {
        if (mTextLastName.getText().toString().equals("")) {
            mTextLastName.setError("Champs obligatoire");
            return false;
        } else if (mTextFirstName.getText().toString().equals("")) {
            mTextFirstName.setError("Champs obligatoire");
            return false;
        }  else if (mTextPhone.getText().toString().equals("")) {
            mTextPhone.setError("Champs obligatoire");
            return false;
        } else if (mTextEmail.getText().toString().equals("")) {
            mTextEmail.setError("Champs obligatoire");
            return false;
        } else if (!MMMUtils.isEmailValid(mTextEmail.getText().toString())) {
            mTextEmail.setError("Veuillez taper une adresse mail valide");
            return false;
        }

        else if (mTextIdCard.getText().toString().equals("")) {
            mTextIdCard.setError("Champs obligatoire");
            return false;
        }

       /* else if (mTextExpiration.getText().toString().equals("")) {
            mTextExpiration.setError("Champs obligatoire");
            return false;
        }*/


        else if (mTextBusinessName.getText().toString().equals("")) {
            mTextBusinessName.setError("Champs obligatoire");
            return false;
        }

        else if (mTextRegistration.getText().toString().equals("")) {
            mTextRegistration.setError("Champs obligatoire");
            return false;
        }


        else if (mTextTaxCard.getText().toString().equals("")) {
            mTextTaxCard.setError("Champs obligatoire");
            return false;
        }

        else if (mTextBusinessAdress.getText().toString().equals("")) {
            mTextBusinessAdress.setError("Champs obligatoire");
            return false;
        }

        else return true;
    }


    private void updateProfile() {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(UserProfileActivity.this, R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();

        // Sending parameters
        Map<String, String> params = new HashMap<String, String>();
        params.put("userId", mUserProfile.getId());
        params.put("firstname", mTextFirstName.getText().toString());
        params.put("lastname", mTextLastName.getText().toString());
     //   params.put("address", mTextAddres.getText().toString());
        params.put("phone", mTextPhone.getText().toString());
        params.put("email", mTextEmail.getText().toString());


        params.put("businessName", mTextBusinessName.getText().toString());
        params.put("idCard ", mTextIdCard.getText().toString());
        params.put("expiryDateCard ", mTextExpiration.getText().toString());
        params.put("businessRegistration ", mTextRegistration.getText().toString());
        params.put("businessAddress ", mTextBusinessAdress.getText().toString());
        params.put("taxCard ", mTextTaxCard.getText().toString());



        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_UPADTE_PROFILE, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();

                        UserUpdated mUser = new UserUpdated().createUpdatedUser(response);
                        editor.putString("USER_EMAIL_MERCHANT", mTextEmail.getText().toString());
                        if(mUser.getUpdateStatus().equals("success"))
                        {
                            editor.putString("USER_NAME_MERCHANT",  mTextFirstName.getText().toString());
                            editor.putString("USER_LASTNAME_MERCHANT",mTextLastName.getText().toString());
                            editor.putString("USER_ADRESS_MERCHANT", mTextAddres.getText().toString());
                            editor.putString("USER_PHONE_MERCHANT",mTextPhone.getText().toString());
                            editor.putString("USER_BUSINESSNAME_MERCHANT", mTextBusinessName.getText().toString());
                            editor.commit();
                        }

                        SweetAlertDialog mSweet =
                                new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("DIOOL")
                                        .setContentText(mUser.getMessage());


                        mSweet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                finish();
                            }
                        });

                        mSweet.show();

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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, UserProfileActivity.class.getCanonicalName());
    }
























    ////////////////////////////////get shop by owner //////////////////////



    public void startGetShopsByOwner() throws Exception{

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(UserProfileActivity.this, R.style.myDialog))
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
        params.put("ownerId",mUserProfile.getId());
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
                                MMMUtils.logoutUser(UserProfileActivity.this);
                                Toast.makeText(UserProfileActivity.this, "Vous devez avoir au moins un shop à sélectionner",
                                        Toast.LENGTH_LONG).show();
                            }else{
                                String[] strShop = new String[mShops.getMListShop().size()];
                                for(int i = 0; i < mShops.getMListShop().size(); i++){
                                    strShop[i] = mShops.getMListShop().get(i).getName();
                                }

                                AlertDialogRadio.setCode(strShop);
                                /** Getting the fragment manager */
                                FragmentManager manager = UserProfileActivity.this.getFragmentManager();
                                // FragmentManager manager = getFragmentManager();

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
                            new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, TransfertFragment.class.getCanonicalName());
    }



    ////////////////////////////////////////assign shop for merchant //////////////////////////////////////////


    public void startAssignShopForMerchant() throws Exception{

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(UserProfileActivity.this, R.style.myDialog))
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
                            new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText(mShops.getMessage())
                                    .show();


                        }

                        else
                        {
                            new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
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

                new SweetAlertDialog(UserProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, TransfertFragment.class.getCanonicalName());
    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        MyMoneyMobileApplication.getInstance().touch();
        Log.d(TAG, "User interaction to " + this.toString());
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

    }
}
