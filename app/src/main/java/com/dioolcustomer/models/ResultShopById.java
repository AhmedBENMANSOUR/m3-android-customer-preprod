package com.dioolcustomer.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 13/01/2017.
 */

public class ResultShopById {

    @Getter
    @Setter
    private Integer id;


    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String logitude;

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

    public ResultShopById(){

    }
}
