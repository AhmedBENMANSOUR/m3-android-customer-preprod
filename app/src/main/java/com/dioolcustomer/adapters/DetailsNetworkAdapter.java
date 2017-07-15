package com.dioolcustomer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dioolcustomer.R;
import com.dioolcustomer.models.ChildrenRevenue;
import com.dioolcustomer.models.UserNetwork;

import java.util.ArrayList;

/**
 * Created by sihem.messaoui on 11/08/2016.
 */
public class DetailsNetworkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{


    private Context context;
    UserNetwork mCurrentChild;
    ArrayList<ChildrenRevenue> mList=new ArrayList<>();

    @Override
    public void onClick(View v) {
    }


    public static class RevenuViewHolder extends RecyclerView.ViewHolder {
        TextView mProvider, mTextOperation, mTextParentMargin, mTextChildMargin;
        public RevenuViewHolder(View v) {
            super(v);

            mProvider= (TextView) v.findViewById(R.id.provider);
            mTextOperation=(TextView) v.findViewById(R.id.operation);
            mTextParentMargin=(TextView) v.findViewById(R.id.parent_margin);
            mTextChildMargin= (TextView) v.findViewById(R.id.child_margin);

        }

    }


    public DetailsNetworkAdapter(UserNetwork mChild, Context context) {
        this.context = context;
        this.mList=mChild.getMListChildrenRevenu();
        mCurrentChild=mChild;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_network_details, parent, false);

        vh = new RevenuViewHolder(v);
        vh.setIsRecyclable(false);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((RevenuViewHolder)holder).mProvider.setText(mList.get(position).getProviderName());
        ((RevenuViewHolder)holder).mTextOperation.setText(mList.get(position).getOperationName());
        ((RevenuViewHolder)holder).mTextParentMargin.setText(mList.get(position).getParentMargin());
        ((RevenuViewHolder)holder).mTextChildMargin.setText(mList.get(position).getMargin());


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
