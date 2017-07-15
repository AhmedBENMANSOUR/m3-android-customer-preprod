package com.dioolcustomer.models;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 21/02/2017.
 */

public class ChildrensEmails {

    @Getter
    @Setter
    private String code;


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

    @Getter
    @Setter
    private String result;


    public ChildrensEmails(){

    }

    public ChildrensEmails createChildrensEmails(JSONObject mObject) throws JSONException{

        ChildrensEmails mResponse  = new ChildrensEmails();

        if(mObject.has("code")){
            mResponse.setCode(mObject.getString("code"));
        }
        if(mObject.has("message")){
            mResponse.setMessage(mObject.getString("message"));
        }
        if(mObject.has("developerMessage")){
            mResponse.setDeveloperMessage(mObject.getString("developerMessage"));
        }
        if(mObject.has("moreInfo")){
            mResponse.setMoreInfo(mObject.getString("moreInfo"));
        }
        if(mObject.has("lang")){
            mResponse.setLang(mObject.getString("lang"));
        }

        //Parsing result Object

        if(mObject.has("result")){
            mResponse.setResult(mObject.getString("result"));
        }


        return mResponse;
    }




}
