package com.dioolcustomer.models;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 13/01/2017.
 */

public class ShopById {


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
    private String id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String longitude;

    @Getter
    @Setter
    private String latitude;

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private String city;

    @Getter
    @Setter
    private String country;

    @Getter
    @Setter
    private String ownerId;




    public ShopById(){

    }


    @Override
    public String toString() {
        return "ShopById{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", developerMessage='" + developerMessage + '\'' +
                ", moreInfo='" + moreInfo + '\'' +
                ", lang='" + lang + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }

    public ShopById createShopId(JSONObject mObject) throws JSONException {

        ShopById mResponse = new ShopById();

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


            if (mObject.has("result")) {

                    try{
                        JSONObject mResult = mObject.getJSONObject("result");

                        if(mResult.has("id")){
                            mResponse.setId(mResult.getString("id"));
                        }
                        if(mResult.has("name")){
                            mResponse.setName(mResult.getString("name"));
                        }
                        if(mResult.has("longitude")){
                            mResponse.setLongitude(mResult.getString("longitude"));
                        }
                        if(mResult.has("latitude")){
                            mResponse.setLatitude(mResult.getString("latitude"));
                        }
                        if(mResult.has("address")){
                            mResponse.setAddress(mResult.getString("address"));
                        }
                        if(mResult.has("city")){
                            mResponse.setCity(mResult.getString("city"));
                        }
                        if(mResult.has("country")){
                            mResponse.setCountry(mResult.getString("country"));
                        }
                        if(mResult.has("owner_id")){
                            mResponse.setOwnerId(mResult.getString("owner_id"));
                        }
                    }catch (Exception e){
                        e.getMessage();
                    }




            }




        return  mResponse;
    }

    /*public String getName() {
        return name;
    }*/
}
