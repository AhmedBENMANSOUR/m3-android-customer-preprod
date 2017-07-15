package com.dioolcustomer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dioolcustomer.R;
import com.dioolcustomer.adapters.DetailsNetworkAdapter;
import com.dioolcustomer.models.UserNetwork;

import org.greenrobot.eventbus.EventBus;


public class NetworkDetailsFragment extends Fragment {


    View mView;
    Bundle mBundle;
    TextView mTextName;
    UserNetwork mNetwork=new UserNetwork();
    RecyclerView mRecycleView;
    TextView mEmptyText;


    public NetworkDetailsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static NetworkDetailsFragment newInstance(String param1, String param2) {
        NetworkDetailsFragment fragment = new NetworkDetailsFragment();
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
        mView= inflater.inflate(R.layout.fragment_network_details, container, false);
        mTextName= (TextView) mView.findViewById(R.id.text_details_name);
        mRecycleView= (RecyclerView) mView.findViewById(R.id.recycler_view_networks_details);
        mRecycleView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecycleView.setLayoutManager(mLayoutManager);

        mEmptyText= (TextView) mView.findViewById(R.id.text_empty);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }



        mBundle=getArguments();
        if(mBundle!=null)
        {
            mNetwork=mBundle.getParcelable("NETWORK");
            mTextName.setText(mNetwork.getFirstname()+" "+mNetwork.getLastname());
            if(mNetwork.getMListChildrenRevenu().size()>0) {
                mRecycleView.setAdapter(new DetailsNetworkAdapter(mNetwork, getActivity()));
                mRecycleView.setVisibility(View.VISIBLE);
                mEmptyText.setVisibility(View.GONE);
            }

            else
            {
                mRecycleView.setVisibility(View.GONE);
                mEmptyText.setVisibility(View.VISIBLE);
            }

        }
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
