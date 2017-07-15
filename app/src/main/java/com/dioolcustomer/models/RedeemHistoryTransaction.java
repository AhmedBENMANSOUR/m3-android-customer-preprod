package com.dioolcustomer.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sihem.messaoui on 19/07/2016.
 */
public class RedeemHistoryTransaction {
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
    private String responseCode;
    @Getter
    @Setter
    private Object timeStamp;

    @Getter
    @Setter
    private String responseMessage;


    @Getter
    @Setter
    private Object totalRedeems;


    @Getter
    @Setter
    private Object nbRedeems;

    @Getter
    @Setter
    private Object mLimited;


    @Getter
    @Setter
    private ArrayList<Redeem> mListHistory = new ArrayList<>();


    public RedeemHistoryTransaction createRedeemHistory(JSONObject mObject) {

        RedeemHistoryTransaction mResponse = new RedeemHistoryTransaction();
        try {
            if (mObject.has("message"))
                mResponse.setMessage(mObject.getString("message"));

            if (mObject.has("code"))
                mResponse.setCode(mObject.getInt("code"));

            if (mObject.has("moreInfo"))
                mResponse.setMoreInfo(mObject.getString("moreInfo"));


            // Parsing NetworkResult Object
            if (mObject.has("result")) {
                JSONObject mResult = mObject.getJSONObject("result");

                if (mResult.has("limited"))
                    mResponse.setMLimited(mResult.get("limited"));

                if (mResult.has("totalRedeems"))
                    mResponse.setTotalRedeems(mResult.get("totalRedeems"));

                if (mResult.has("nbRedeems"))
                    mResponse.setNbRedeems(mResult.get("nbRedeems"));

                if (mResult.has("responseCode"))
                    mResponse.setResponseCode(mResult.getString("responseCode"));

                if (mResult.has("timeStamp"))
                    mResponse.setTimeStamp(mResult.getString("timeStamp"));

                if (mResult.has("responseMessage"))
                    mResponse.setResponseMessage(mResult.getString("responseMessage"));


                if (mResult.has("redeems")) {
                    JSONArray mArray = mResult.getJSONArray("redeems");


                    for (int i = 0; i < mArray.length(); i++) {



                        Redeem mRedeem = new Redeem();
                        JSONObject mHistoryObject = mArray.getJSONObject(i);


                        if (mHistoryObject.has("amount"))
                            mRedeem.setAmount(mHistoryObject.get("amount"));




                        if (mHistoryObject.has("date"))
                            mRedeem.setMDateRedeem(mHistoryObject.get("date"));

                        mListHistory.add(mRedeem);

                    }

                }

                mResponse.setMListHistory(mListHistory);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        return mResponse;
    }

}
