package com.dioolcustomer.models;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sihem.messaoui on 22/05/2016.
 */
public class SendRedeemResponse {
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
    private String responseMessage;

    @Getter
    @Setter
    private String responseCode;

    @Getter
    @Setter
    private Object timeStamp;


    public SendRedeemResponse() {
    }


    public SendRedeemResponse createRedeem(JSONObject mObject)
    {
        SendRedeemResponse mResponse=new SendRedeemResponse();
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


                if (mResult.has("responseCode"))
                    mResponse.setResponseCode(mResult.getString("responseCode"));

                if (mResult.has("timeStamp"))
                    mResponse.setTimeStamp(mResult.getString("timeStamp"));

                if (mResult.has("responseMessage"))
                    mResponse.setResponseMessage(mResult.getString("responseMessage"));


            }


            }

        catch (JSONException e) {
            e.printStackTrace();
        }



        return mResponse;


    }
}
