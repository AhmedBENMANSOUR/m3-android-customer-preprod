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
import com.dioolcustomer.models.ChildrensEmails;
import com.dioolcustomer.security.Encryption;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 20/02/2017.
 */

public class GetChildrensEmails {



    JsonObjectRequest jsonObjReq;
    public String TAG = getClass().getSimpleName();
    private SharedPreferences shared;
    ChildrensEmails mChildrensEmails = new ChildrensEmails();

    Encryption encryption  = new Encryption();

    public String getChildrensEmails(final String userId,final Token mUserToken,String[] children,String parentId) throws Exception {
        // Personalize View when loading WS



        //create table issuerParents
        List<String> parentsIdList = new ArrayList<>();
      /*  Object [] issuerParents;
        JSONObject jsnobject = new JSONObject("{parent_ids:"+encryption.decrypt(mUserToken.getIdToken(),shared.getString("PARENT_IDS", ""))+"}");

        JSONArray jsonArray = jsnobject.getJSONArray("parent_ids");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            if(explrObject.has("parent_id")){
                parentsIdList.add(explrObject.getString("parent_id"));
            }
        }
        issuerParents  = parentsIdList.toArray();*/
        //byte[] b = shared.getString("SHOP_ID", "").toString().getBytes(Charset.forName("UTF-8"));
        //   System.out.println("Encryption.decrypt(mUserToken.getIdToken(),b) : "+encryption.decrypt(mUserToken.getIdToken(),b));

        // Sending parameters
        Map<String, Object> params = new HashMap<String, Object>();

     /*   try{
            params.put("shopId", encryption.decrypt(mUserToken.getIdToken(),shared.getString("SHOP_ID", "")));
        }catch (Exception e){
            e.printStackTrace();
        }
        params.put("channel", "Mobile");
        params.put("issuerParents",issuerParents);
        params.put("confirm", true);*/
        params.put("userId",userId);
        params.put("children",children);
        params.put("parentId",parentId);


        Log.e("params TRASFERT", params.toString() + "");



        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_TRNSFERT, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e(TAG, response.toString());
                        try {
                            mChildrensEmails = new ChildrensEmails().createChildrensEmails(response);
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







}
