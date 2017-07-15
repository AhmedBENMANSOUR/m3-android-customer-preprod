package com.dioolcustomer.models;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 12/01/2017.
 */

public class UserIdByEmail {


    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private String result;


    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String lang;

    @Getter
    @Setter
    private String moreInfo;


    @Getter
    @Setter
    private  String developperMessage;


    public UserIdByEmail() {

    }



    public UserIdByEmail createUserIdByEmail(JSONObject mObject){
        UserIdByEmail mResponse = new UserIdByEmail();
        try{
            if(mObject.has("message")){
                mResponse.setMessage(mObject.getString("message"));
            }
            if(mObject.has("result")){
                mResponse.setResult(mObject.getString("result"));
            }
            if(mObject.has("code")){
                mResponse.setCode(mObject.getString("code"));
            }
            if(mObject.has("lang")){
                mResponse.setLang(mObject.getString("lang"));
            }
            if(mObject.has("moreInfo")){
                mResponse.setMoreInfo(mObject.getString("moreInfo"));
            }
            if(mObject.has("developperMessage")){
                mResponse.setDevelopperMessage(mObject.getString("developperMessage"));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        return  mResponse;

    }
}
