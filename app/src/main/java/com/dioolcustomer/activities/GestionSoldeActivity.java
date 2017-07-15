package com.dioolcustomer.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.dioolcustomer.MyMoneyMobileApplication;
import com.dioolcustomer.R;
import com.dioolcustomer.fragments.AjoutFondsFragment;
import com.dioolcustomer.fragments.TrasnfertSoldeFragment;
import com.dioolcustomer.utils.MMMUtils;

public class GestionSoldeActivity extends ActionBarActivity {

    RelativeLayout mLayoutTransfert, mLayoutFonds;
    View mViewTransfert, mviewFonds;
    String TAG_TRANSFERT = "FRAGMENT_TRANSFERT";
    String TAG_FONDS = "AJOUT_FONDS";
    String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_solde);

        // Init view with Transfert Fragment
        TAG = TAG_TRANSFERT;
        MMMUtils.addFragment(new TrasnfertSoldeFragment(), null, TrasnfertSoldeFragment.class.getCanonicalName(), true, GestionSoldeActivity.this, R.id.frame_transfert_container);



        mLayoutTransfert = (RelativeLayout) findViewById(R.id.layout_transfert);
        mLayoutFonds = (RelativeLayout) findViewById(R.id.layout_fonds);
        mViewTransfert = findViewById(R.id.view_selector_transfert);
        mviewFonds = findViewById(R.id.view_selector_fonds);


        mLayoutTransfert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TAG.equals(TAG_TRANSFERT))
                {
                    MMMUtils.addFragment(new TrasnfertSoldeFragment(), null, TrasnfertSoldeFragment.class.getCanonicalName(), true, GestionSoldeActivity.this, R.id.frame_transfert_container);
                    mViewTransfert.setVisibility(View.VISIBLE);
                    mviewFonds.setVisibility(View.GONE);
                    TAG = TAG_TRANSFERT;
                }
            }
        });



        mLayoutFonds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TAG.equals(TAG_FONDS))
                {
                    MMMUtils.addFragment(new AjoutFondsFragment(), null, AjoutFondsFragment.class.getCanonicalName(), true, GestionSoldeActivity.this, R.id.frame_transfert_container);
                    mviewFonds.setVisibility(View.VISIBLE);
                    mViewTransfert.setVisibility(View.GONE);
                    TAG = TAG_FONDS;
                }
            }
        });


    }


    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        MyMoneyMobileApplication.getInstance().touch();
    }



}
