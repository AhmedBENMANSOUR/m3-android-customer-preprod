package com.dioolcustomer.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sihem.messaoui on 23/05/2016.
 */
public class HistoryTransaction {
    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private Integer code;
    @Getter
    @Setter
    private String moreInfo;
    @Getter
    @Setter
    private Integer status;

    @Getter
    @Setter
    private String responseCode;
    @Getter
    @Setter
    private Object timeStamp;

    @Getter
    @Setter
    private String responseMessage;


    @Getter
    @Setter
    private Object mTotal;


    @Getter
    @Setter
    private Object mNombre;


    @Getter
    @Setter
    private Object mLimited;

    @Getter
    @Setter
    private ArrayList<History> mListHistory=new ArrayList<>();


    public HistoryTransaction() {
    }



    public HistoryTransaction createHistory( JSONObject mObject)
    {

        HistoryTransaction mResponse=new HistoryTransaction();
        try {
            if (mObject.has("message"))
                mResponse.setMessage(mObject.getString("message"));

            if (mObject.has("code"))
                mResponse.setCode(mObject.getInt("code"));

            if (mObject.has("moreInfo"))
                mResponse.setMoreInfo(mObject.getString("moreInfo"));

            if (mObject.has("status"))
                mResponse.setStatus(mObject.getInt("status"));




            // Parsing NetworkResult Object
            if (mObject.has("result")) {
                JSONObject mResult = mObject.getJSONObject("result");

                if (mResult.has("limited"))
                    mResponse.setMLimited(mResult.get("limited"));


                if (mResult.has("totalTransfers")) {
                    mResponse.setMTotal(mResult.get("totalTransfers"));
                }


                if (mResult.has("nbTransfers")) {
                    mResponse.setMNombre(mResult.get("nbTransfers"));
                    Log.e("aaaaaaa",mResult.get("nbTransfers") +"s");
                }


                if (mResult.has("responseCode"))
                    mResponse.setResponseCode(mResult.getString("responseCode"));

                if (mResult.has("timeStamp"))
                    mResponse.setTimeStamp(mResult.getString("timeStamp"));

                if (mResult.has("responseMessage"))
                    mResponse.setResponseMessage(mResult.getString("responseMessage"));


                if (mResult.has("transfers"))
                {
                    JSONArray mArray=mResult.getJSONArray("transfers");

                    for(int i=0; i<mArray.length();i++)
                    {
                        History  mHistory=new History();
                        JSONObject mHistoryObject=mArray.getJSONObject(i);
                        if (mHistoryObject.has("id"))
                            mHistory.setId(mHistoryObject.getString("id"));


                        if (mHistoryObject.has("userId"))
                            mHistory.setUserId(mHistoryObject.getString("userId"));


                        if (mHistoryObject.has("date"))
                            mHistory.setDate(mHistoryObject.getString("date"));


                        if (mHistoryObject.has("senderProviderIdentifier"))
                            mHistory.setSenderProviderIdentifier(mHistoryObject.getString("senderProviderIdentifier"));


                        if (mHistoryObject.has("senderServiceType"))
                            mHistory.setSenderServiceType(mHistoryObject.getString("senderServiceType"));


                        if (mHistoryObject.has("senderProviderAccountId"))
                            mHistory.setSenderProviderAccountId(mHistoryObject.getString("senderProviderAccountId"));

                        if (mHistoryObject.has("senderUserIdentifier"))
                            mHistory.setSenderUserIdentifier(mHistoryObject.getString("senderUserIdentifier"));


                        if (mHistoryObject.has("amount"))
                            mHistory.setAmount(mHistoryObject.get("amount"));

                        if (mHistoryObject.has("recipientProviderIdentifier"))
                            mHistory.setRecipientProviderIdentifier(mHistoryObject.getString("recipientProviderIdentifier"));

                        if (mHistoryObject.has("recipientServiceType"))
                            mHistory.setRecipientServiceType(mHistoryObject.getString("recipientServiceType"));


                        if (mHistoryObject.has("recipientProviderAccountId"))
                            mHistory.setRecipientProviderAccountId(mHistoryObject.getString("recipientProviderAccountId"));

                        if (mHistoryObject.has("recipientUserIdentifier"))
                            mHistory.setRecipientUserIdentifier(mHistoryObject.getString("recipientUserIdentifier"));

                        if (mHistoryObject.has("comment"))
                            mHistory.setComment(mHistoryObject.getString("comment"));


                        if (mHistoryObject.has("status"))
                            mHistory.setTransactionStatus(mHistoryObject.getString("status"));

                        if (mHistoryObject.has("details"))
                            mHistory.setDetails(mHistoryObject.getString("details"));


                        if (mHistoryObject.has("extSenderTransactionRef"))
                            mHistory.setExtSenderTransactionRef(mHistoryObject.getString("extSenderTransactionRef"));
                        if (mHistoryObject.has("extSenderTransactionTimestamp"))
                            mHistory.setExtSenderTransactionTimestamp(mHistoryObject.getString("extSenderTransactionTimestamp"));
                        if (mHistoryObject.has("extRecipientTransactionRef"))
                            mHistory.setExtRecipientTransactionRef(mHistoryObject.getString("extRecipientTransactionRef"));
                        if (mHistoryObject.has("extRecipientTransactionTimestamp"))
                            mHistory.setExtRecipientTransactionTimestamp(mHistoryObject.getString("extRecipientTransactionTimestamp"));


                        if (mHistoryObject.has("userRevenue"))
                            mHistory.setUserRevenue(mHistoryObject.get("userRevenue"));


                        if (mHistoryObject.has("recipientUserAlias"))
                            mHistory.setRecipientUserAlias(mHistoryObject.getString("recipientUserAlias"));

                        if (mHistoryObject.has("senderUserAlias"))
                            mHistory.setSenderUserAlias(mHistoryObject.getString("senderUserAlias"));


                        mListHistory.add(mHistory);

                    }
                }

                mResponse.setMListHistory(mListHistory);

            }


        }

        catch (JSONException e) {
            e.printStackTrace();
        }



        return  mResponse;
    }
}
