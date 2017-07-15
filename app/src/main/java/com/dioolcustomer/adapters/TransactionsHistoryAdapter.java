package com.dioolcustomer.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.google.gson.Gson;
import com.dioolcustomer.R;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.models.History;
import com.dioolcustomer.security.Encryption;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by mypc on 04/05/2016.
 */
public class TransactionsHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<History> mListHistory;
    private Context context;

    private SharedPreferences shared;
    Gson gson;
    UserProfile mUserProfile;
    Token mUserToken;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    Encryption encryption  = new Encryption();

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView textViewReceiver;
        public ImageView imageViewTransaction;
        public ImageView imageViewProvider;
        public TextView textViewAmount;
        public TextView textViewTime;
        public TextView textViewDDMM;
        public TextView textViewYear;
        public ImageView mImageDetails;
        public LinearLayout mLayoutDetails, mLayoutInterne, mLayoutExterne;
        public TextView mRefInterne, mRefExterne, mTextRevenue, mTextFees,mtextType;
        public CardView mMainLayout;

        public TransactionViewHolder(View v) {
            super(v);
            textViewReceiver = (TextView) v.findViewById(R.id.texView_transaction);
            imageViewTransaction = (ImageView) v.findViewById(R.id.imageViewTransactionIcon);
            imageViewProvider = (ImageView) v.findViewById(R.id.imageViewProviderIcon);
            mImageDetails = (ImageView) v.findViewById(R.id.image_details);
            textViewAmount = (TextView) v.findViewById(R.id.texView_amount);
            textViewTime = (TextView) v.findViewById(R.id.texView_time);
            textViewDDMM = (TextView) v.findViewById(R.id.textView_dd_mm);
            textViewYear = (TextView) v.findViewById(R.id.textView_year);
            mtextType= (TextView) v.findViewById(R.id.texView_type_interne);
            mLayoutDetails = (LinearLayout) v.findViewById(R.id.layout_bas);
            mLayoutInterne = (LinearLayout) v.findViewById(R.id.layout_interne);
            mLayoutExterne = (LinearLayout) v.findViewById(R.id.layout_externe);
            mRefInterne = (TextView) v.findViewById(R.id.texte_ref_interne);
            mRefExterne = (TextView) v.findViewById(R.id.texte_ref_externe);
            mMainLayout = (CardView) v.findViewById(R.id.card_view);

            mTextRevenue = (TextView) v.findViewById(R.id.texView_revenue);
            mTextFees = (TextView) v.findViewById(R.id.texView_fees);
        }
    }


    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TransactionsHistoryAdapter(ArrayList<History> mListHistory, Context context) throws Exception {
        this.mListHistory = mListHistory;
        this.context = context;
        //Collections.sort(mListHistory, new DateComparator());


        //Prepare Preferences
        shared = context.getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, context.MODE_PRIVATE);
        gson = new Gson();
        String jsonToken = shared.getString("USER_TOKEN", "");
        mUserToken = gson.fromJson(jsonToken, Token.class);
        String jsonProfile = encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_PROFILE", ""));
        mUserProfile = new Gson().fromJson(jsonProfile, UserProfile.class);


    }

    @Override
    public int getItemViewType(int position) {
        return mListHistory.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }


    public class DateComparator implements Comparator<History> {
        @Override
        public int compare(History o1, History o2) {
            return o2.getDate().compareTo(o1.getDate());
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.transactions_history_item, parent, false);

            vh = new TransactionViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_loading, parent, false);

            vh = new ProgressViewHolder(v);
        }
        vh.setIsRecyclable(false);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        if (holder instanceof TransactionViewHolder) {
            String nProviderId = mListHistory.get(position).getRecipientProviderAccountId();
            String nSenderId = mListHistory.get(position).getSenderProviderAccountId();

            // set User revenue
            if (String.valueOf(mListHistory.get(position).getUserRevenue()).equals(null)) {
                ((TransactionViewHolder) holder).mTextRevenue.setVisibility(View.VISIBLE);
                ((TransactionViewHolder) holder).mTextRevenue.setText("Revenue: " + String.valueOf(mListHistory.get(position).getUserRevenue()));
            }

            if ((!nProviderId.equals("null")) && !(nProviderId.equals(""))) {
               /* if (nProviderId.length() > 3)
                    ((TransactionViewHolder) holder).textViewReceiver.setText(nProviderId.substring(3, nProviderId.length()));
                else*/
                ((TransactionViewHolder) holder).textViewReceiver.setText(nProviderId);
            } else if ((!nSenderId.equals("null")) && !(nSenderId.equals(""))) {
               /* if (nSenderId.length() > 3)
                    ((TransactionViewHolder) holder).textViewReceiver.setText(nSenderId.substring(3, nSenderId.length() ));
                else*/
                ((TransactionViewHolder) holder).textViewReceiver.setText(nSenderId);

            } else if ((!mListHistory.get(position).getRecipientUserIdentifier().equals("null")) && !(mListHistory.get(position).getRecipientUserIdentifier().equals(""))) {
               ((TransactionViewHolder) holder).textViewReceiver.setText(mListHistory.get(position).getRecipientUserAlias());
            } else if ((!mListHistory.get(position).getSenderUserIdentifier().equals("null")) && !(mListHistory.get(position).getSenderUserIdentifier().equals(""))) {
               ((TransactionViewHolder) holder).textViewReceiver.setText(mListHistory.get(position).getSenderUserAlias());
            }


            switch (mListHistory.get(position).getRecipientServiceType().toString()) {
                case "AIRTIME": {

                    // init Référence
                   if ((mListHistory.get(position).getExtRecipientTransactionRef().equals("null")) || (mListHistory.get(position).getExtRecipientTransactionRef().equals(""))) {
                        ((TransactionViewHolder) holder).mLayoutExterne.setVisibility(View.GONE);
                    } else {
                        ((TransactionViewHolder) holder).mRefExterne.setText(mListHistory.get(position).getExtRecipientTransactionRef());
                        ((TransactionViewHolder) holder).mLayoutExterne.setVisibility(View.VISIBLE);
                    }

                    ((TransactionViewHolder) holder).imageViewTransaction.setBackground(context.getResources().getDrawable(R.drawable.airtime));
                   if (mListHistory.get(position).getRecipientProviderIdentifier().equals(GlobalConstants.OCM_VALUE))
                        ((TransactionViewHolder) holder).imageViewProvider.setBackground(context.getResources().getDrawable(R.drawable.orange_active));

                    else if (mListHistory.get(position).getRecipientProviderIdentifier().equals(GlobalConstants.MTNC_VALUES))
                        ((TransactionViewHolder) holder).imageViewProvider.setBackground(context.getResources().getDrawable(R.drawable.mtn_active));

                    else if (mListHistory.get(position).getRecipientProviderIdentifier().equals(GlobalConstants.NXTL_VALUE))
                        ((TransactionViewHolder) holder).imageViewProvider.setBackground(context.getResources().getDrawable(R.drawable.nxtl1));
                }
                break;

                case "MOBILE_MONEY": {
                    System.out.println("mListHistory.get(position).getSenderUserIdentifier() : "+mListHistory.get(position).getSenderUserIdentifier());
                    System.out.println("mListHistory.get(position).getRecipientUserAlias() : "+mListHistory.get(position).getRecipientUserAlias());

                    if((mListHistory.get(position).getSenderUserAlias() != null) && (!mListHistory.get(position).getSenderUserAlias().equals(""))
                            && (mListHistory.get(position).getRecipientUserAlias().equals("")) || (mListHistory.get(position).getRecipientUserAlias().equals("null")) || (mListHistory.get(position).getRecipientUserAlias() == null)){
                        if ((mListHistory.get(position).getExtRecipientTransactionRef().equals("null")) || (mListHistory.get(position).getExtRecipientTransactionRef().equals(""))) {
                            ((TransactionViewHolder) holder).mLayoutExterne.setVisibility(View.GONE);

                        } else {
                            ((TransactionViewHolder) holder).mLayoutExterne.setVisibility(View.VISIBLE);
                            ((TransactionViewHolder) holder).mRefExterne.setText(mListHistory.get(position).getExtRecipientTransactionRef());
                        }

                        ((TransactionViewHolder) holder).imageViewTransaction.setBackground(context.getResources().getDrawable(R.drawable.cashin));
                    } else try {
                        if ((mListHistory.get(position).getRecipientUserIdentifier().equals(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_EMAIL_MERCHANT", "")).toString())) || (mListHistory.get(position).getRecipientUserIdentifier().equals(encryption.decrypt(mUserToken.getIdToken(),encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_NAME_MERCHANT", ""))).toString()))) {

                            // init Référence
                            if ((mListHistory.get(position).getExtSenderTransactionRef().equals("null")) || (mListHistory.get(position).getExtSenderTransactionRef().equals(""))) {
                                ((TransactionViewHolder) holder).mLayoutExterne.setVisibility(View.GONE);
                            } else {
                                ((TransactionViewHolder) holder).mRefExterne.setText(mListHistory.get(position).getExtSenderTransactionRef());
                                ((TransactionViewHolder) holder).mLayoutExterne.setVisibility(View.VISIBLE);
                            }


                            ((TransactionViewHolder) holder).imageViewTransaction.setBackground(context.getResources().getDrawable(R.drawable.cashout));
                        } else {
                            ((TransactionViewHolder) holder).imageViewTransaction.setBackground(context.getResources().getDrawable(R.drawable.cashin));
                            if ((mListHistory.get(position).getExtRecipientTransactionRef().equals("null")) || (mListHistory.get(position).getExtRecipientTransactionRef().equals(""))) {
                                ((TransactionViewHolder) holder).mLayoutExterne.setVisibility(View.GONE);

                            } else {
                                ((TransactionViewHolder) holder).mLayoutExterne.setVisibility(View.VISIBLE);
                                ((TransactionViewHolder) holder).mRefExterne.setText(mListHistory.get(position).getExtRecipientTransactionRef());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Operator icons
                    if ((mListHistory.get(position).getSenderProviderIdentifier().equals(GlobalConstants.OCM_VALUE)) || (mListHistory.get(position).getRecipientProviderIdentifier().equals(GlobalConstants.OCM_VALUE)))
                        ((TransactionViewHolder) holder).imageViewProvider.setBackground(context.getResources().getDrawable(R.drawable.orange1));

                    else if ((mListHistory.get(position).getSenderProviderIdentifier().equals(GlobalConstants.MTNC_VALUES)) || (mListHistory.get(position).getRecipientProviderIdentifier().equals(GlobalConstants.MTNC_VALUES)))
                        ((TransactionViewHolder) holder).imageViewProvider.setBackground(context.getResources().getDrawable(R.drawable.mtn1));

                    else if ((mListHistory.get(position).getSenderProviderIdentifier().equals(GlobalConstants.NXTL_VALUE)) || (mListHistory.get(position).getRecipientProviderIdentifier().equals(GlobalConstants.NXTL_VALUE)))
                        ((TransactionViewHolder) holder).imageViewProvider.setBackground(context.getResources().getDrawable(R.drawable.nxtl1));


                    else {
                        ((TransactionViewHolder) holder).imageViewProvider.setBackground(context.getResources().getDrawable(R.drawable.logo_app));
                        try {
                            if(mListHistory.get(position).getSenderProviderIdentifier().equals("DIOOL") && mListHistory.get(position).getRecipientProviderIdentifier().equals("DIOOL")){
                                try {
                                    if(!mListHistory.get(position).getRecipientUserAlias().equals("") && mListHistory.get(position).getRecipientUserAlias() != null
                                            && mListHistory.get(position).getRecipientUserIdentifier().equals(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID", "")))){
                                        ((TransactionViewHolder) holder).mtextType.setText("Transfert Interne Entrant");
                                        ((TransactionViewHolder) holder).textViewReceiver.setText(mListHistory.get(position).getSenderUserAlias());

                                    }
                                    else if(!mListHistory.get(position).getSenderUserAlias().equals("") && mListHistory.get(position).getSenderUserAlias() != null
                                            && mListHistory.get(position).getSenderUserIdentifier().equals(encryption.decrypt(mUserToken.getIdToken(),shared.getString("USER_ID", "")))){
                                        ((TransactionViewHolder) holder).mtextType.setText("Transfert Interne Sortant");
                                        ((TransactionViewHolder) holder).textViewReceiver.setText(mListHistory.get(position).getRecipientUserAlias());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                break;

            }


            String timeString = "";
            String HourString = "";
            String monthString = "";
            String dateString = "";
            String dayString = "";

           String textAmount = mListHistory.get(position).getAmount().toString();
            ((TransactionViewHolder) holder).textViewAmount.setText(Html.fromHtml("<b>" + textAmount + "</b> " + context.getString(R.string.currency_name)));

            DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(mListHistory.get(position).getDate());

            if (Integer.parseInt(String.valueOf(dateTime.getHourOfDay())) < 10)
                HourString = "0" + dateTime.getHourOfDay();
            else
                HourString = dateTime.getHourOfDay() + "";


            //Hours and Minutes
           if (Integer.parseInt(String.valueOf(dateTime.getMinuteOfHour())) < 10)
                timeString = HourString + ":0" + dateTime.getMinuteOfHour();
            else
                timeString = HourString + ":" + dateTime.getMinuteOfHour();

            //Days
            if (Integer.parseInt(String.valueOf(dateTime.getDayOfMonth())) < 10)
                dayString = "0" + dateTime.getDayOfMonth();
            else
                dayString = dateTime.getDayOfMonth() + "";

            //Months
            if (Integer.parseInt(String.valueOf(dateTime.getMonthOfYear())) < 10)
                monthString = "0" + dateTime.getMonthOfYear();
            else
                monthString = dateTime.getMonthOfYear() + "";


           dateString = dayString + "/" + monthString;

            ((TransactionViewHolder) holder).textViewDDMM.setText(dateString);
            ((TransactionViewHolder) holder).textViewTime.setText(timeString);
            ((TransactionViewHolder) holder).textViewYear.setText(dateTime.getYear() + "");

            // Init Réferences
            ((TransactionViewHolder) holder).mRefInterne.setText(mListHistory.get(position).getId());


        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }


        ((TransactionViewHolder) holder).mMainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TransactionViewHolder) holder).mLayoutDetails.getVisibility() == View.GONE) {
                    ((TransactionViewHolder) holder).mLayoutDetails.setVisibility(View.VISIBLE);
                    ((TransactionViewHolder) holder).mImageDetails.setImageResource(R.drawable.collapse);
                } else {
                    ((TransactionViewHolder) holder).mLayoutDetails.setVisibility(View.GONE);
                    ((TransactionViewHolder) holder).mImageDetails.setImageResource(R.drawable.expand);
                }
            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mListHistory.size();
    }


}
