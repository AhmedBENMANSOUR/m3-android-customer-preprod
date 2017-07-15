package com.dioolcustomer.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.api.ParameterBuilder;
import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Auth0;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.identity.IdentityProvider;
import com.auth0.identity.WebIdentityProvider;
import com.auth0.identity.web.CallbackParser;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.callbacks.EventBusIdentityProviderCallback;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.webservice.GetShopById;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.google.gson.Gson;
import com.dioolcustomer.R;
import com.dioolcustomer.callbacks.AuthenticationEvent;
import com.dioolcustomer.callbacks.ErrorEvent;
import com.dioolcustomer.utils.MMMUtils;
import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.Traits;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class AuthActivity extends AppCompatActivity {


    private Encryption encryption = new Encryption();

    String mUserType = "";
    private static final String TAG = MainActivity.class.getName();

    private static final int TYPE_CODE = 0;

    private AuthenticationAPIClient client;
    private EventBus eventBus;
    private WebIdentityProvider webProvider;
    private IdentityProvider identity;
    private ProgressDialog progress;
    EditText emailField;
    EditText passwordField;
    TextView mTextShare;

    private String firstName;
    private String lastName;
    private String businessName;
    private String phone;
    private String idCard;
    private String idValidityDate;
    private String idType;
    private String shopId;
    private String userIdToken = null;
    private String appScope;

    private String inheritance = "";
    private String business_data_verified;
    private String assigning_mode;
    private Date gracePeriod;
    private boolean boolGracePeriod;
    private JSONArray mGlobalAccountStatus;


    Gson gson;
    SharedPreferences.Editor editor;
    private SharedPreferences shared;


    JsonObjectRequest jsonObjReq;
    Token mUserToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Prepare Preferences
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


        // Launch Pin Activity
        if(mUserType.startsWith("customer")){
            try {
                launchActivity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


//        Analytics.with(this).screen("View", TAG);

        appScope = getString(R.string.auth_scope);
        Auth0 auth0 = new Auth0(getString(R.string.auth_client_ID), getString(R.string.auth_client_DOMAIN));
        this.client = auth0.newAuthenticationAPIClient();
        this.client.setDefaultDbConnection(getString(R.string.data_base));
        this.eventBus = new EventBus();
        final EventBusIdentityProviderCallback callback = new EventBusIdentityProviderCallback(eventBus, client);
        this.webProvider = new WebIdentityProvider(new CallbackParser(), auth0.getClientId(), auth0.getAuthorizeUrl());
        this.webProvider.setCallback(callback);

        emailField = (EditText) findViewById(R.id.login_email_field);
        passwordField = (EditText) findViewById(R.id.login_password_field);
        try {
            //  System.out.println("shared.getString(GlobalConstants.USER_LOGIN, emailField.getText().toString()) : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString(GlobalConstants.USER_LOGIN, emailField.getText().toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            emailField.setText(encryption.decrypt(mUserToken.getIdToken(),shared.getString(GlobalConstants.USER_LOGIN, emailField.getText().toString())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTextShare = (TextView) findViewById(R.id.text_share_link);
        Button loginButton = (Button) findViewById(R.id.login_action_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MMMUtils.isConnectedTointernet(AuthActivity.this))
                    startLogin();
                else
                    Toast.makeText(AuthActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();




            }
        });


        Button loginPasswordlessButton = (Button) findViewById(R.id.login_passwordless_action_button);
        loginPasswordlessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MMMUtils.isConnectedTointernet(AuthActivity.this))
                    startPasswordlessLogin();
                else
                    Toast.makeText(AuthActivity.this, "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();


            }
        });


        TextView mTextChangePassword = (TextView) findViewById(R.id.text_mot_passe_oublie);
        mTextChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(AuthActivity.this, ResetPasswordActivity.class);
                startActivity(emailIntent);

            }
        });


        mTextShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareLinkSignUp();
            }
        });


    }

    private void startPasswordlessLogin() {
        progress = ProgressDialog.show(AuthActivity.this, null, "Traitement en cours...", true);
        ParameterBuilder builder = ParameterBuilder.newBuilder();
        Map<String, Object> parameters = builder.setScope(appScope).setGrantType(com.auth0.api.ParameterBuilder.GRANT_TYPE_PASSWORD).asDictionary();
        client.passwordlessWithSMS("0021652360116", false).addParameters(parameters)
                .start(new BaseCallback<Void>() {
                    @Override
                    public void onSuccess(Void payload) {
                        progress.dismiss();
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        progress.dismiss();
                    }
                });

    }

    private void shareLinkSignUp() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Vous êtes un Marchand. Effectuer toutes les opérations dans votre boutique avec n'importe quel abonné mobile. Retraits, Transferts, Depôts. Disponible sur Android en utilisant votre connexion 2G/3G/4G." +
                "Inscrivez-vous via ce lien: https://emonize.mymoneytop.com/m3-home/";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My MONEY MOBILE");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }


    private void startLogin() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        Log.e("------ LOGIN START","ERREUR LOGIN");
        progress = ProgressDialog.show(AuthActivity.this, null, "Traitement en cours...", true);
        ParameterBuilder builder = ParameterBuilder.newBuilder();
        Map<String, Object> parameters = builder.setScope(appScope).setGrantType(com.auth0.api.ParameterBuilder.GRANT_TYPE_PASSWORD).asDictionary();
        client.login(email, password).addParameters(parameters)
                .start(new AuthenticationCallback() {
                    @Override
                    public void onSuccess(UserProfile userProfile, Token token) {
                        Log.e("IDDD",userProfile.getId()+"!");

                        Log.e("idToken", token.getIdToken());
                        //if()

                        eventBus.post(new AuthenticationEvent(userProfile, token));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        eventBus.post(new ErrorEvent(R.string.login_failed_title, R.string.login_failed_message, throwable));
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        webProvider.stop();
        eventBus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (progress != null) {
            progress.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (identity != null) {
            identity.authorize(this, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, RESULT_OK, intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (identity != null) {
            identity.authorize(this, requestCode, resultCode, data);
        }
    }

    @Subscribe
    public void onEvent(ErrorEvent event) {
        progress.dismiss();
        event.showDialog(this);
    }

    @Subscribe
    public void onEvent(final AuthenticationEvent event) throws Exception {
        final UserProfile profile = event.getProfile();
        progress.dismiss();
        MyMoneyMobileApplication.getInstance().setmIsConnected(true);


        String jsonProfile = gson.toJson(profile);
        String jsonToken = gson.toJson(event.getToken());

        JSONObject mPersonalData, mUserMetaData, mUserPersonalData, mUserAppData,mBusinessData;
        mUserMetaData = null;

        String signupStep = "";

        try {
            // signupstep = new JSONObject(mUserMetaData.get("user_metadata")+"");
            mUserMetaData = new JSONObject(profile.getExtraInfo());
            if(mUserMetaData.has("signup_step")){
                signupStep = mUserMetaData.get("signup_step").toString();


            }

            mUserPersonalData =new JSONObject(mUserMetaData.get("user_metadata")+"");
            mUserAppData = mUserMetaData.getJSONObject("app_metadata");
            JSONObject mUserBusnessData = mUserMetaData.getJSONObject("business_data");
            if(mUserBusnessData.has("inheritance")){
                inheritance = mUserBusnessData.get("inheritance").toString();
            }
            if(mUserBusnessData.has("verified")){
                business_data_verified = mUserBusnessData.get("verified").toString();
            }




            mPersonalData = new JSONObject(mUserPersonalData.get("personal_data") + "");


            //  Analytics.with(this).identify(profile.getName(),profile );

            userIdToken = event.getToken().getIdToken();

            if(mUserPersonalData.has("business_data")) {
                mBusinessData = new JSONObject(mUserPersonalData.get("business_data") + "");
                if (mBusinessData.has("taxCard")){
                    String str = encryption.encrypt(userIdToken,mBusinessData.get("taxCard").toString());
                    editor.putString("USER_TAX_CARD_MERCHANT", str);
                }
                if (mBusinessData.has("businessRegistration")){
                    String str = encryption.encrypt(userIdToken,mBusinessData.get("businessRegistration").toString());
                    editor.putString("USER_BUSINESS_REGISTRATION_MERCHANT", str);
                }
                if (mBusinessData.has("address")){
                    String str = encryption.encrypt(userIdToken,mBusinessData.get("address").toString());
                    editor.putString("USER_BUSINESS_ADRESS_MERCHANT", str);
                }
                if (mBusinessData.has("businessName")){
                    String str = encryption.encrypt(userIdToken,mBusinessData.get("businessName").toString());
                    editor.putString("USER_BUSINESSNAME_MERCHANT", str);
                    businessName =  mBusinessData.get("businessName").toString();
                }
                if(mBusinessData.has("shopId")){
                    String str  = encryption.encrypt(userIdToken,mBusinessData.get("shopId").toString());
                    editor.putString("SHOP_ID",str);
                    shopId = mBusinessData.get("shopId").toString();
                }
                if(mBusinessData.has("inheritance")){
                    inheritance = mBusinessData.get("inheritance").toString();
                }
                if(mBusinessData.has("assigningMode")){
                    //  assigning_mode = mUserBusnessData.get("assigningMode").toString();
                    String str = encryption.encrypt(userIdToken,mBusinessData.get("assigningMode").toString());
                    editor.putString("ASSIGNING_MODE", str);
                }

            }




            if (mUserAppData.has("profil")) {
                String str  = encryption.encrypt(userIdToken,mUserAppData.getString("profil").toString());
                mUserType = mUserAppData.getString("profil");
                editor.putString("USER_TYPE_MERCHANT", str);
            }
            if(mUserAppData.has("parent_ids")){
                String str  = encryption.encrypt(userIdToken,mUserAppData.getString("parent_ids").toString());
                editor.putString("PARENT_IDS", str);
            }
            if(mUserAppData.has("children")){
                String str  = encryption.encrypt(userIdToken,mUserAppData.getString("children").toString());
                System.out.println(" mUserAppData.getString(\"children\") "+ mUserAppData.getString("children"));
                editor.putString("CHILDREN", str);
            }
            if(mUserAppData.has("global_account_status")){
                mGlobalAccountStatus = mUserAppData.getJSONArray("global_account_status");
                System.out.println("global_account_status : "+mGlobalAccountStatus);
                JSONObject obj = mGlobalAccountStatus.toJSONObject(mGlobalAccountStatus);
                // eventBus.post(new ErrorEvent(R.string.login_failed_title, R.string.login_failed_message, throwable));
            }
            if(mUserAppData.has("grace_period")){
                String gracePeriodStr = mUserAppData.getString("grace_period");
                // SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                Date date = null;
                try {
                    date = format.parse(gracePeriodStr);
                    System.out.println(date);
                    Calendar cal = Calendar.getInstance();
                    Calendar cal1 = Calendar.getInstance();
                    String currentDate =  format.format(cal.getTime());
                    System.out.println("currentDate : "+currentDate);
                    cal1.setTime(date);
                    long diff = cal1.getTimeInMillis() - cal.getTimeInMillis();
                    System.out.println("diff : "+diff);
                    if(date.after(cal.getTime())){
                        boolGracePeriod = true;
                    }

                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }





            if (mPersonalData.has("idcard")){
                String str  = encryption.encrypt(userIdToken, mPersonalData.getString("idcard").toString());
                editor.putString("USER_ID_CARD_MERCHANT", str);
                idCard = mPersonalData.getString("idcard").toString();
            }
            if (mPersonalData.has("firstname")){
                String str  = encryption.encrypt(userIdToken, mPersonalData.getString("firstname").toString());
                editor.putString("USER_NAME_MERCHANT", str);
                firstName = mPersonalData.getString("firstname").toString();
            }

            if (mPersonalData.has("lastname")){
                String str  = encryption.encrypt(userIdToken, mPersonalData.getString("lastname").toString());
                editor.putString("USER_LASTNAME_MERCHANT", str);
                lastName =  mPersonalData.getString("lastname").toString();
            }
            if (mPersonalData.has("address")){
                String str  = encryption.encrypt(userIdToken, mPersonalData.getString("address").toString());
                editor.putString("USER_ADRESS_MERCHANT", str.toString());
            }
            if (mPersonalData.has("phone")){
                String str  = encryption.encrypt(userIdToken, mPersonalData.getString("phone").toString());
                editor.putString("USER_PHONE_MERCHANT", str);
                phone = mPersonalData.getString("phone").toString();
            }
            if(mPersonalData.has("identitycardvaliditydate")){
                idValidityDate = mPersonalData.getString("identitycardvaliditydate").toString();
            }
            if(mPersonalData.has("idType")){
                idType = mPersonalData.getString("idType").toString();
            }


            String str  = encryption.encrypt(userIdToken, profile.getId());
            editor.putString("USER_ID",str);


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("zzz1", e.toString() + "");
        }


        System.out.println("mUserType : "+mUserType);

        if(mUserType.startsWith("merchant") || mUserType.equals("super_merchant")){
            Crouton.makeText(AuthActivity.this, "Veuillez telecharger l'application DIOOL Merchant", Style.INFO).show();
        }

        else  if ((mUserType.startsWith("customer")) && ( signupStep.equals("2")))
        {

            userIdToken = event.getToken().getIdToken();
            String str = encryption.encrypt(userIdToken, jsonProfile);
            editor.putString("USER_PROFILE",str);

            str = encryption.encrypt(userIdToken, jsonToken);
            editor.putString("USER_TOKEN", jsonToken);
            str = encryption.encrypt(userIdToken, event.getToken().getIdToken());
            editor.putString("USER_ID_TOKEN", str);
            //editor.putString("USER_ID_TOKEN", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJJREVOVElUWSIsIkFDQ09VTlQiLCJNT05FVEFSWV9BQ0NPVU5UIiwiVFJBTlNGRVJfTU9CSUxFTU9ORVkiLCJQQVlNRU5UIiwiVFJBTlNGRVJfQUlSVElNRSIsIlRSQU5TRkVSX0RJT09MIiwiU1RBVFNfVFJBTlNGRVIiLCJGVU5EU19UUkFOU0ZFUiIsIlBBWU1FTlQiLCJRVU9URV9SRVFVRVNUX1JFU1BPTlNFIiwiQUNDT1VOVF9NR01UIiwiU0hPUF9NR01UIiwiU0hPUF9WSUVXIiwiU0hPUF9BU1NJR04iLCJUUkFOU0ZFUiJdLCJ1c2VyX2lkIjoiYXV0aDB8NThiNTkzYWQ0NGU3MDMwYzBiZTE3OWQzIiwiZW1haWwiOiJuaXphcmtsaWJpQGdtYWlsLmNvbSIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJwcm9maWwiOiJzdXBlcl9tZXJjaGFudCIsImlzcyI6Imh0dHBzOi8vbXltb25leW1vYmlsZS5ldS5hdXRoMC5jb20vIiwic3ViIjoiYXV0aDB8NThiNTkzYWQ0NGU3MDMwYzBiZTE3OWQzIiwiYXVkIjoicDNhcmNBT3VjaDJXUVg4ajV6TG5jYm5kUExrcTlZYjMiLCJleHAiOjE0OTAxMTkyNzksImlhdCI6MTQ5MDA5MDQ3OX0.ifU_BzOOOtpDfPg6f2GblleVmrGdUaUgHLvsR7ssltI");
            str = encryption.encrypt(userIdToken,  profile.getEmail());
            editor.putString("USER_EMAIL_MERCHANT", str);
            str = encryption.encrypt(userIdToken,  emailField.getText().toString());
            editor.putString(GlobalConstants.USER_LOGIN, str);
            editor.commit();




            ///// channel track
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
            Properties channelProrerties = new Properties();

            channelProrerties.put("channel","mobile");
            channelProrerties.put("login_timestamp",dateFormatter.format(new Date()));
            channelProrerties.put("user_id",profile.getId());
            Analytics.with(this).track("channel_track",channelProrerties);

            // Start menu Activity
            final Intent newIntent = new Intent(AuthActivity.this, ServiceSelectionActivity.class);
            startActivity(newIntent);

            finish();


        }else if(mUserType.startsWith("customer")){
            userIdToken = event.getToken().getIdToken();
            String str = encryption.encrypt(userIdToken, jsonProfile);
            editor.putString("USER_PROFILE",str);

            str = encryption.encrypt(userIdToken, jsonToken);
            editor.putString("USER_TOKEN", jsonToken);
            str = encryption.encrypt(userIdToken, event.getToken().getIdToken());
            editor.putString("USER_ID_TOKEN", str);
            str = encryption.encrypt(userIdToken,  profile.getEmail());
            editor.putString("USER_EMAIL_MERCHANT", str);
            str = encryption.encrypt(userIdToken,  emailField.getText().toString());
            editor.putString(GlobalConstants.USER_LOGIN, str);
            editor.commit();




            ///// channel track
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
            Properties channelProrerties = new Properties();

            channelProrerties.put("channel","mobile");
            channelProrerties.put("login_timestamp",dateFormatter.format(new Date()));
            channelProrerties.put("user_id",profile.getId());
            Analytics.with(this).track("channel_track",channelProrerties);

            // Start menu Activity
            final Intent newIntent = new Intent(AuthActivity.this, ServiceSelectionActivity.class);
            startActivity(newIntent);

            finish();

        }



        else if(!mUserType.equals("customer") && (signupStep.equals("0") || signupStep.equals("1"))){
            Crouton.makeText(AuthActivity.this, "Veuillez terminer votre inscription", Style.INFO).show();
        }/*else {
            Crouton.makeText(AuthActivity.this, "Si vous êtes un CUSTOMER , veuillez télécharger l'application My Money Mobile Customer!", Style.INFO).show();

        }*/


        // Toast.makeText(AuthActivity.this, "Bienvenue " + profile.getName(), Toast.LENGTH_SHORT).show();
        //  Traits t = new Traits().putFirstName("BEN MANSOUR").putLastName("Ahmed").putValue("userId","auth0|583702bb9c85d85553918774").putEmail("ahmedbenmansourslack@gmail.com");
        System.out.println("firstName : "+firstName);
        System.out.println("l" +
                "tName : "+lastName);
        System.out.println("businessName : "+businessName);
        System.out.println("getString(R.string.app_version : "+getString(R.string.app_version));

        Traits traits = null;
        //System.out.println("SHOPID / "+shared.getString("SHOP_ID",""));
        if(shopId == null || shopId == ""){
            traits = new Traits().putValue("email",emailField.getText().toString()).putValue("firstname",firstName)
                    .putValue("lastname",lastName).putValue("businessName",businessName)
                    .putValue("profile",mUserType).putValue("phone",phone).putValue("idcard",idCard)
                    .putValue("idvaliditydate",idValidityDate).putValue("idType",idType).putValue("version",getString(R.string.app_version));
            System.out.println("profile.getId() : "+profile.getId());
            Analytics.with(this).identify(profile.getId(),traits,null);
//            Analytics.with(this).track("Login successful");
        }else{

            GetShopById getShopById  = new GetShopById();
            String app_version = getString(R.string.app_version);
            System.out.println("getString(R.string.app_version : "+getString(R.string.app_version));
            System.out.println("app_version : "+app_version);
            getShopById.getShopById(mUserToken,userIdToken,shopId,profile.getId(),emailField.getText().toString()
                    ,firstName,lastName,businessName,mUserType,phone,idCard,idValidityDate,idType,app_version);
            System.out.println("profile.getId() : "+profile.getId());
            Analytics.with(this).identify(profile.getId(),traits,null);
            System.out.println();

        }



    }

    public void launchActivity() throws Exception {
        if (!shared.getString("USER_TOKEN", "").equals("")) {
            {
                // Create Refresh Token
                String jsonToken = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_TOKEN", ""));
                Token mUserExistToken = new Gson().fromJson(jsonToken, Token.class);
                if ((!mUserExistToken.getIdToken().equals(null)) && (!mUserExistToken.getIdToken().equals(""))) {

                    // Check if Code Pin is Initialized and Token exists
                    if (MyMoneyMobileApplication.getInstance().lockManager.getAppLock().isPasscodeSet()) {
                        CustomPinActivity.isFromAuthActivity = true;
                        Intent mIntent = new Intent(this, CustomPinActivity.class);
                        mIntent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                        startActivity(mIntent);
                        finish();

                    } else {
                        CustomPinActivity.isFromAuthActivity = true;
                        Intent mIntent = new Intent(this, CustomPinActivity.class);
                        mIntent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                        startActivity(mIntent);
                        finish();
                    }

                }
            }

        }
    }


}
