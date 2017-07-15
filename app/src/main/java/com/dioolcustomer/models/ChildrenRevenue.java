package com.dioolcustomer.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sihem.messaoui on 14/08/2016.
 */
public class ChildrenRevenue {


    @Getter
    @Setter
    private String userId;


    @Getter
    @Setter
    private String childUserId;



    @Getter
    @Setter
    private String nostroOperationId;



    @Getter
    @Setter
    private String providerName;




    @Getter
    @Setter
    private String operationName;



    @Getter
    @Setter
    private String margin;



    @Getter
    @Setter
    private String parentMargin;


    public ChildrenRevenue() {
    }
}
