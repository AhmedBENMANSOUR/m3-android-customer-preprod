package com.dioolcustomer.models;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 28/02/2017.
 */

public class MobileAppVersion {



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


        public MobileAppVersion() {

        }

        public MobileAppVersion createMobileAppVersion(JSONObject mObject) throws JSONException {

            MobileAppVersion mResponse = new MobileAppVersion();

            if (mObject.has("code")) {
                mResponse.setCode(mObject.getString("code"));
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

            if (mObject.has("result")) {
                mResponse.setResult(mObject.getString("result"));
            }


            return mResponse;
        }
    }

