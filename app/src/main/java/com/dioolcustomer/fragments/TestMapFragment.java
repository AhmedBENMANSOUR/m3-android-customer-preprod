package com.dioolcustomer.fragments;
import android.Manifest;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
        import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
        import android.os.Bundle;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


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
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.TransfertArgentResponse;
import com.dioolcustomer.models.quoterequest.CustomerActiveQuoteRequest;
import com.dioolcustomer.security.Encryption;
import com.google.android.gms.maps.CameraUpdate;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapView;
        import com.google.android.gms.maps.MapsInitializer;
        import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.Reader;
        import java.io.StringWriter;
        import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TestMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TestMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestMapFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";



    public String TAG = getClass().getSimpleName();
    private int quouteRequestId;
    Encryption encryption = new Encryption();

    MapView mapView;
    GoogleMap googleMap;
    private WebView contactWebView;

    EditText txtAmount;
    TextView amountTextView,operationTextView;
    Button mBtnSend, cancelButton, closeQuoteRequestButton;
    LinearLayout activeQuoteRequestLayout;
    LinearLayout closeQuoteRequestLayout;
    private RadioGroup radioOperationGroup;
    RadioButton radioOperationButton;
    String operation;

    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    JsonObjectRequest jsonObjReq;
    Gson gson;
    UserProfile mUserProfile;
    Token mUserToken;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public TestMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestMapFragment newInstance(String param1, String param2) {
        TestMapFragment fragment = new TestMapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // txtAmount = (EditText) getActivity().findViewById(R.id.quote_request_amount_layout);

        shared = this.getActivity().getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, getActivity().MODE_PRIVATE);
        editor = this.getActivity().getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, getActivity().MODE_PRIVATE).edit();

        gson = new Gson();
        String jsonToken = shared.getString("USER_TOKEN", "");
        mUserToken = gson.fromJson(jsonToken, Token.class);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        try {
            checkActiveQuoteRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       final View view = inflater.inflate(R.layout.fragment_test_map, container, false);
        getActivity().setTitle("Contact Us");
        contactWebView = (WebView)view.findViewById(R.id.contact_us);
        contactWebView.setBackgroundColor(0);
        if(Build.VERSION.SDK_INT >= 11){
            contactWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
        contactWebView.getSettings().setBuiltInZoomControls(true);
        AssetManager mgr = getContext().getAssets();
        String filename = null;

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        txtAmount = (EditText) view.findViewById(R.id.quote_request_amount_edit);
        mBtnSend = (Button) view.findViewById(R.id.quote_request_buttonSend);
        cancelButton = (Button) view.findViewById(R.id.quote_request_buttonCancel);
        closeQuoteRequestButton = (Button)view.findViewById(R.id.close_quote_request_button);
        activeQuoteRequestLayout = (LinearLayout) view.findViewById(R.id.quote_request_layout);
        closeQuoteRequestLayout = (LinearLayout) view.findViewById(R.id.close_quote_request_layout);
        amountTextView= (TextView) view.findViewById(R.id.amount_quote_request_textview);
        operationTextView = (TextView) view.findViewById(R.id.operation_quote_request_textview);
        radioOperationGroup = (RadioGroup) view.findViewById(R.id.radioOperation);


        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedId = radioOperationGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioOperationButton = (RadioButton) view.findViewById(selectedId);

                operation  = (String) radioOperationButton.getText();

                try {
                    startQuoteRequest();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();

                }


            }
        });

        closeQuoteRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    closeQuoteRequest();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });


        if (mapView != null) {
            mapView.getMapAsync((new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.addMarker(new MarkerOptions().title("Paris").position(new LatLng(4.047795,9.767451)));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(4.047795,9.767451), 15));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                        android.Manifest.permission.ACCESS_FINE_LOCATION},
                                2);
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);


                }
            }));


         /*   Marker i = googleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon))
                    .position(new LatLng(55.854049, 13.661331)));*/

         //  googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return view;
            }
       //     googleMap.setMyLocationEnabled(true);
        //    googleMap.getUiSettings().setZoomControlsEnabled(true);
            MapsInitializer.initialize(this.getActivity());
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(55.854049, 13.661331));
            LatLngBounds bounds = builder.build();
            int padding = 0;
            // Updates the location and zoom of the MapView
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
       //     googleMap.moveCamera(cameraUpdate);
        }
        return view;
    }

  /*  protected synchronized void buildGoogleApiClient() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient().Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }*/
    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    public static String StreamToString(InputStream in) throws IOException {
        if(in == null) {
            return "";
        }
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
        }
        return writer.toString();
    }




/////////////////////////////////////////////////////////////web service /////////////////////////////////////////////////////////////////
private void startQuoteRequest() throws Exception {
    Log.e("aaaa", encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", "")).toString());

    // Personalize View when loading WS
    final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
            .direction(ACProgressConstant.DIRECT_CLOCKWISE)
            .themeColor(Color.WHITE)
            .text("Patientez...")
            .fadeColor(Color.DKGRAY).build();
    dialog.setCancelable(false);
    dialog.show();

    //create table issuerParents
    List<String> parentsIdList = new ArrayList<>();
    Object [] issuerParents;
    JSONObject jsnobject = new JSONObject("{parent_ids:"+encryption.decrypt(mUserToken.getIdToken(),shared.getString("PARENT_IDS", ""))+"}");

    JSONArray jsonArray = jsnobject.getJSONArray("parent_ids");
    for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject explrObject = jsonArray.getJSONObject(i);
        if(explrObject.has("parent_id")){
            parentsIdList.add(explrObject.getString("parent_id"));
        }
    }
    issuerParents  = parentsIdList.toArray();

   // int selectedId = radioOperationGroup.getCheckedRadioButtonId();

    // find the radiobutton by returned id
    ///radioSexButton = (RadioButton) view.findViewById(selectedId);


    // Sending parameters
    Map<String, Object> params = new HashMap<String, Object>();
    params.put("amount", Float.parseFloat(String.valueOf(txtAmount.getText())));
    params.put("serviceType", operation);
    params.put("latitude", 4.047795);
    params.put("longitude", 9.767451);
    params.put("address", "Dooala");


    Log.e("params CASFIN", params.toString() + "");

    jsonObjReq = new JsonObjectRequest(Request.Method.POST,
            GlobalConstants.URL_QUOTE_REQUEST, new JSONObject(params),
            new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG, response.toString());
                    dialog.dismiss();
                    final TransfertArgentResponse mTransfertArgentResponse = new TransfertArgentResponse().createTransfertObject(response);
                    activeQuoteRequestLayout.setVisibility(View.GONE);
                    closeQuoteRequestLayout.setVisibility(View.VISIBLE);




                }
            }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e(TAG, "Error: " + error.getMessage());
            dialog.dismiss();

            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("DIOOL")
                    .setContentText("L'opération a échoué")
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
            try {
                Log.e("Autorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
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

    jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
            0,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    /// Adding request to request queue
    MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, getClass().getCanonicalName());
}







    private void checkActiveQuoteRequest() throws Exception {
        Log.e("aaaa", encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", "")).toString());

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();

        //create table issuerParents
        List<String> parentsIdList = new ArrayList<>();
        Object [] issuerParents;
        JSONObject jsnobject = new JSONObject("{parent_ids:"+encryption.decrypt(mUserToken.getIdToken(),shared.getString("PARENT_IDS", ""))+"}");

        JSONArray jsonArray = jsnobject.getJSONArray("parent_ids");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            if(explrObject.has("parent_id")){
                parentsIdList.add(explrObject.getString("parent_id"));
            }
        }
        issuerParents  = parentsIdList.toArray();






        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                GlobalConstants.URL_CHECK_CUSTOMER_ACTIVE_QUOTE_REQUEST, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();

                        final CustomerActiveQuoteRequest mCustomerActiveQuoteRequest;
                        try {
                            mCustomerActiveQuoteRequest = new CustomerActiveQuoteRequest().createCustomerActiveQuoteRequest(response);
                            System.out.println("mCustomerActiveQuoteRequest.getMerchantLatitude() : "+mCustomerActiveQuoteRequest.getMerchantLatitude());
                            if(mCustomerActiveQuoteRequest.getId() != null){
                                quouteRequestId = mCustomerActiveQuoteRequest.getId();
                                amountTextView.setText((mCustomerActiveQuoteRequest.getAmount()).toString());
                                operationTextView.setText(mCustomerActiveQuoteRequest.getServiceType());

                                activeQuoteRequestLayout.setVisibility(View.GONE);
                                closeQuoteRequestLayout.setVisibility(View.VISIBLE);

                                if(mCustomerActiveQuoteRequest.getMerchantLatitude() != null){
                                    mapView.getMapAsync((new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap googleMap) {
                                            googleMap.addMarker(new MarkerOptions().title(mCustomerActiveQuoteRequest.getMerchantShopname()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)).position(new LatLng(mCustomerActiveQuoteRequest.getMerchantLatitude(),mCustomerActiveQuoteRequest.getMerchantLongitude())));

                                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(4.04525,9.700051), 15));
                                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


                                            googleMap.setMyLocationEnabled(true);



                                        }
                                    }));

                                }
                        }
                        else{
                                closeQuoteRequestLayout.setVisibility(View.GONE);
                                activeQuoteRequestLayout.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }










                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DIOOL")
                        .setContentText("L'opération a échoué")
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
                try {
                    Log.e("Autorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
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

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, getClass().getCanonicalName());
    }





    /////////////////////////CLOSE QUOTE REQUEST ////////////////////////////////////////////


    private void closeQuoteRequest() throws Exception {
        Log.e("aaaa", encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", "")).toString());

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(getActivity(), R.style.myDialog))
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
                .text("Patientez...")
                .fadeColor(Color.DKGRAY).build();
        dialog.setCancelable(false);
        dialog.show();

        //create table issuerParents
        List<String> parentsIdList = new ArrayList<>();
        Object [] issuerParents;
        JSONObject jsnobject = new JSONObject("{parent_ids:"+encryption.decrypt(mUserToken.getIdToken(),shared.getString("PARENT_IDS", ""))+"}");

        JSONArray jsonArray = jsnobject.getJSONArray("parent_ids");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject explrObject = jsonArray.getJSONObject(i);
            if(explrObject.has("parent_id")){
                parentsIdList.add(explrObject.getString("parent_id"));
            }
        }
        issuerParents  = parentsIdList.toArray();




        Map<String, Object> params = new HashMap<String, Object>();
        params.put("quoteRequestId", quouteRequestId);


        jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                GlobalConstants.URL_CLOSE_QUOTE_REQUEST, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();


                        activeQuoteRequestLayout.setVisibility(View.VISIBLE);
                        closeQuoteRequestLayout.setVisibility(View.GONE);










                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                dialog.dismiss();

                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("DIOOL")
                        .setContentText("L'opération a échoué")
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
                try {
                    Log.e("Autorization", mUserToken.getType() + " " + encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID_TOKEN", "")));
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

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, getClass().getCanonicalName());
    }



}
