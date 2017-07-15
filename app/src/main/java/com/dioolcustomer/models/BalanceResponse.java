package com.dioolcustomer.models;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sihem.messaoui on 21/05/2016.
 */
public class BalanceResponse {

    @Getter
    @Setter
    private String message;


    @Getter
    @Setter
    private Object revenueAccountBalance;

    @Getter
    @Setter
    private Object depositAccountBalance;

    @Getter
    @Setter
    private String responseMessage;


    @Getter
    @Setter
    private String succeeded;


    @Getter
    @Setter
    private String responseCode;

    @Getter
    @Setter
    private String timeStamp;

    @Getter
    @Setter
    private Integer code;
    @Getter
    @Setter
    private String moreInfo;


    @Getter
    @Setter
    private Integer status;


    public BalanceResponse() {
    }


    public BalanceResponse createBalanceObject(JSONObject mObject) {

        BalanceResponse mBalanceObject=new BalanceResponse();
        try {
            if (mObject.has("message"))
                mBalanceObject.setMessage(mObject.getString("message"));

            if (mObject.has("code"))
                mBalanceObject.setCode(mObject.getInt("code"));

            if (mObject.has("moreInfo"))
                mBalanceObject.setMoreInfo(mObject.getString("moreInfo"));


            // Parsing Result Object
            if (mObject.has("result")) {
                JSONObject mResult = mObject.getJSONObject("result");

                if (mResult.has("responseCode"))
                    mBalanceObject.setResponseCode(mResult.getString("responseCode"));

                if (mResult.has("timeStamp"))
                    mBalanceObject.setTimeStamp(mResult.getString("timeStamp"));

                if (mResult.has("responseMessage"))
                    mBalanceObject.setResponseMessage(mResult.getString("responseMessage"));


                if (mResult.has("revenueAccountBalance"))
                    mBalanceObject.setRevenueAccountBalance(mResult.get("revenueAccountBalance"));

                if (mResult.has("depositAccountBalance"))
                    mBalanceObject.setDepositAccountBalance(mResult.get("depositAccountBalance"));

            }

            }

        catch (JSONException e) {
            e.printStackTrace();
        }

        return mBalanceObject;

    }
}
