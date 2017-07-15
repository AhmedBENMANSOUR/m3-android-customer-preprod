package com.dioolcustomer.models.quoterequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 14/03/2017.
 */

public class QuoteRequestResponse {

    /*
    *
    * "id": 90,
      "userId": "auth0|58ac2ae8a0810a2e55788b1c",
      "firstName": "SERGE OLIVIER",
      "lastName": "BOUPDA",
      "provider": null,
      "amount": 5000,
      "merchantUserId": null,
      "merchantShopname": null,
      "merchantAddress": null,
      "merchantLatitude": null,
      "merchantLongitude": null,
      "serviceType": "CASHIN",
      "status": 1,
      "latitude": 4.0401487,
      "longitude": 9.730539,
      "address": "Douala, Cameroun"*/

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
    private Double amount;

    @Getter
    @Setter
    private String serviceType;

    @Getter
    @Setter
    private String merchantUserId;

    @Getter
    @Setter
    private String merchantFirstName;

    @Getter
    @Setter
    private String merchantLastName;

    @Getter
    @Setter
    private String merchantShopName;

    @Getter
    @Setter
    private String address;

    @Getter
    @Setter
    private Double longitude;

    @Getter
    @Setter
    private Double lattitude;


    public QuoteRequestResponse(){

    }

    public ArrayList<QuoteRequestResponse> createQuoteRequestResponse(JSONObject mObject) throws JSONException {


       // QuoteRequestResponse mQuoteRequestResponse = new QuoteRequestResponse();
        ArrayList<QuoteRequestResponse> mListQuoteRequest = new ArrayList<>();

        try{
            JSONArray mArray = mObject.getJSONArray("result");


            if(mObject != null){
                for(int i = 0; i < mArray.length(); i++){
                    JSONObject mQuoteRequestObject = mArray.getJSONObject(i);

                    QuoteRequestResponse mQuoteRequestResponse = new QuoteRequestResponse();

                    if(mQuoteRequestObject.has("amount")){
                        if(!mQuoteRequestObject.getString("amount").equals("null"))
                            mQuoteRequestResponse.setAmount(mQuoteRequestObject.getDouble("amount"));
                    }
                    if(mQuoteRequestObject.has("firstName")){
                        mQuoteRequestResponse.setFirstName(mQuoteRequestObject.getString("firstName"));
                    }
                    if(mQuoteRequestObject.has("lastName")){
                        mQuoteRequestResponse.setLastName(mQuoteRequestObject.getString("lastName"));
                    }
                    if(mQuoteRequestObject.has("serviceType")){
                        mQuoteRequestResponse.setServiceType(mQuoteRequestObject.getString("serviceType"));
                    }
                    if(mQuoteRequestObject.has("id")){
                        mQuoteRequestResponse.setId(mQuoteRequestObject.getInt("id"));
                    }

                    mListQuoteRequest.add(mQuoteRequestResponse);

                }

            }


        }catch (Exception e){
            mListQuoteRequest = null;
        }




        return mListQuoteRequest;
    }


}
