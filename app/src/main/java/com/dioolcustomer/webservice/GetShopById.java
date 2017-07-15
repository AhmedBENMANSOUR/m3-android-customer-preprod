package com.dioolcustomer.webservice;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.core.Token;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.ShopById;
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.services.concurrency.AsyncTask;

/**
 * Created by ASUS on 13/01/2017.
 */

public class GetShopById extends AppCompatActivity {


    Token mUserToken;
    JsonObjectRequest jsonObjReq;
    private SharedPreferences shared;

    ShopById mShop;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Prepare Preferences
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        /*editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        new  AsynchroneTest().execute();
    }


    public void getShopById(final Token mUserToken, String userIdToken, String shopId, String... strings) {

        String userId = strings[0];
        String email = strings[1];
        String firstName = strings[2];
        String lastName = strings[3];
        String businessName = strings[4];
        String mUserTye = strings[5];
        String phone = strings[6];
        String idCard = strings[7];
        String idValidityType = strings[8];
        String idType = strings[9];
        String app_version = strings[10];

         AsynchroneTest asynchroneTest = new AsynchroneTest();
         asynchroneTest.doInBackground(userIdToken, shopId,userId,email,firstName,lastName
        ,businessName,mUserTye, phone,idCard,idValidityType, idType,app_version);

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("GetShopById Page") // TODO: Define a title for the content shown.
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




    private class AsynchroneTest extends AsyncTask<String, String, String> {



        @Override
        protected String doInBackground(final String... strings) {
            Map<String, String> params = new HashMap<String, String>();
            params.put("shopId", strings[1]);//final Token mUserToken, final String userIdToken, String shopId


            final String token = strings[0];
            final String shopName = null;

            Log.e("params GetShopById", params.toString() + "");
            jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    GlobalConstants.URL_GET_SHOP_NAME_By_ID, new JSONObject(params),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                mShop = new ShopById().createShopId(response);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            System.out.println(mShop.toString());
                            System.out.println("Strings[0] : "+strings[0]);
                            System.out.println("Strings[1] : "+strings[1]);
                            System.out.println("Strings[2] : "+strings[2]);
                            System.out.println("Strings[3] : "+strings[3]);
                            System.out.println("Strings[4] : "+strings[4]);
                            System.out.println("Strings[5] : "+strings[5]);
                            System.out.println("Strings[6] : "+strings[6]);
                            System.out.println("Strings[7] : "+strings[7]);
                            System.out.println("Strings[8] : "+strings[8]);
                            System.out.println("Strings[9] : "+strings[9]);
                            System.out.println("Strings[10] : "+strings[10]);
                            System.out.println("Strings[11] : "+strings[11]);
                            System.out.println("Strings[12] : "+strings[12]);
                           // System.out.println("getString(R.string.app_version) : "+getString(R.string.app_version));


                            Traits traits = new Traits().putValue("email",strings[3]).putValue("firstname",strings[4])
                                    .putValue("lastname",strings[5]).putValue("businessName",strings[6])
                                    .putValue("profile",strings[7]).putValue("phone",strings[8]).putValue("idcard",strings[9])
                                    .putValue("idvaliditydate",strings[10]).putValue("idType",strings[11])
                                    .putValue("shopname",mShop.getName()).putValue("version",strings[12]);
                            Analytics.with(GetShopById.this).identify(strings[2],traits,null);
                            // return mShop.getName();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println();

                }
            }) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    Log.e("Autorization", "Bearer" + " " + token);
                    headers.put("Authorization", "Bearer" + " " + token);
                    return headers;
                }


            };

            jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            /// Adding request to request queue
            MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, GetShopById.class.getCanonicalName());
            return null;

        }

        protected void onProgressUpdate(Integer... progress) {
           // setProgressPercent(progress[0]);
        }

        @Override
        protected void onPreExecute(){
            System.out.println("pre execute");
        }
        @Override
        protected void onPostExecute(String result) {
          // super.onPreExecute();
            Toast.makeText(GetShopById.this,"un message",  Toast.LENGTH_LONG).show();
            System.out.println("fin de consommation de service web");
        }
    }

}
