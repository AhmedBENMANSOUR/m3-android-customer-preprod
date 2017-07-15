package com.dioolcustomer.constants;

import com.dioolcustomer.models.quoterequest.QuoteRequestResponse;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by ASUS on 12/04/2017.
 */

public class GlobalStatic {




    private static boolean quoteRequestListChecked = false;

    @Getter
    @Setter
    private static int sizeListQuoteRequest = 0;

    @Getter
    @Setter
    private static ArrayList<QuoteRequestResponse> mListQuoteRequestCopy = new ArrayList<>();

    @Getter
    @Setter
    private static  boolean useList = false;

    @Getter
    @Setter
    private static boolean waiteForQuoteResponse  = true;

    @Getter
    @Setter
    private static List<Integer> idQRTokenList = new ArrayList<>();

    @Getter
    @Setter
    private static List<Integer> idQRPassList = new ArrayList<>();


    public static boolean isQuoteRequestListChecked() {
        return GlobalStatic.quoteRequestListChecked;
    }

    public static void setQuoteRequestListChecked(boolean quoteRequestListChecked) {
        GlobalStatic.quoteRequestListChecked = quoteRequestListChecked;
    }
}
