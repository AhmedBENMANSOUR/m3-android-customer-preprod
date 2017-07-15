package com.dioolcustomer.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 28/04/2017.
 */

public class ShopsByOwnerId {


    @Getter
    @Setter
    private Integer code;

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
    private Integer id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String shopType;

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
    private String owner_id;

    @Getter
    @Setter
    private ArrayList<ShopById> mListShop=new ArrayList<>();





    public ShopsByOwnerId(){

    }


    public ShopsByOwnerId createShopsByOwnerId(JSONObject mObject){

        ShopsByOwnerId mResponse = new ShopsByOwnerId();


        try {
            if (mObject.has("message"))
                mResponse.setMessage(mObject.getString("message"));

            if (mObject.has("code"))
                mResponse.setCode(mObject.getInt("code"));

            if (mObject.has("moreInfo"))
                mResponse.setMoreInfo(mObject.getString("moreInfo"));

            if (mObject.has("developerMessage"))
                mResponse.setDeveloperMessage(mObject.getString("developerMessage"));

            if (mObject.has("lang"))
                mResponse.setLang(mObject.getString("lang"));

            if (mObject.has("result")) {
                JSONArray mResult = mObject.getJSONArray("result");

                for (int i = 0; i < mResult.length(); i++){
                    ShopById mShop = new ShopById();
                    JSONObject mShopObject=mResult.getJSONObject(i);

                    if(mShopObject.has("id")){
                        mShop.setId(mShopObject.getString("id"));
                    }
                    if(mShopObject.has("name")){
                        mShop.setName(mShopObject.getString("name"));
                    }

                    if(mShopObject.has("longitude")){
                        mShop.setLongitude(mShopObject.getString("longitude"));
                    }
                    if(mShopObject.has("latitude")){
                        mShop.setLatitude(mShopObject.getString("latitude"));
                    }
                    if(mShopObject.has("address")){
                        mShop.setAddress(mShopObject.getString("address"));
                    }
                    if(mShopObject.has("city")){
                        mShop.setCity(mShopObject.getString("city"));
                    }
                    if(mShopObject.has("country")){
                        mShop.setCountry(mShopObject.getString("country"));
                    }
                    if(mShopObject.has("owner_id")){
                        mShop.setOwnerId(mShopObject.getString("owner_id"));
                    }

                  mListShop.add(mShop);
                }
            }

          mResponse.setMListShop(mListShop);

        } catch (JSONException e) {
            e.printStackTrace();
        }


            return mResponse;
    }
}
