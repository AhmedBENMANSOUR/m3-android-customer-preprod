package com.dioolcustomer.webservice;

import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.core.Token;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.MobileAppVersion;
import com.dioolcustomer.security.Encryption;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ASUS on 28/02/2017.
 */

public class CheckMobileAppVersion {



    JsonObjectRequest jsonObjReq;
    public String TAG = getClass().getSimpleName();
    private SharedPreferences shared;
    MobileAppVersion mMobileAppVersion = new MobileAppVersion();

    Encryption encryption  = new Encryption();

    public void checkVersion( final Token mUserToken) throws Exception {
        // Personalize View when loading WS


        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                GlobalConstants.GET_APP_VERSION, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, response.toString());
                        try {
                            mMobileAppVersion = new MobileAppVersion().createMobileAppVersion(response);
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
                    //System.out.println("encryption.decrypt(mUserToken.getIdToken(),shared.getString(\"USER_ID_TOKEN\", \"\")) : "+encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, CheckMobileAppVersion.class.getCanonicalName());



    }









}
