package com.dioolcustomer.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dioolcustomer.R;


public class TrasnfertSoldeFragment extends Fragment {

    View mView;

    public TrasnfertSoldeFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static TrasnfertSoldeFragment newInstance(String param1, String param2) {
        TrasnfertSoldeFragment fragment = new TrasnfertSoldeFragment();
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
        mView= inflater.inflate(R.layout.fragment_trasnfert_solde, container, false);
        return mView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
