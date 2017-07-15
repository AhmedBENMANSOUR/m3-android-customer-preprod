package com.dioolcustomer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dioolcustomer.R;
import com.dioolcustomer.models.Redeem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.TimeZone;

/**
 * Created by sihem.messaoui on 19/07/2016.
 */
public class RedeemHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Redeem> mListHistory;
    private Context context;
    SimpleDateFormat sdf;

    public static class RedeemViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView textViewAmout, textViewDate;

        public RedeemViewHolder(View v) {
            super(v);
            textViewAmout = (TextView) v.findViewById(R.id.texView_amout_redeem);
            textViewDate = (TextView) v.findViewById(R.id.texView_date_redeem);
        }
    }


    public RedeemHistoryAdapter(ArrayList<Redeem> mListHistory, Context context) {
        this.mListHistory = mListHistory;
        this.context = context;

        sdf = new SimpleDateFormat("dd-MM-yyyy 'à' HH:mm:ss "); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // give a timezone reference for formating (see comment at the bottom

    }

    public class DateComparator implements Comparator<Redeem> {
        @Override
        public int compare(Redeem o1, Redeem o2) {
            return o2.getMDateRedeem().toString().compareTo(o1.getMDateRedeem().toString());
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.redeem_history_item, parent, false);

        vh = new RedeemViewHolder(v);
        vh.setIsRecyclable(false);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RedeemViewHolder) holder).textViewAmout.setText(mListHistory.get(position).getAmount().toString()+" XAF");


        long unixSeconds = Long.parseLong(mListHistory.get(position).getMDateRedeem().toString());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(unixSeconds);

        String formattedDate = sdf.format(calendar.getTime());
        ((RedeemViewHolder) holder).textViewDate.setText(formattedDate);



    }




    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mListHistory.size();
    }

}
