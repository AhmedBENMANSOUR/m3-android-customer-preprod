package com.dioolcustomer.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.dioolcustomer.R;
import com.dioolcustomer.adapters.CustomPagerAdapter;
import com.dioolcustomer.constants.GlobalConstants;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

public class MainActivity extends ActionBarActivity {
    public final String TAG = getClass().getSimpleName();
    private SharedPreferences shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Analytics.with(this).screen("View", TAG);
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        AutoScrollViewPager viewPager = (AutoScrollViewPager) findViewById(R.id.main_pager);
        viewPager.setAdapter(new CustomPagerAdapter(this));
        viewPager.setInterval(4000);
        viewPager.startAutoScroll();

        //Button toLoginButton = (Button) findViewById(R.id.to_login_button);
        Button toSignupButton = (Button) findViewById(R.id.to_signup_button);
        Button AuthLoginButton = (Button) findViewById(R.id.auth_signup_button);
        AuthLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AuthActivity.class));
                finish();
            }
        });

       /* toLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });*/

        toSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signupClick(view);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void signupClick(View view) {
        final Dialog signupDialog = new Dialog(MainActivity.this);
        signupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        signupDialog.setContentView(R.layout.dialog_signup_layout);
        Button signupCustomerButton = (Button) signupDialog.findViewById(R.id.button_signup_customer);
        Button signupMerchantButton = (Button) signupDialog.findViewById(R.id.button_signup_merchant);
        signupCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                intent.putExtra("userType", "customer");
                startActivity(intent);
                signupDialog.dismiss();
            }
        });

        signupMerchantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                intent.putExtra("userType", "merchant");
                startActivity(intent);
                signupDialog.dismiss();
            }
        });

        signupDialog.show();
    }

}



