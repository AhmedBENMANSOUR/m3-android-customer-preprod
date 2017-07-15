package com.dioolcustomer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dioolcustomer.R;
import com.dioolcustomer.models.UserNetwork;

import java.util.ArrayList;

/**
 * Created by sihem.messaoui on 11/08/2016.
 */
public class UserNerworkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public  static UserNetwork mCurrentNewtork;
    private Context context;
    ArrayList<UserNetwork> mList=new ArrayList<>();


    public static class NetworkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mImageUser;
        TextView mTextName, mTextProfile, mTextFistName, mTextLastName, mTextDeposit,mTextRevenu;
        public NetworkViewHolder(View v) {
            super(v);

            v.setOnClickListener(this);
            mImageUser= (ImageView) v.findViewById(R.id.image_user);
            mTextName= (TextView) v.findViewById(R.id.user_name);
            mTextFistName=(TextView) v.findViewById(R.id.firstname);
            mTextLastName=(TextView) v.findViewById(R.id.lastname);
            mTextProfile= (TextView) v.findViewById(R.id.user_profile);

            mTextDeposit=(TextView) v.findViewById(R.id.deposit);
            mTextRevenu= (TextView) v.findViewById(R.id.revenu);
        }

        @Override
        public void onClick(View v) {
            //if(mCurrentNewtork!=null)
            //EventBus.getDefault().post(new OpenNetworkDetails(mCurrentNewtork));
        }
    }


    public UserNerworkAdapter(ArrayList<UserNetwork> mList, Context context) {
        this.context = context;
        this.mList=mList;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_user_network, parent, false);

        vh = new NetworkViewHolder(v);
        vh.setIsRecyclable(false);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        mCurrentNewtork=mList.get(position);
        ((NetworkViewHolder)holder).mTextName.setText(mList.get(position).getUsername());
        ((NetworkViewHolder)holder).mTextProfile.setText(mList.get(position).getProfile());
        ((NetworkViewHolder)holder).mTextFistName.setText(mList.get(position).getFirstname());
        ((NetworkViewHolder)holder).mTextLastName.setText(mList.get(position).getLastname());

        ((NetworkViewHolder)holder).mTextDeposit.setText(mList.get(position).getDaCredit());
        ((NetworkViewHolder)holder).mTextRevenu.setText(mList.get(position).getCrCredit());


        Log.e("eee", mList.get(position)+"");
        Glide.with(context).load(mList.get(position).getPicture()).into(((NetworkViewHolder)holder).mImageUser);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
