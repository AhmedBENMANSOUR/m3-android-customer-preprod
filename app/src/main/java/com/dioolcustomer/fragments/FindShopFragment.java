package com.dioolcustomer.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import com.dioolcustomer.activities.AddFundsActivity;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.ShopsByOwnerId;
import com.dioolcustomer.security.Encryption;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressConstant;
import cc.cloudist.acplibrary.ACProgressFlower;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class FindShopFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, LocationSource,
        LocationListener, ActivityCompat.OnRequestPermissionsResultCallback{




    public String TAG = getClass().getSimpleName();
    Button mBtnSend, cancelButton;
    private SharedPreferences shared;
    SharedPreferences.Editor editor;
    JsonObjectRequest jsonObjReq;
    Gson gson;
    UserProfile mUserProfile;
    Token mUserToken;

    Encryption encryption  = new Encryption();

    GoogleMap mGoogleMap;
    Marker marker;
    Geocoder geocoder;
    SupportMapFragment mapFragment;
    private boolean enabled_gps= false;
    static double latitude;
    static double langitude;
    static String address;
    MarkerOptions markerOptions;
    LatLng latLng;
    MapView mapView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_find_shop, container, false);




        shared = this.getActivity().getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, getActivity().MODE_PRIVATE);
        editor = this.getActivity().getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, getActivity().MODE_PRIVATE).edit();

        gson = new Gson();
        String jsonToken = shared.getString("USER_TOKEN", "");
        mUserToken = gson.fromJson(jsonToken, Token.class);

        try {
            startGetAllShos();
        } catch (Exception e) {
            e.printStackTrace();
        }

       /* SupportMapFragment supportMapFragment = (SupportMapFragment)
                getActivity().getSupportFragmentManager().findFragmentById(R.id.map);*/
        //mapView = (MapView) mView.findViewById(R.id.map);
       // mapView.onCreate(savedInstanceState);

        // Getting a reference to the map
      /*  mapView.getMapAsync((new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
              //  googleMap.addMarker(new MarkerOptions().title("Paris").position(new LatLng(4.047795,9.767451)));

            //    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(4.047795,9.767451), 15));
             //   googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
                            2);
                    return;
                }
                googleMap.setMyLocationEnabled(true);

                googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        // TODO Auto-generated method stub
                        try{
                            marker.remove();
                        }catch (Exception e){

                        }

                        langitude = arg0.getLatitude();
                        latitude = arg0.getLongitude();
                        marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title(address));
                    }
                });


                    }
        }));*/

        /*MapFragment fragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(this);*/
         mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        getLocation();

        // Getting reference to btn_find of the layout activity_main


        // Defining button click event listener for the find button
        View.OnClickListener findClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting reference to EditText to get the user input location


                // Getting user input location



            }
        };

        // Setting button click event listener for the find button



        return mView;
    }






    private void getLocation(){
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
                  //  mGoogleMap.setMyLocationEnabled(true);
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
                      /*  try {
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
                        }*/
                        marker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title(address));
                    }
                });







                mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
                             /*   addresses =  geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
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
                                }*/




                                // mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            } catch (Exception e) {
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









    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);

                                //getLocation();
                            //    mGoogleMap.setMyLocationEnabled(true);



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
                                      /*  try {
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
                                        }*/
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


    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            //  Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                //  addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return addresses;
        }


    }

















    /////////////////////////web services/////////////////////////////


    //////////get all shop

    private void startGetAllShos() throws Exception {

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

        // Sending parameters


        jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                GlobalConstants.URL_GET_ALL_SHOPS, new JSONObject(),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e(TAG, response.toString());
                        dialog.dismiss();
                        final ShopsByOwnerId mShops = new ShopsByOwnerId().createShopsByOwnerId(response);

                        mapFragment.getMapAsync((new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    for(int  i = 0;i < mShops.getMListShop().size(); i++){

                                    String name  = mShops.getMListShop().get(i).getName();
                                        if((mShops.getMListShop().get(i).getLatitude() != null && !mShops.getMListShop().get(i).getLatitude().equals("null"))
                                                && (new Double(mShops.getMListShop().get(i).getLongitude()) != null && !mShops.getMListShop().get(i).getLongitude().equals("null"))){
                                            googleMap.addMarker(new MarkerOptions().title(mShops.getMListShop().get(i).getName()).position(new LatLng(new Double(mShops.getMListShop().get(i).getLatitude()),new Double(mShops.getMListShop().get(i).getLongitude()))).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon)));
                                        }


                                //    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(4.047795,9.767451), 15));
                                  //  googleMap.animateCamera(CameraUpdateFactory.zoomTo(40), 2000, null);

                                    }
                                /*    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        ActivityCompat.requestPermissions(getActivity(),
                                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                                        android.Manifest.permission.ACCESS_FINE_LOCATION},
                                                2);
                                        return;
                                    }
                                    googleMap.setMyLocationEnabled(true);*/


                                }
                            }));


                        System.out.println("il y a une reponse ");





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
                headers.put("Authorization", mUserToken.getType() + " " + mUserToken.getIdToken());
                return headers;
            }


        };

        // Define time out request
        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                GlobalConstants.cat2Timeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        /// Adding request to request queue
        MyMoneyMobileApplication.getInstance().addToRequestQueue(jsonObjReq, AddFundsActivity.class.getCanonicalName());
    }

}
