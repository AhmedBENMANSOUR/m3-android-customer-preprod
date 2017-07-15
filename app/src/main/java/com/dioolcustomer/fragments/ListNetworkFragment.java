package com.dioolcustomer.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.activities.ListNetworkActivity;
import com.dioolcustomer.activities.SettingsActivity;
import com.dioolcustomer.adapters.UserNerworkAdapter;
import com.dioolcustomer.callbacks.OpenNetworkDetails;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.UserNetworkResponse;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import io.intercom.android.sdk.Intercom;

public class ListNetworkFragment extends Fragment {


    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    JsonObjectRequest jsonObjReq;
    public final String TAG = getClass().getSimpleName();

    final static String STRING_BUILDER_0 = "[";
    final static String STRING_BUILDER_1 = "\"childId\"";
    final static String STRING_BUILDER_2 = ",";
    final static String STRING_BUILDER_3 = "]";


    Gson gson;
    Token mUserToken;
    UserProfile mUserProfile;

    RecyclerView mRecycleView;
    TextView mEmptyText;

    Encryption encryption  = new Encryption();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_list_network, container, false);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#1a1f3a"));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setBackgroundDrawable(colorDrawable);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Mon réseau" + "</font>")));

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mRecycleView= (RecyclerView) mView.findViewById(R.id.recycler_view_networks);
        mEmptyText= (TextView) mView.findViewById(R.id.text_empty);
        mRecycleView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(mLayoutManager);

        //Prepare Preferences
        shared = getActivity().getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, Context.MODE_PRIVATE).edit();


        String jsonToken = shared.getString("USER_TOKEN", "");
        Log.e("USER_TOKEN", jsonToken);
        gson = new Gson();
        mUserToken = gson.fromJson(jsonToken, Token.class);

        String jsonProfile = null;
        try {
            jsonProfile = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_PROFILE", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUserProfile = gson.fromJson(jsonProfile, UserProfile.class);

        if (MMMUtils.isConnectedTointernet(getActivity()))
            getUserChildrens();
        else {
            Toast.makeText(getActivity(), "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();

        }


        return mView;
    }




    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.network_menu, menu);
        getActivity().getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }




    @Subscribe
    public void onEvent(OpenNetworkDetails mNetwork) {
        Bundle mBundle=new Bundle();
        mBundle.putParcelable("NETWORK", mNetwork.getmNetwork());
        MMMUtils.addFragment(new NetworkDetailsFragment(),mBundle,NetworkDetailsFragment.class.getCanonicalName(),true,getActivity(),R.id.frame_network);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // Handle
        switch (item.getItemId()) {
            case R.id.add_network: {
                return true;
            }

            case R.id.settings:
                Intent intent = new Intent(getActivity(),
                        SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                MMMUtils.logoutUser(getActivity());

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


  /*  @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        MyMoneyMobileApplication.getInstance().touch();
    }*/


    private void getUserChildrens() {

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();

        // Sending parameters
        Map<String, String> params = new HashMap<String, String>();
        JSONObject mUserMetaData, mUserBusinessData;

        String userChild = "";
        mUserMetaData = new JSONObject(mUserProfile.getExtraInfo());
        JSONArray mjson = null;
        try {
            mUserBusinessData = mUserMetaData.getJSONObject("app_metadata");
            mjson = mUserBusinessData.getJSONArray("children");
            userChild=mjson+"";
        } catch (JSONException e) {
            e.printStackTrace();
        }


        String mValues="[";
        char quotes='"';

        if(mjson != null){
            for(int i =0;i<mjson.length();i++)
            {
                try {
                    mValues+=quotes+mjson.getString(i)+quotes;
                    if(i==mjson.length()-1)
                        mValues+="]";

                    else

                        mValues+=',';
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }



        Log.e("PARAMS", mValues.replaceAll("^\"|\"$", ""));
        params.put("userId", mUserProfile.getId());
        params.put("childrens", mValues.replaceAll("^\"|\"$", ""));

        JSONObject mObject=new JSONObject();
        try {
            mObject=new JSONObject(params.toString().replace('\"',' '));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_GET_NETWORKS,mObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        // Log.e(TAG, response.toString());
                        dialog.dismiss();

                        UserNetworkResponse mResponse=new UserNetworkResponse().createNetworks(response);

                        if(mResponse.getMUsers().size()>0) {
                            UserNerworkAdapter mAdapter = new UserNerworkAdapter(mResponse.getMUsers(), getActivity());
                            mRecycleView.setAdapter(mAdapter);
                            mRecycleView.setVisibility(View.VISIBLE);
                            mEmptyText.setVisibility(View.GONE);
                        }

                        else
                        {
                            mRecycleView.setVisibility(View.GONE);
                            mEmptyText.setVisibility(View.VISIBLE);
                        }


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
                    Log.e("TOKEN", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, ListNetworkActivity.class.getCanonicalName());
    }


}
