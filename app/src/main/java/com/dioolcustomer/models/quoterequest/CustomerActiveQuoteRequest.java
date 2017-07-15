package com.dioolcustomer.models.quoterequest;

import org.json.JSONException;
import org.json.JSONObject;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 31/03/2017.
 */

public class CustomerActiveQuoteRequest {

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
    private String userId;

    @Getter
    @Setter
    private String firstName;

    @Getter
    @Setter
    private String lastName;

    @Getter
    @Setter
    private String provider;

    @Getter
    @Setter
    private Double amount;

    @Getter
    @Setter
    private String merchantUserId;

    @Getter
    @Setter
    private String merchantShopname;

    @Getter
    @Setter
    private String merchantAddress;

    @Getter
    @Setter
    private Double merchantLatitude;

    @Getter
    @Setter
    private Double merchantLongitude;

    @Getter
    @Setter
    private String serviceType;

    @Getter
    @Setter
    private Integer status;

    @Getter
    @Setter
    private Double latitude;

    @Getter
    @Setter
    private Double longitude;

    @Getter
    @Setter
    private String address;


    public CustomerActiveQuoteRequest(){

    }


    public CustomerActiveQuoteRequest createCustomerActiveQuoteRequest(JSONObject mObject) throws JSONException {

        CustomerActiveQuoteRequest mCustomerActiveQuoteRequest = new CustomerActiveQuoteRequest();

        if(mObject.has("code")){
            mCustomerActiveQuoteRequest.setCode(mObject.getInt("code"));
        }
        if(mObject.has("message")){
            mCustomerActiveQuoteRequest.setMessage(mObject.getString("message"));
        }

        if(mObject.has("developerMessage")){
            mCustomerActiveQuoteRequest.setDeveloperMessage(mObject.getString("developerMessage"));
        }
        if(mObject.has("moreInfo")){
            mCustomerActiveQuoteRequest.setMoreInfo(mObject.getString("moreInfo"));
        }
        if(mObject.has("lang")){
            mCustomerActiveQuoteRequest.setLang(mObject.getString("lang"));
        }


        // Parsing Result Object

        if (mObject.has("result")) {
            if(!mObject.getString("result").equals("null")){
                JSONObject mResult = mObject.getJSONObject("result");

                if(mResult.has("id")){
                    mCustomerActiveQuoteRequest.setId(mResult.getInt("id"));
                }
                if(mResult.has("userId")){
                    mCustomerActiveQuoteRequest.setUserId(mResult.getString("userId"));
                }
                if(mResult.has("firstName")){
                    mCustomerActiveQuoteRequest.setFirstName(mResult.getString("firstName"));
                }
                if(mResult.has("lastName")){
                    mCustomerActiveQuoteRequest.setLastName(mResult.getString("lastName"));
                }
                if(mResult.has("provider")){
                    mCustomerActiveQuoteRequest.setProvider(mResult.getString("provider"));
                }
                if(mResult.has("amount")){
                    mCustomerActiveQuoteRequest.setAmount(mResult.getDouble("amount"));
                }
                if(mResult.has("merchantUserId")){
                    mCustomerActiveQuoteRequest.setMerchantUserId(mResult.getString("merchantUserId"));
                }
                if(mResult.has("merchantShopname")){
                    mCustomerActiveQuoteRequest.setMerchantShopname(mResult.getString("merchantShopname"));
                }
                if(mResult.has("merchantAddress")){
                    mCustomerActiveQuoteRequest.setMerchantAddress(mResult.getString("merchantAddress"));
                }
                if(mResult.has("merchantLatitude")){
                    if(!mResult.getString("merchantLatitude").equals("null"))
                       mCustomerActiveQuoteRequest.setMerchantLatitude(mResult.getDouble("merchantLatitude"));
                }
                if(mResult.has("merchantLongitude")){
                    if(!mResult.getString("merchantLongitude").equals("null"))
                       mCustomerActiveQuoteRequest.setMerchantLongitude(mResult.getDouble("merchantLongitude"));
                }
                if(mResult.has("serviceType")){
                    mCustomerActiveQuoteRequest.setServiceType(mResult.getString("serviceType"));
                }
                if(mResult.has("status")){
                    mCustomerActiveQuoteRequest.setStatus(mResult.getInt("status"));
                }
                if(mResult.has("latitude")){
                    mCustomerActiveQuoteRequest.setLatitude(mResult.getDouble("latitude"));
                }
                if(mResult.has("longitude")){
                    mCustomerActiveQuoteRequest.setLongitude(mResult.getDouble("longitude"));
                }
                if(mResult.has("address")){
                    mCustomerActiveQuoteRequest.setAddress(mResult.getString("address"));
                }
            }


        }



        return mCustomerActiveQuoteRequest;
    }

}
