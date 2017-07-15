package com.dioolcustomer.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.provider.Settings;
import android.content.DialogInterface;

import com.dioolcustomer.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapQuoteRequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapQuoteRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapQuoteRequestFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private MapView mapView;

    private OnFragmentInteractionListener mListener;



    private GoogleMap mMap;

    public MapQuoteRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapQuoteRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapQuoteRequestFragment newInstance(String param1, String param2) {
        MapQuoteRequestFragment fragment = new MapQuoteRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }
        SupportMapFragment mapfrag = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapfrag.getMapAsync(this);
        //mMap.getMapAsync();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map_quote_request, container, false);
        view.findViewById(R.id.map);
        ((SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMapAsync((new OnMapReadyCallback() {
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

                   /* if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {
                            buildGoogleApiClient();
                            googleMap.setMyLocationEnabled(true);
                        }
                    }
                    else {
                        buildGoogleApiClient();
                        googleMap.setMyLocationEnabled(true);
                    }*/

            }
        }));
       // SupportMapFragment mapfrag = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
        //mapfrag.getMapAsync(this);
       /* mapView = (MapView) getView().findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);*/
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       /* if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if(mMap.isMyLocationEnabled()){
                mMap.setMyLocationEnabled(true);
        }
        else {
                showSettingsAlert();
        }
    }



    public void showSettingsAlert(){
            android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
      // Setting Dialog Title<br />
                alertDialog.setTitle("GPS is settings");
       // Setting Dialog Message<br />
                alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
      // On pressing Settings button<br />
                alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
            }
        });
        // on pressing cancel button<br />
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
            }
        });
       // Showing Alert Message<br />
                alertDialog.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
