package com.dioolcustomer.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by sihem.messaoui on 23/05/2016.
 */
public class History {

    @Getter
    @Setter
    private String extSenderTransactionRef;

    @Getter
    @Setter
    private String extSenderTransactionTimestamp;

    @Getter
    @Setter
    private String extRecipientTransactionRef;

    @Getter
    @Setter
    private String extRecipientTransactionTimestamp;


    @Getter
    @Setter
    private String id;


    @Getter
    @Setter
    private String userId;

    @Getter
    @Setter
    private String date;


    @Getter
    @Setter
    private String senderProviderIdentifier;


    @Getter
    @Setter
    private String senderServiceType;



    @Getter
    @Setter
    private String senderProviderAccountId;


    @Getter
    @Setter
    private String recipientProviderAccountId;


    @Getter
    @Setter
    private String senderUserIdentifier;


    @Getter
    @Setter
    private Object amount;


    @Getter
    @Setter
    private String recipientProviderIdentifier;


    @Getter
    @Setter
    private String recipientServiceType;


    @Getter
    @Setter
    private String recipientUserIdentifier;


    @Getter
    @Setter
    private String comment;


    @Getter
    @Setter
    private String transactionStatus;

    @Getter
    @Setter
    private String details;


    @Getter
    @Setter
    private Object userRevenue;

    @Getter
    @Setter
    private String recipientUserAlias;

    @Getter
    @Setter
    private String senderUserAlias;


    public History() {
    }
}
