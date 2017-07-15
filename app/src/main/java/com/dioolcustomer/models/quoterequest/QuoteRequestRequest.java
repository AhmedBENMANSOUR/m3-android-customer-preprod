package com.dioolcustomer.models.quoterequest;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 21/03/2017.
 */

public class QuoteRequestRequest {


    @Getter
    @Setter
    private String providerId;

    @Getter
    @Setter
    private String merchantFirstName;

    @Getter
    @Setter
    private String merchantLastName;


    @Getter
    @Setter
    private Float amount;

    @Getter
    @Setter
    private String serviceType;


    @Getter
    @Setter
    private Float latitude;

    @Getter
    @Setter
    private Float longitude;


    @Getter
    @Setter
    private String address;




    public  QuoteRequestRequest(){

    }
}
