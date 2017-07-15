package com.dioolcustomer.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sihem.messaoui on 14/08/2016.
 */
public class UserNetworkResponse {
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
    private ArrayList<UserNetwork> mUsers=new ArrayList<>();





    public UserNetworkResponse createNetworks(JSONObject mObject) {

        UserNetworkResponse mResponse = new UserNetworkResponse();
        try {
            if (mObject.has("message"))
                mResponse.setMessage(mObject.getString("message"));

            if (mObject.has("code"))
                mResponse.setCode(mObject.getInt("code"));

            if (mObject.has("moreInfo"))
                mResponse.setMoreInfo(mObject.getString("moreInfo"));

            // Parsing NetworkResult Object
            if (mObject.has("result")) {
                JSONObject mResult = mObject.getJSONObject("result");
                ArrayList<UserNetwork>mList=new ArrayList<>();
                if (mResult.has("users")) {
                    JSONArray mArray = mResult.getJSONArray("users");
                    Log.e("USERS", mArray.length()+"");
                    for (int i = 0; i < mArray.length(); i++) {
                        UserNetwork mNetwork = new UserNetwork();


                        JSONObject mNetworkObject = mArray.getJSONObject(i);
                        if (mNetworkObject.has("id"))
                            mNetwork.setId(mNetworkObject.getString("id"));

                        if (mNetworkObject.has("userId"))
                            mNetwork.setUserId(mNetworkObject.getString("userId"));


                        if (mNetworkObject.has("profile"))
                            mNetwork.setProfile(mNetworkObject.getString("profile"));


                        if (mNetworkObject.has("name"))
                            mNetwork.setName(mNetworkObject.getString("name"));

                        if (mNetworkObject.has("username"))
                            mNetwork.setUsername(mNetworkObject.getString("username"));


                        if (mNetworkObject.has("email"))
                            mNetwork.setEmail(mNetworkObject.getString("email"));
                        if (mNetworkObject.has("picture"))
                            mNetwork.setPicture(mNetworkObject.getString("picture"));
                        if (mNetworkObject.has("firstname"))
                            mNetwork.setFirstname(mNetworkObject.getString("firstname"));
                        if (mNetworkObject.has("lastname"))
                            mNetwork.setLastname(mNetworkObject.getString("lastname"));
                        if (mNetworkObject.has("phone"))
                            mNetwork.setPhone(mNetworkObject.getString("phone"));
                        if (mNetworkObject.has("address"))
                            mNetwork.setAdress(mNetworkObject.getString("address"));
                        if (mNetworkObject.has("businessName"))
                            mNetwork.setBusinessName(mNetworkObject.getString("businessName"));
                        if (mNetworkObject.has("daCredit"))
                            mNetwork.setDaCredit(mNetworkObject.getString("daCredit"));
                        if (mNetworkObject.has("crCredit"))
                            mNetwork.setCrCredit(mNetworkObject.getString("crCredit"));


                        if (mNetworkObject.has("parents")) {
                            JSONArray mParentsArray = mNetworkObject.getJSONArray("parents");
                            ArrayList<String> mListParents = new ArrayList<>();
                            for (int j = 0; j < mParentsArray.length(); j++) {
                                mListParents.add(mParentsArray.getString(j));
                            }

                            mNetwork.setMListParents(mListParents);

                        }

                        if (mNetworkObject.has("children"))
                            mNetwork.setChildrens(mNetworkObject.getString("children"));

                        if (mNetworkObject.has("childrenRevenueDistribution")) {
                            JSONArray mRevenueArray = mNetworkObject.getJSONArray("childrenRevenueDistribution");
                            ArrayList<ChildrenRevenue> mListRevenue = new ArrayList<>();
                            for (int y = 0; y < mRevenueArray.length(); y++) {
                                ChildrenRevenue mRevenue = new ChildrenRevenue();
                                JSONObject mRevenueObject = mRevenueArray.getJSONObject(y);


                                if(mRevenueObject.has("userId"))
                                    mRevenue.setUserId(mRevenueObject.getString("userId"));

                                if(mRevenueObject.has("childUserId"))
                                    mRevenue.setChildUserId(mRevenueObject.getString("childUserId"));
                                if(mRevenueObject.has("nostroOperationId"))
                                    mRevenue.setNostroOperationId(mRevenueObject.getString("nostroOperationId"));
                                if(mRevenueObject.has("providerName"))
                                    mRevenue.setProviderName(mRevenueObject.getString("providerName"));
                                if(mRevenueObject.has("operationName"))
                                    mRevenue.setOperationName(mRevenueObject.getString("operationName"));
                                if(mRevenueObject.has("margin"))
                                    mRevenue.setMargin(mRevenueObject.getString("margin"));
                                if(mRevenueObject.has("parentMargin"))
                                    mRevenue.setParentMargin(mRevenueObject.getString("parentMargin"));


                                mListRevenue.add(mRevenue);
                            }

                            mNetwork.setMListChildrenRevenu(mListRevenue);

                        }

                        mList.add(mNetwork);
                        Log.e("LIST", mList.size()+"");
                    }


                    mResponse.setMUsers(mList);

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("EXC",e.toString());
        }


        return mResponse;

    }

}
