package com.dioolcustomer.activities;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.core.Token;
import com.google.gson.Gson;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.security.Encryption;

public class GestionCompteActivity extends AppCompatActivity {

    Gson gson;
    Token mUserToken;

    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    RelativeLayout mLayout1, mLayout2;
    TextView mEmptyAccountText, mTextC1, mTextC2;
    ImageView mUpdateAccount1,mUpdateAccount2;
    ImageView mDeleteAccount1,mDeleteAccount2;

    Encryption encryption  = new Encryption();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_compte);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#009633"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Gestion des comptes" + "</font>")));


        mLayout1= (RelativeLayout) findViewById(R.id.layout1);
        mLayout2= (RelativeLayout) findViewById(R.id.layout2);
        mEmptyAccountText= (TextView) findViewById(R.id.text_empty_account);
        mTextC1= (TextView) findViewById(R.id.text_comte_1);
        mTextC2= (TextView) findViewById(R.id.text_comte_2);
        mUpdateAccount1= (ImageView) findViewById(R.id.text_update_compte1);
        mUpdateAccount2= (ImageView) findViewById(R.id.text_update_compte2);
        mDeleteAccount1= (ImageView) findViewById(R.id.text_delete_compte1);
        mDeleteAccount2= (ImageView) findViewById(R.id.text_delete_compte2);


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

        mUpdateAccount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mUpdateAccount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mDeleteAccount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mDeleteAccount2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        try {
            mTextC1.setText(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_ACCOUNT_1", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mTextC2.setText(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_ACCOUNT_2", "")));
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if((encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_ACCOUNT_1", "")).equals(""))&&(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_ACCOUNT_2", "")).equals("")))
                mEmptyAccountText.setVisibility(View.VISIBLE);
            else if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_ACCOUNT_1", "")).equals("")) {
                mLayout1.setVisibility(View.GONE);
            }
            else if(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_ACCOUNT_2", "")).equals("")) {
                mLayout2.setVisibility(View.GONE);
            }
            else {
                mLayout1.setVisibility(View.VISIBLE);
                mLayout2.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Quote Request" + "</font>")));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gestion_compte, menu);
        try {
            if(!(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_ACCOUNT_1", "")).equals(""))&&!(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_ACCOUNT_2", "")).equals("")))
                menu.findItem(R.id.add_account).setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Handle
        switch (item.getItemId()) {
            case R.id.add_account: {
                showAddAccountAlert();
                return true;
            }
        }

        return true;
    }

    private void showAddAccountAlert() {
        final Dialog dialog = new Dialog(GestionCompteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_add_account);
        dialog.setTitle("DIOOL");


        Button mSendButton = (Button) dialog.findViewById(R.id.create_account_button);
        final EditText mText = (EditText) dialog.findViewById(R.id.text_id_account);
        //mText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_ACCOUNT_1", "")).equals(""))
                     {
                         mLayout1.setVisibility(View.VISIBLE);
                         mEmptyAccountText.setVisibility(View.GONE);
                         mTextC1.setText(mText.getText().toString());
                         editor.putString("USER_ID_ACCOUNT_1", mText.getText().toString());
                         editor.commit();
                     }

                     else  if (encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_ACCOUNT_2", "")).equals(""))
                    {
                        mLayout2.setVisibility(View.VISIBLE);
                        mTextC2.setText(mText.getText().toString());
                        mEmptyAccountText.setVisibility(View.GONE);
                        editor.putString("USER_ID_ACCOUNT_2", mText.getText().toString());
                        editor.commit();
                        invalidateOptionsMenu();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        MyMoneyMobileApplication.getInstance().touch();
    }


}
