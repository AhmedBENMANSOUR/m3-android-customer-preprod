package com.dioolcustomer.models;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sihem.messaoui on 19/05/2016.
 */
public class TransfertArgentResponse {

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
    private String developerMessage;
    @Getter
    @Setter
    private String responseCode;
    @Getter
    @Setter
    private Object timeStamp;
    @Getter
    @Setter
    private Object senderFees;
    @Getter
    @Setter
    private Object extSenderTransactionRef;
    @Getter
    @Setter
    private Object senderRevenue;
    @Getter
    @Setter
    private Object totalRedistributableRevenue;
    @Getter
    @Setter
    private Object recipientCurrency;
    @Getter
    @Setter
    private Object senderLoyaltyPoints;
    @Getter
    @Setter
    private Object senderBalance;
    @Getter
    @Setter
    private String responseMessage;

    @Getter
    @Setter
    private Object transferRef;
    @Getter
    @Setter
    private Object recipientBalance;
    @Getter
    @Setter
    private Object currencyExchangeRate;

    @Getter
    @Setter
    private Object recipientTimeStamp;


    @Getter
    @Setter
    private Object userLoyaltyPoints;

    @Getter
    @Setter
    private Object userRevenue;



    @Getter
    @Setter
    private Object userFees;




    /*@Getter
    @Setter
    private Object userNewRevenue;*/


    @Getter
    @Setter
    private Object userOldLoyaltyPoints;

    @Getter
    @Setter
    private Object userNewLoyaltyPoints;


    @Getter
    @Setter
    private Object extSenderTransactionTimestamp;


    @Getter
    @Setter
    private Object extRecipientTransactionRef;

    @Getter
    @Setter
    private Object recipientAmount;


    @Getter
    @Setter
    private Object extRecipientTransactionTimestamp;




    JSONObject mObject;

    public TransfertArgentResponse() {
    }


    public TransfertArgentResponse createTransfertObject(JSONObject mObject) {
        TransfertArgentResponse mResponse = new TransfertArgentResponse();
        try {
            if (mObject.has("message"))
                mResponse.setMessage(mObject.getString("message"));

            if (mObject.has("code"))
                mResponse.setCode(mObject.getInt("code"));

            if (mObject.has("moreInfo"))
                mResponse.setMoreInfo(mObject.getString("moreInfo"));

            if (mObject.has("developerMessage"))
                mResponse.setDeveloperMessage(mObject.getString("developerMessage"));


            // Parsing NetworkResult Object
            if (mObject.has("result")) {
                JSONObject mResult = mObject.getJSONObject("result");

                if (mResult.has("responseCode"))
                    mResponse.setResponseCode(mResult.getString("responseCode"));

                if (mResult.has("timeStamp"))
                    mResponse.setTimeStamp(mResult.getString("timeStamp"));

                if (mResult.has("senderFees"))
                    mResponse.setSenderFees(mResult.getString("senderFees"));

                if (mResult.has("extSenderTransactionRef"))
                    mResponse.setExtSenderTransactionRef(mResult.getString("extSenderTransactionRef"));

                if (mResult.has("senderRevenue"))
                    mResponse.setSenderRevenue(mResult.getString("senderRevenue"));

                if (mResult.has("totalRedistributableRevenue"))
                    mResponse.setTotalRedistributableRevenue(mResult.getString("totalRedistributableRevenue"));

                if (mResult.has("recipientCurrency"))
                    mResponse.setRecipientCurrency(mResult.getString("recipientCurrency"));

                if (mResult.has("senderLoyaltyPoints"))
                    mResponse.setSenderLoyaltyPoints(mResult.getString("senderLoyaltyPoints"));

                if (mResult.has("senderBalance"))
                    mResponse.setSenderBalance(mResult.getString("senderBalance"));

                if (mResult.has("responseMessage"))
                    mResponse.setResponseMessage(mResult.getString("responseMessage"));

                if (mResult.has("extRecipientTransactionRef"))
                    mResponse.setExtRecipientTransactionRef(mResult.getString("extRecipientTransactionRef"));

                if (mResult.has("transferRef"))
                    mResponse.setTransferRef(mResult.getString("transferRef"));

                if (mResult.has("recipientBalance"))
                    mResponse.setRecipientBalance(mResult.getString("recipientBalance"));

                if (mResult.has("currencyExchangeRate"))
                    mResponse.setCurrencyExchangeRate(mResult.getString("currencyExchangeRate"));


                if (mResult.has("recipientAmount"))
                    mResponse.setRecipientAmount(mResult.getString("recipientAmount"));

                if (mResult.has("recipientTimeStamp"))
                    mResponse.setRecipientTimeStamp(mResult.getString("recipientTimeStamp"));


                if (mResult.has("userRevenue"))
                    mResponse.setUserRevenue(mResult.getString("userRevenue"));

               /* if (mResult.has("userNewBalance"))
                    mResponse.setUserNewBalance(mResult.getString("userNewBalance"));*/

                if (mResult.has("userFees"))
                    mResponse.setUserFees(mResult.getString("userFees"));

                if (mResult.has("userLoyaltyPoints"))
                    mResponse.setUserLoyaltyPoints(mResult.getString("userLoyaltyPoints"));


                /*if (mResult.has("userNewRevenue"))
                    mResponse.setUserNewRevenue(mResult.getString("userNewRevenue"));*/

                if (mResult.has("userOldLoyaltyPoints"))
                    mResponse.setUserOldLoyaltyPoints(mResult.getString("userOldLoyaltyPoints"));

                if (mResult.has("userNewLoyaltyPoints"))
                    mResponse.setUserNewLoyaltyPoints(mResult.getString("userNewLoyaltyPoints"));


                if (mResult.has("extSenderTransactionTimestamp"))
                    mResponse.setExtSenderTransactionTimestamp(mResult.getString("extSenderTransactionTimestamp"));



                if (mResult.has("extRecipientTransactionRef"))
                    mResponse.setExtSenderTransactionRef(mResult.getString("extRecipientTransactionRef"));



                if (mResult.has("extRecipientTransactionTimestamp"))
                    mResponse.setRecipientTimeStamp(mResult.getString("extRecipientTransactionTimestamp"));


            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mResponse;
    }
}
