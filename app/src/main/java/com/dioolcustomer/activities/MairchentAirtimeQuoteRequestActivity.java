package com.dioolcustomer.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.constants.GlobalStatic;
import com.dioolcustomer.models.TransfertArgentResponse;
import com.dioolcustomer.models.quoterequest.CustomerActiveQuoteRequest;
import com.dioolcustomer.security.Encryption;
import com.dioolcustomer.utils.MMMUtils;
import com.dioolcustomer.utils.Waiter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.intercom.android.sdk.Intercom;

public class MairchentAirtimeQuoteRequestActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, LocationSource,
        LocationListener, ActivityCompat.OnRequestPermissionsResultCallback{


    GoogleMap mGoogleMap;

    SupportMapFragment mapFragment;

    Marker marker;
    Geocoder geocoder;
    List<Address> addresses;
    private boolean enabled_gps= false;


    static double latitude;
    static double langitude;
    static String address;
    private int quouteRequestId;
    String operation;
    public String TAG = getClass().getSimpleName();

    EditText txtAmount;
    TextView amountTextView,operationTextView,messageQouteRequest,shopNameQuoteRequest,shopAddressQuoteRequest;
    Button mBtnSend, cancelButton, closeQuoteRequestButton;
    private RadioGroup radioOperationGroup;
    RadioButton radioOperationButton;
    LinearLayout activeQuoteRequestLayout;
    LinearLayout closeQuoteRequestLayout, shopNameLayout, shopAddressLayout;

    Token mUserToken;
    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    JsonObjectRequest jsonObjReq;
    Gson gson;



    Encryption encryption = new Encryption();
    GlobalStatic globalStatic = new GlobalStatic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mairchent_airtime_quote_request);

        mBtnSend = (Button) findViewById(R.id.buttonSend);
        cancelButton = (Button) findViewById(R.id.buttonCancel);
        radioOperationGroup = (RadioGroup) findViewById(R.id.radioOperation);
        txtAmount = (EditText) findViewById(R.id.quote_request_amount_edit);
        amountTextView= (TextView) findViewById(R.id.amount_quote_request_textview);
        operationTextView = (TextView) findViewById(R.id.operation_quote_request_textview);
        activeQuoteRequestLayout = (LinearLayout) findViewById(R.id.quote_request_layout);
        closeQuoteRequestLayout = (LinearLayout) findViewById(R.id.close_quote_request_layout);
        closeQuoteRequestButton = (Button)findViewById(R.id.close_quote_request_button);
        messageQouteRequest = (TextView)findViewById(R.id.message_looking_for_quote_request);
        shopNameQuoteRequest = (TextView)findViewById(R.id.shopname);
        shopAddressQuoteRequest = (TextView)findViewById(R.id.shop_adress);
        shopNameLayout = (LinearLayout)findViewById(R.id.quote_request_shopname);
        shopAddressLayout = (LinearLayout)findViewById(R.id.quote_request_shop_adrress);




        shopAddressLayout.setVisibility(View.GONE);
        shopNameLayout.setVisibility(View.GONE);


        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, this.MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, this.MODE_PRIVATE).edit();

        gson = new Gson();
        String jsonToken = shared.getString("USER_TOKEN", "");
        mUserToken = gson.fromJson(jsonToken, Token.class);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        getLocation();





        try {
            checkActiveQuoteRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Boolean send = true;
                int selectedId = radioOperationGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioOperationButton = (RadioButton) findViewById(selectedId);

                operation  = (String) radioOperationButton.getText();

                try {
                    if(String.valueOf(txtAmount.getText()).trim().equals("")){

                        new SweetAlertDialog(MairchentAirtimeQuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("DIOOL")
                                .setContentText("Veuillez inserer le montant")
                                .show();

                        send = false;
                    }
                     if(address == null || address.trim().equals("") ){

                        showDialog();
                         send = false;
                    }
                    else if(latitude == 0.0 || langitude == 0.0){
                        new SweetAlertDialog(MairchentAirtimeQuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("DIOOL")
                                .setContentText("Veuillez choisir une position")
                                .show();
                         send = false;
                    }
                    if (send){
                        startQuoteRequest();
                    }
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
                    txtAmount.setEnabled(true);
                    mGoogleMap.clear();
                    closeQuoteRequest();
                    getLocation();
                    txtAmount.setText("");
                    shopAddressLayout.setVisibility(View.GONE);
                    shopNameLayout.setVisibility(View.GONE);
                    messageQouteRequest.setText("Nous sommes entrain de chercher un merchant");
                    address = null;
                    latitude = 0.0;
                    langitude = 0.0;
                    globalStatic.setWaiteForQuoteResponse(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });





       new Thread(new Runnable() {

            private final String TAG = Waiter.class.getName();
            private long lastUsed;
            private long period;
            private boolean stop = false;

            public synchronized void touch() {
                lastUsed = System.currentTimeMillis();
            }
            public synchronized void forceInterrupt() {

            }

            // soft stopping of thread
            public synchronized void stopTimer() {
                stop = true;

            }


            public synchronized void setPeriod(long period) {
                this.period = period;
            }
            @Override
            public void run() {
                Looper.prepare();

                long idle = 0;
                this.touch();
                do {
                    idle = System.currentTimeMillis() - lastUsed;
                    Log.e(TAG, "Application is idle for " + idle + " ms");
                    try {
                        Thread.sleep(15000); // check every 30 seconds
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Waiter interrupted!");
                    }
                    if (idle > 15000) {
                        idle = 0;
                        // do something here - e.g. call popup or so
                        try {
                           // if(globalStatic.isWaiteForQuoteResponse()){
                                checkActiveQuoteRequest();
                         //   }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.e("Finish","zzz");
                    }
                } while (!stop);
                Log.d(TAG, "Finishing Waiter thread");

            }
        }).start();


        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.action_bar_color)));
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "Quote Request" + "</font>")));

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
                Intent intent = new Intent(MairchentAirtimeQuoteRequestActivity.this,
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
               /* if (MMMUtils.isConnectedTointernet(AddFundsActivity.this))
                    getMerchantBalance();
                return true;*/

            default:
                return true;
        }
    }




    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                                //getLocation();
                                mGoogleMap.setMyLocationEnabled(true);



                                mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                                    @Override
                                    public void onMyLocationChange(Location arg0) {
                                        // TODO Auto-generated method stub
                                        try{
                                            marker.remove();
                                        }catch (Exception e){

                                        }

                                        langitude = arg0.getLatitude();
                                        latitude = arg0.getLongitude();
                                        try {
                                            addresses =  geocoder.getFromLocation(latitude, langitude, 1);
                                            String address_ = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                            String city = addresses.get(0).getLocality();
                                            String state = addresses.get(0).getAdminArea();
                                            String country = addresses.get(0).getCountryName();
                                            String postalCode = addresses.get(0).getPostalCode();
                                            String knownName = addresses.get(0).getFeatureName();
                                            address = address_+","+city+","+state+","+country+","+postalCode+","+knownName;

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title(address));
                                    }
                                });
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }








    private void getLocation(){
        geocoder = new Geocoder(this, Locale.getDefault());

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            latitude= 0.0;
            langitude = 0.0;
            enabled_gps = true;
        }else{
            showGPSDisabledAlertToUser();
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

                enabled_gps = false;
            }
        }


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

                mGoogleMap = googleMap;
                //  mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                if(enabled_gps){
                    mGoogleMap.setMyLocationEnabled(true);
                }


                mGoogleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // TODO Auto-generated method stub
                        try{
                            marker.remove();
                        }catch (Exception e){

                        }

                        langitude = arg0.getLatitude();
                        latitude = arg0.getLongitude();
                        try {
                            addresses =  geocoder.getFromLocation(latitude, langitude, 1);
                            String address_ = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = addresses.get(0).getLocality();
                            String state = addresses.get(0).getAdminArea();
                            String country = addresses.get(0).getCountryName();
                            String postalCode = addresses.get(0).getPostalCode();
                            String knownName = addresses.get(0).getFeatureName();
                            address = address_+","+city+","+state+","+country+","+postalCode+","+knownName;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title(address));
                    }
                });







                mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                            try{
                                marker.remove();
                            }catch (Exception e){

                            }
                            try {
                                System.out.println("latLng.latitude : "+latLng.latitude);
                                System.out.println("latLng.longitude : "+latLng.longitude);
                                latitude = latLng.latitude;
                                langitude = latLng.longitude;
                                addresses =  geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                                if(addresses.size() == 0){
                                   showDialog();


                                }else {
                                    String address_ = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName();
                                    address = address_+","+city+","+state+","+country+","+postalCode+","+knownName;
                                }




                               // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(address));


                        }


                    }
                });




            }
        });

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(4.03688,9.75861), 12));

            }
        });
    }












/////////////////////////////show Dialog ////////////////////////////
    public void showDialog(){
        LayoutInflater li = LayoutInflater.from(MairchentAirtimeQuoteRequestActivity.this);
        View promptsView = li.inflate(R.layout.dialog_add_address, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MairchentAirtimeQuoteRequestActivity.this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogAddressInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                // get user input and set it to result
                                // edit text
                                // result.setText(userInput.getText());
                                address = String.valueOf(userInput.getText());
                                System.out.println("address : "+address);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                address = null;
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }





    /////////////////////////////////////////////web services //////////////////////////////////////////////////////////////////



    private void startQuoteRequest() throws Exception {
        Log.e("aaaa", encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", "")).toString());

        // Personalize View when loading WS
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(MairchentAirtimeQuoteRequestActivity.this, R.style.myDialog))
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
            if(operation.equals("Dépôt")){
                params.put("serviceType", "CASHIN");
            }else{
                params.put("serviceType", "CASHOUT");
            }

            params.put("latitude", latitude);
            params.put("longitude", langitude);
            params.put("address", address);


            Log.e("params CASFIN", params.toString() + "");

            jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    GlobalConstants.URL_QUOTE_REQUEST, new JSONObject(params),
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, response.toString());
                            dialog.dismiss();
                            final TransfertArgentResponse mTransfertArgentResponse = new TransfertArgentResponse().createTransfertObject(response);
                            if(mTransfertArgentResponse.getCode() == 0){
                                activeQuoteRequestLayout.setVisibility(View.GONE);
                                closeQuoteRequestLayout.setVisibility(View.VISIBLE);
                                amountTextView.setText(String.valueOf(txtAmount.getText()));
                                if(operation.equals("Dépôt")){
                                    txtAmount.setEnabled(false);
                                    operationTextView.setText("Dépôt");
                                }else{
                                    operationTextView.setText("Rerait");
                                    txtAmount.setEnabled(false);
                                }
                               // globalStatic.setWaiteForQuoteResponse(true);
                            }else{
                                new SweetAlertDialog(MairchentAirtimeQuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("DIOOL")
                                        .setContentText(mTransfertArgentResponse.getMessage())
                                        .show();
                            }


                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Error: " + error.getMessage());
                    dialog.dismiss();

                    new SweetAlertDialog(MairchentAirtimeQuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
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






    //////////////////check for quote request ///////////////////////////////////




    private void checkActiveQuoteRequest() throws Exception {
        Log.e("aaaa", encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", "")).toString());



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
                     //   dialog.dismiss();

                        final CustomerActiveQuoteRequest mCustomerActiveQuoteRequest;
                        try {
                            mCustomerActiveQuoteRequest = new CustomerActiveQuoteRequest().createCustomerActiveQuoteRequest(response);
                            System.out.println("mCustomerActiveQuoteRequest.getMerchantLatitude() : "+mCustomerActiveQuoteRequest.getMerchantLatitude());
                            if(mCustomerActiveQuoteRequest.getCode() == 0){

                            }
                            if(mCustomerActiveQuoteRequest.getCode() == 0){


                                if(mCustomerActiveQuoteRequest.getId() == null){
                                    closeQuoteRequestLayout.setVisibility(View.GONE);
                                    activeQuoteRequestLayout.setVisibility(View.VISIBLE);
                                }
                                if(mCustomerActiveQuoteRequest.getId() != null){
                                    quouteRequestId = mCustomerActiveQuoteRequest.getId();
                                    amountTextView.setText((mCustomerActiveQuoteRequest.getAmount()).toString());
                                    if(mCustomerActiveQuoteRequest.getServiceType().equals("CASHIN")){
                                        operationTextView.setText("Dépôt");
                                        txtAmount.setEnabled(false);
                                    }else{
                                        operationTextView.setText("Retrait");
                                        txtAmount.setEnabled(false);
                                    }

                                    activeQuoteRequestLayout.setVisibility(View.GONE);
                                    closeQuoteRequestLayout.setVisibility(View.VISIBLE);
                                }

 ////////////////////////////////////////////////// MAP ////////////////////////////////////////////////////

                                if(mCustomerActiveQuoteRequest.getMerchantLatitude() != null){
                                    shopNameLayout.setVisibility(View.VISIBLE);
                                    shopAddressLayout.setVisibility(View.VISIBLE);

                                    shopNameQuoteRequest.setText("Boutique : "+mCustomerActiveQuoteRequest.getMerchantShopname());
                                    shopAddressQuoteRequest.setText("Adresse : "+mCustomerActiveQuoteRequest.getMerchantAddress());
                                    messageQouteRequest.setText("Vous avez une réponse");

                                if(mCustomerActiveQuoteRequest.getMerchantLatitude() != null  && globalStatic.isWaiteForQuoteResponse()){
                                    mapFragment.getMapAsync((new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap googleMap) {
                                            googleMap.addMarker(new MarkerOptions().title(mCustomerActiveQuoteRequest.getMerchantShopname()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)).position(new LatLng(mCustomerActiveQuoteRequest.getMerchantLatitude(),mCustomerActiveQuoteRequest.getMerchantLongitude())));

                                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCustomerActiveQuoteRequest.getMerchantLatitude(),mCustomerActiveQuoteRequest.getMerchantLongitude()), 15));
                                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


                                            googleMap.setMyLocationEnabled(true);



                                        }
                                    }));

                                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                        mapFragment.getMapAsync((new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap googleMap) {
                                                googleMap.addMarker(new MarkerOptions().title(mCustomerActiveQuoteRequest.getAddress()).position(new LatLng(mCustomerActiveQuoteRequest.getLatitude(),mCustomerActiveQuoteRequest.getLongitude())));

                                                // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCustomerActiveQuoteRequest.getMerchantLatitude(),mCustomerActiveQuoteRequest.getMerchantLongitude()), 15));
                                                // googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


                                                googleMap.setMyLocationEnabled(true);



                                            }
                                        }));


                                    }
                                }else{
                                    mapFragment.getMapAsync((new OnMapReadyCallback() {
                                        @Override
                                        public void onMapReady(GoogleMap googleMap) {
                                            googleMap.addMarker(new MarkerOptions().title(mCustomerActiveQuoteRequest.getMerchantShopname()).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)).position(new LatLng(mCustomerActiveQuoteRequest.getMerchantLatitude(),mCustomerActiveQuoteRequest.getMerchantLongitude())));

                                         /*   googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCustomerActiveQuoteRequest.getMerchantLatitude(),mCustomerActiveQuoteRequest.getMerchantLongitude()), 15));
                                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);*/


                                            googleMap.setMyLocationEnabled(true);



                                        }
                                    }));

                                    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                                        mapFragment.getMapAsync((new OnMapReadyCallback() {
                                            @Override
                                            public void onMapReady(GoogleMap googleMap) {
                                                googleMap.addMarker(new MarkerOptions().title(mCustomerActiveQuoteRequest.getAddress()).position(new LatLng(mCustomerActiveQuoteRequest.getLatitude(),mCustomerActiveQuoteRequest.getLongitude())));

                                                // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCustomerActiveQuoteRequest.getMerchantLatitude(),mCustomerActiveQuoteRequest.getMerchantLongitude()), 15));
                                                // googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


                                                googleMap.setMyLocationEnabled(true);



                                            }
                                        }));


                                    }
                                }





                             if(globalStatic.isWaiteForQuoteResponse()){



                              //  globalStatic.setWaiteForQuoteResponse(true);

                           /*     if(mCustomerActiveQuoteRequest.getMerchantLatitude() != null){

                                        shopNameLayout.setVisibility(View.VISIBLE);
                                        shopAddressLayout.setVisibility(View.VISIBLE);

                                        shopNameQuoteRequest.setText("Boutique : "+mCustomerActiveQuoteRequest.getMerchantShopname());
                                        shopAddressQuoteRequest.setText("Adresse : "+mCustomerActiveQuoteRequest.getMerchantAddress());
                                        messageQouteRequest.setText("Vous avez une réponse");
                                      //  globalStatic.setWaiteForQuoteResponse(true);
                                    }*/


                                        new SweetAlertDialog(MairchentAirtimeQuoteRequestActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                                .setTitleText("DIOOL")
                                                .setContentText("Votre demande est acceptée\n" +
                                                        "le shop est "+mCustomerActiveQuoteRequest.getMerchantShopname()+"" +
                                                        "\n l'adresse est "+mCustomerActiveQuoteRequest.getMerchantAddress())
                                                .show();
                                        globalStatic.setWaiteForQuoteResponse(false);
                                    }


                                }else{
                                   // closeQuoteRequestLayout.setVisibility(View.GONE);
                                   // activeQuoteRequestLayout.setVisibility(View.VISIBLE);
                                }
                            }
                            else{
                                closeQuoteRequestLayout.setVisibility(View.GONE);
                                activeQuoteRequestLayout.setVisibility(View.VISIBLE);

                              /*  new SweetAlertDialog(MairchentAirtimeQuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("DIOOL")
                                        .setContentText(mCustomerActiveQuoteRequest.getDeveloperMessage())
                                        .show();*/
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }










                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                //dialog.dismiss();
                try{
                    new SweetAlertDialog(MairchentAirtimeQuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("DIOOL")
                            .setContentText("L'opération a échoué")
                            .show();

                }catch (Exception e){

                }


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
        final ACProgressFlower dialog = new ACProgressFlower.Builder(new ContextThemeWrapper(MairchentAirtimeQuoteRequestActivity.this, R.style.myDialog))
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

                new SweetAlertDialog(MairchentAirtimeQuoteRequestActivity.this, SweetAlertDialog.ERROR_TYPE)
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
