package com.dioolcustomer.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sihem.messaoui on 14/08/2016.
 */
public class UserNetwork implements Parcelable {
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String userId;

    @Getter
    @Setter
    private String profile;


    @Getter
    @Setter
    private String name;


    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String picture;


    @Getter
    @Setter
    private String firstname;


    @Getter
    @Setter
    private String lastname;


    @Getter
    @Setter
    private String phone;

    @Getter
    @Setter
    private String adress;


    @Getter
    @Setter
    private String businessName;

    @Getter
    @Setter
    private String daCredit;


    @Getter
    @Setter
    private String crCredit;


    @Getter
    @Setter
    private ArrayList<String> mListParents=new ArrayList<>();


    @Getter
    @Setter
    private String childrens;



    @Getter
    @Setter
    private ArrayList<ChildrenRevenue> mListChildrenRevenu=new ArrayList<>();

    public UserNetwork() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
