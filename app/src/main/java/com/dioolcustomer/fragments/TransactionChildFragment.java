package com.dioolcustomer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.google.gson.Gson;
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.activities.TransactionsHistoryActivity;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.History;
import com.dioolcustomer.models.HistoryTransaction;
import com.dioolcustomer.models.UserNetwork;
import com.dioolcustomer.utils.MMMUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class TransactionChildFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match


    String StartValue = "2000-04-23T18:25:43.511Z";
    String EndValue = "";
    int start = 0;
    int end = 49;
    int mNB=0;

    View mView;
    private RecyclerView mRecycleView;
    TextView mEmptyText,mLoadMoreText;
    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    Bundle mBundle;
    private JsonObjectRequest jsonObjReq;

    Gson gson;
    UserProfile mUserProfile;
    Token mUserToken;
    TextView mTextTotalTransfert, mNomreTransfert;
    private ArrayList<History> mListHistory = new ArrayList<>();
    UserNetwork mNetwork = new UserNetwork();

    public TransactionChildFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static TransactionChildFragment newInstance(String param1, String param2) {
        TransactionChildFragment fragment = new TransactionChildFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_transaction_child, container, false);

        mRecycleView = (RecyclerView) mView.findViewById(R.id.recycler_view_networks_details);
        mRecycleView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(mLayoutManager);

        mNomreTransfert = (TextView) mView.findViewById(R.id.texView_nb_transfert_child);
        mTextTotalTransfert = (TextView) mView.findViewById(R.id.texView_total_transfert_child);
        mLoadMoreText = (TextView) mView.findViewById(R.id.text_load_more);
        mEmptyText = (TextView) mView.findViewById(R.id.text_empty);



        //Prepare Preferences
        shared = getActivity().getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, getActivity().MODE_PRIVATE);
        editor = getActivity().getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, getActivity().MODE_PRIVATE).edit();

        gson = new Gson();

        String jsonProfile = shared.getString("USER_PROFILE", "");
        String jsonToken = shared.getString("USER_TOKEN", "");
        mUserProfile = gson.fromJson(jsonProfile, UserProfile.class);
        mUserToken = gson.fromJson(jsonToken, Token.class);

        mBundle = getArguments();
        if (mBundle != null) {
            mNetwork = mBundle.getParcelable("NETWORK");
        }

        if (MMMUtils.isConnectedTointernet(getActivity())) {
            getChildTransactionHistory(start,end);
        }
        else
        {
            Toast.makeText(getActivity(), "Veuillez vérifier votre connexion Internet", Toast.LENGTH_SHORT).show();

        }



        return  mView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    private void getChildTransactionHistory(final int startI, int EndI) {

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
        params.put("startDate", StartValue);
        params.put("endDate", EndValue);
        params.put("status", "0");
        params.put("indexStart", startI + "");
        params.put("indexEnd", EndI + "");
        //params.put("operations","[AIRTIME, CASHIN, CASHOUT, INTERNAL_TRANSFER]");
        //"users" :["auth0|M3_PARIS"]
        params.put("users","["+mNetwork.getUserId()+"]");


        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_HISTORY, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();

                        HistoryTransaction mHistoryResponse = new HistoryTransaction().createHistory(response);
                        Log.e("size", mHistoryResponse.getMListHistory().size()+"");

                        // Prepare next page
                        start = end + 1;
                        end = end + 51;

                        for (int i = 0; i < mHistoryResponse.getMListHistory().size(); i++)
                            mListHistory.add(mHistoryResponse.getMListHistory().get(i));


                        // Enable/Disable load more
                        if (mHistoryResponse.getMListHistory().size() >= 50)
                            mLoadMoreText.setVisibility(View.VISIBLE);
                        else mLoadMoreText.setVisibility(View.GONE);


                        if (mListHistory.size() > 0) {
                            mNB+=(int)mHistoryResponse.getMNombre();

                            mNomreTransfert.setText(String.valueOf(mNB));
                            mTextTotalTransfert.setText(mHistoryResponse.getMTotal().toString());

                            if (!mHistoryResponse.getMLimited().toString().equals("null")) {
                                // Inform User That List is Not completed
                                Crouton.makeText(getActivity(), "La liste affichée est tronquée, veuillez charger plus des transactions!", Style.INFO).show();
                            }

                            mEmptyText.setVisibility(View.GONE);
                            mRecycleView.setVisibility(View.VISIBLE);

                            //mHistoryAdapter = new TransactionsHistoryAdapter(mListHistory, TransactionsHistoryActivity.this);
                            //mRecycleView.setAdapter(mHistoryAdapter);

                        } else {
                            mEmptyText.setVisibility(View.VISIBLE);
                            mRecycleView.setVisibility(View.GONE);
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Erreur", "Error: " + error.toString());
                dialog.dismiss();

                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DIOOL")
                        .setContentText("Veuillez réessayer de nouveau!")
                        .show();
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                Log.e("Autorization", mUserToken.getType() + " " + shared.getString("USER_ID_TOKEN", ""));
                headers.put("Authorization", mUserToken.getType() + " " + shared.getString("USER_ID_TOKEN", ""));
                return headers;
            }


        };


        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, TransactionsHistoryActivity.class.getCanonicalName());
    }




}
