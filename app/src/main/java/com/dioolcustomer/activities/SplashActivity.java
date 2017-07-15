package com.dioolcustomer.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.rampo.updatechecker.UpdateChecker;
import com.rampo.updatechecker.notice.Notice;
import com.rampo.updatechecker.store.Store;

public class SplashActivity extends AppCompatActivity  {

    private static final int SPLASH_SHOW_TIME = 5000;
    private SharedPreferences shared;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Prepare Preferences
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();


        if(shared.getBoolean("IS_FIRST_LAUNCH_MERCHANT_APPLICATION", true)) {
            editor.clear().commit();
            editor.putBoolean("IS_FIRST_LAUNCH_MERCHANT_APPLICATION",false);
            editor.commit();
        }


        YoYo.with(Techniques.BounceIn)
                .duration(2000)
                .playOn(findViewById(R.id.image_logo));

       checkNewVersion();

       new waitAnimation().execute();
    }

    private void checkNewVersion() {
        UpdateChecker mChecker = new UpdateChecker(this);
        mChecker.setNotice(Notice.NOTIFICATION);
        mChecker.setNoticeIcon(R.drawable.ic_launcher);
        mChecker.setStore(Store.GOOGLE_PLAY);
        mChecker.setSuccessfulChecksRequired(5);
        mChecker.start();
    }


    private class waitAnimation extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {



            try {
                Thread.sleep(SPLASH_SHOW_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            startActivity(new Intent(SplashActivity.this,TutorialActivity.class));
            finish();
        }

    }


}
