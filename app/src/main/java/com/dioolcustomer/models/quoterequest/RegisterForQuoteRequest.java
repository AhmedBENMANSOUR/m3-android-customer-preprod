package com.dioolcustomer.models.quoterequest;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 26/04/2017.
 */

public class RegisterForQuoteRequest {


    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private String developerMessage;

    @Getter
    @Setter
    private String moreInfo;

    @Getter
    @Setter
    private String lang;


    public RegisterForQuoteRequest() {

    }


    public RegisterForQuoteRequest createRegisterForQuoteRequest(JSONObject mObject) throws JSONException {

        RegisterForQuoteRequest mResponse = new RegisterForQuoteRequest();

        if (mObject.has("code")) {
            mResponse.setCode(mObject.getInt("code"));
        }
        if (mObject.has("message")) {
            mResponse.setMessage(mObject.getString("message"));
        }
        if (mObject.has("developerMessage")) {
            mResponse.setDeveloperMessage(mObject.getString("developerMessage"));
        }
        if (mObject.has("moreInfo")) {
            mResponse.setMoreInfo(mObject.getString("moreInfo"));
        }
        if (mObject.has("lang")) {
            mResponse.setLang(mObject.getString("lang"));
        }

        //Parsing result Object

      /*  if (mObject.has("result")) {
            mResponse.setResult(mObject.getString("result"));
        }*/


        return mResponse;

    }
}
