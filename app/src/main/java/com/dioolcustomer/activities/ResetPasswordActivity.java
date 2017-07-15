package com.dioolcustomer.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.auth0.api.ParameterBuilder;
import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Auth0;
import com.auth0.core.Token;
import com.google.gson.Gson;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;

import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText mAdresseMailField;
    Button mBtnValidate;
    private AuthenticationAPIClient client;
    private SharedPreferences shared;
    Gson gson;
    Token mUserToken;

    Encryption encryption  = new Encryption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        setContentView(R.layout.activity_reset_password);
        getSupportActionBar().setTitle("Changer Mot de passe");

        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        mAdresseMailField = (EditText) findViewById(R.id.adresse_mail_field);
        mBtnValidate = (Button) findViewById(R.id.login_action_button);
        Auth0 auth0 = new Auth0(getString(R.string.auth_client_ID), getString(R.string.auth_client_DOMAIN));
        this.client = auth0.newAuthenticationAPIClient();
        this.client.setDefaultDbConnection(String.valueOf(getString(R.string.data_base)));

        String jsonToken = null;
        try {
            jsonToken = shared.getString("USER_TOKEN", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        gson = new Gson();
        mUserToken = gson.fromJson(jsonToken, Token.class);

        mBtnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MMMUtils.isConnectedTointernet(ResetPasswordActivity.this))
                    try {
                        if(mAdresseMailField.getText().toString().equals(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", "")))){
                            startChangePassword();
                        }else{
                            new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("DIOOL")
                                    .setContentText("Veuillez taper votre adresse email!")
                                    .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                else
                Toast.makeText(ResetPasswordActivity.this, "Veuillez vérifier votre connexion Internet",Toast.LENGTH_SHORT).show();


            }
        });

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Changer mot de passe" + "</font>")));

    }


    private void startChangePassword() {
        ParameterBuilder builder = ParameterBuilder.newBuilder();
        final ProgressDialog progress = ProgressDialog.show(ResetPasswordActivity.this, null, "Traitement en cours...", true);
        Map<String, Object> parameters = builder.setScope("openid app_metadata").asDictionary();
        client.changePassword(mAdresseMailField.getText().toString()).addParameters(parameters).start(new BaseCallback<Void>() {
            @Override
            public void onSuccess(Void payload) {
                progress.dismiss();
                new AlertDialog.Builder(ResetPasswordActivity.this)
                        .setCancelable(true)
                        .setTitle("Succés")
                        .setMessage("Un lien de changement de mot de passe est envoyé à cet adresse mail: " + mAdresseMailField.getText().toString())
                        .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                                final Intent newIntent = new Intent(ResetPasswordActivity.this, AuthActivity.class);
                                startActivity(newIntent);
                                finish();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Throwable error) {
                progress.dismiss();
                Log.e("rrr", error.getMessage()+"");
                new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Opération echouée")
                        .setContentText("Veuillez vérifier l'adresse mail saisie")
                        .show();
            }
        });
    }

}
