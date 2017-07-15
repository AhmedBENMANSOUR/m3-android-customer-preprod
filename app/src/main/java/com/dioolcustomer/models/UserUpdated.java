package com.dioolcustomer.models;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sihem.messaoui on 26/07/2016.
 */
public class UserUpdated {
    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private Integer code;

    @Getter
    @Setter
    private String updateStatus;

    public UserUpdated() {
    }


    public UserUpdated createUpdatedUser(JSONObject mObject) {

        UserUpdated mResponse = new UserUpdated();
        try {
            if (mObject.has("message"))
                mResponse.setMessage(mObject.getString("message"));

            if (mObject.has("code"))
                mResponse.setCode(mObject.getInt("code"));


           if (mObject.has("result"))
                mResponse.setUpdateStatus(mObject.getJSONObject("result").getString("mailChange"));

        } catch (JSONException e) {
            e.printStackTrace();

        }


        return mResponse;

    }

}
