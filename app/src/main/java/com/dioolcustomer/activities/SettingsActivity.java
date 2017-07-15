package com.dioolcustomer.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.utils.MMMUtils;

import io.intercom.android.sdk.Intercom;

public class SettingsActivity extends AppCompatActivity {

    TextView mChangeText, mCompteText, mTextProfile, mTextMention;
    SwitchCompat mSwitchSaving;


    private SharedPreferences shared;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Paramétres");

        mChangeText = (TextView) findViewById(R.id.text_changing_mdp);
        mCompteText = (TextView) findViewById(R.id.text_compte);
      //  mSwitchSaving = (SwitchCompat) findViewById(R.id.Switch_pdf);
        mTextProfile= (TextView) findViewById(R.id.text_profile);
        mTextMention  = (TextView) findViewById(R.id.text_metion);

        //Prepare Preferences
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();

        // Init Toggle Button
     /*   if (shared.getBoolean("Saving_Pdf", false))
            mSwitchSaving.setChecked(true);
        else
            mSwitchSaving.setChecked(false);
*/

        mTextProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,
                        UserProfileActivity.class);
                startActivity(intent);
            }
        });


        mChangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,
                        ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

        mCompteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,
                        GestionCompteActivity.class);
                startActivity(intent);
            }
        });


        mTextMention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);

              //  Uri pdf_uri = Uri.parse("https://preprod.mymoneytop.com/m3-home/assets/documents/CGU_MyMoneyMobile_Entreprise.pdf");
                 /* DownloadManager.Request request  = new DownloadManager.Request(pdf_uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                Long reference = downloadManager.enqueue(request);

                System.out.println("reference : "+reference);*/

              //  DownloadData(pdf_uri,v);
                //https://sites.google.com/site/compiletimeerrorcom/android-programming/oct-2013/DownloadManagerAndroid1.zip
                String servicestring = Context.DOWNLOAD_SERVICE;
                DownloadManager downloadmanager;
                downloadmanager = (DownloadManager) getSystemService(servicestring);
                Uri uri = Uri
                        .parse("https://emonize.mymoneytop.com/m3-home/assets/documents/CGU_MyMoneyMobile_Entreprise.pdf");
                DownloadManager.Request request = new DownloadManager.Request(uri);
                Long reference = downloadmanager.enqueue(request);

            }
        });


      /*  mSwitchSaving.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean("Saving_Pdf", true);
                    editor.commit();
                } else {
                    editor.putBoolean("Saving_Pdf", false);
                    editor.commit();
                }
            }
        });*/

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Paramètres" + "</font>")));
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
                Intent intent = new Intent(SettingsActivity.this,
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


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        MyMoneyMobileApplication.getInstance().touch();
        Log.d(SettingsActivity.class.getCanonicalName(), "User interaction to " + this.toString());
    }




    //////////Download file //////////////////////////



    //// download data

    DownloadManager downloadManager;

    private long DownloadData (Uri uri, View v) {

        long downloadReference;

        // Create request for android download manager
         downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        //Setting title of request
        request.setTitle("Data Download");

        //Setting description of request
        request.setDescription("Android Data download using DownloadManager.");

        //Set the local destination for the downloaded file to a path within the application's external files directory

            request.setDestinationInExternalFilesDir(SettingsActivity.this, Environment.DIRECTORY_DOWNLOADS,"condition.pdf");

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request);

      /*  Button DownloadStatus = (Button) findViewById(R.id.DownloadStatus);
        DownloadStatus.setEnabled(true);
        Button CancelDownload = (Button) findViewById(R.id.CancelDownload);
        CancelDownload.setEnabled(true);*/

        return downloadReference;
    }



    //Check Download Status

    private void Check_Image_Status(long Image_DownloadId) {

        DownloadManager.Query ImageDownloadQuery = new DownloadManager.Query();
        //set the query filter to our previously Enqueued download
        ImageDownloadQuery.setFilterById(Image_DownloadId);

        //Query the download manager about downloads that have been requested.
        Cursor cursor = downloadManager.query(ImageDownloadQuery);
        if(cursor.moveToFirst()){
            DownloadStatus(cursor, Image_DownloadId);
        }

    }


    //// Download status


    private void DownloadStatus(Cursor cursor, long DownloadId){

        //column for download  status
        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
        int status = cursor.getInt(columnIndex);
        //column for reason code if the download failed or paused
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        int reason = cursor.getInt(columnReason);
        //get the download filename
        int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
        String filename = cursor.getString(filenameIndex);

        String statusText = "";
        String reasonText = "";

        switch(status){
            case DownloadManager.STATUS_FAILED:
                statusText = "STATUS_FAILED";
                switch(reason){
                    case DownloadManager.ERROR_CANNOT_RESUME:
                        reasonText = "ERROR_CANNOT_RESUME";
                        break;
                    case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                        reasonText = "ERROR_DEVICE_NOT_FOUND";
                        break;
                    case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                        reasonText = "ERROR_FILE_ALREADY_EXISTS";
                        break;
                    case DownloadManager.ERROR_FILE_ERROR:
                        reasonText = "ERROR_FILE_ERROR";
                        break;
                    case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        reasonText = "ERROR_HTTP_DATA_ERROR";
                        break;
                    case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                        reasonText = "ERROR_INSUFFICIENT_SPACE";
                        break;
                    case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                        reasonText = "ERROR_TOO_MANY_REDIRECTS";
                        break;
                    case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                        reasonText = "ERROR_UNHANDLED_HTTP_CODE";
                        break;
                    case DownloadManager.ERROR_UNKNOWN:
                        reasonText = "ERROR_UNKNOWN";
                        break;
                }
                break;
            case DownloadManager.STATUS_PAUSED:
                statusText = "STATUS_PAUSED";
                switch(reason){
                    case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                        reasonText = "PAUSED_QUEUED_FOR_WIFI";
                        break;
                    case DownloadManager.PAUSED_UNKNOWN:
                        reasonText = "PAUSED_UNKNOWN";
                        break;
                    case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                        reasonText = "PAUSED_WAITING_FOR_NETWORK";
                        break;
                    case DownloadManager.PAUSED_WAITING_TO_RETRY:
                        reasonText = "PAUSED_WAITING_TO_RETRY";
                        break;
                }
                break;
            case DownloadManager.STATUS_PENDING:
                statusText = "STATUS_PENDING";
                break;
            case DownloadManager.STATUS_RUNNING:
                statusText = "STATUS_RUNNING";
                break;
            case DownloadManager.STATUS_SUCCESSFUL:
                statusText = "STATUS_SUCCESSFUL";
                reasonText = "Filename:\n" + filename;
                break;
        }

        if(DownloadId == 1) {

            Toast toast = Toast.makeText(SettingsActivity.this,
                    "Music Download Status:" + "\n" + statusText + "\n" +
                            reasonText,
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        }
        else {

            Toast toast = Toast.makeText(SettingsActivity.this,
                    "Image Download Status:"+ "\n" + statusText + "\n" +
                            reasonText,
                    Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

            // Make a delay of 3 seconds so that next toast (Music Status) will not merge with this one.
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                }
            }, 3000);

        }

    }


}
