package com.dioolcustomer.constants;

/**
 * Global Constants
 */
public class GlobalConstants {

    public static final String MMM_SHARED_PREFERENCES = "shared_data_merchant";

    public static final String USER_LOGIN = "MMM_USER_LOGIN";

    public static final String MTNC_VALUES = "62401";
    public static final String OCM_VALUE = "62402";
    public static final String NXTL_VALUE = "62404";

    // URL PROD
    //public static final String BASE_URL = "https://app.diool.com:8113/diool/api/v1/";
    //public static final String BASE_URL = "https://core.diool.com/api/v1/";


    // URL PREPROD
   // public static final String BASE_URL = " https://emonize.mymoneytop.com:8993/diool/api/v1/";
   // public static final String BASE_URL = "https://core.diool.com/api/v1/";

    //  URL DEVELOP
    public static final String BASE_URL ="http://devcore.diool.com/api/v1/";
    public static final String API_PATH = "/m3api";
    //local
    //public static final String API_URL=BASE_URL+API_PATH;
    //Preprod
    //public static final String API_URL=BASE_URL+API_PATH;
    //Prod
    public static final String API_URL = BASE_URL + API_PATH;


    public static final String CGM_URL_MERCHANT = BASE_URL + "/documents/CGU_MyMoneyMobile_Entreprise.pdf";
    public static final String CGM_URL_CUSTOMER = BASE_URL + "/documents/CGU_MyMoneyMobile_Customer.pdf";

    public static final String CALLBACK_URL_NETVERIFY = API_URL + "?action=updateuserverificationstatus";
    public static final String PHONE_PREFIX = "237";

    public static final Integer cat1Timeout = 45000;
    public static final Integer cat2Timeout = 90000;
    public static final Integer cat3Timeout = 300000;

    //Timeout manegement
    public static final int MY_SOCKET_TIMEOUT_MS = 30000;

    public static final String PROFILE_CUSTOMER = "6";
    public static final String PROFILE_MERCHANT = "4";

    public static final String ANONYMOUS_ID1 = "v6GqpIJTYRunmmUY2lTtu61ApxmMi6U3"; //SEGMENT_WRITE_KEY

    /*test*
    public static final String INTERCOM_API_KEY = "b4d224755fd7ec20319a54acc12fbf9adb43b97e";
    public static final String INTERCOM_APP_ID = "u1es1kqd";
    /* */
    /*prod*/
    public static final String ANONYMOUS_ID2 = "android_sdk-ffa7531ba679665b9cf16aa0933ead54951f1aee";//INTERCOM_API_KEY
    //public static final String INTERCOM_APP_ID = "x9b3thxf";
    //public static final String INTERCOM_API_KEY = "android_sdk-ed9271ec2f87ee5ccbee41fc1c44c9e806b0f754";
    public static final String ANONYMOUS_ID3 = "x9b3thxf"; //INTERCOM_APP_ID
    /**/
    public static final String ANONYMOUS_ID4 = "4fae905c-8738-4a46-95fb-fd150a04e71d";//NETVERIFY_TOKEN => login
    public static final String ANONYMOUS_ID5 = "IVu9AKUZPfVnzvifYvt1CrhzO2XFNKYq";//NETVERIFY_SECRET => password


    //Broadcast action
    public static final String ACTION_LOGOUT = "com.mymoneymobile.ACTION_LOGOUT";

    //Custom Map
    public static final String MAP_URL = "https://www.google.com/maps/d/kml?hl=en_US&app=mp&mid=zg0k04tQ_4iU.kWAj9PVLSh0A&forcekml=1&cid=mp&cv=h0duE_VTKpY.en_US.";
    //public static final String MAP_URL= "https://www.google.com/maps/d/kml?hl=en_US&app=mp&mid=zg0k04tQ_4iU.kWAj9PVLSh0A&lid=zg0k04tQ_4iU.kFNmhYdMWJ6s&forcekml=1&cid=mp&cv=h0duE_VTKpY.en_US.";


    //  Transfer URL
    public static String URL_TRNSFERT = BASE_URL + "operations/transfer";
    // Url Balance
    public static String URL_BALANCE = BASE_URL + "stats/balance?beVersion=2.4.0";
    // Url Redeem
    public static String URL_REDEEM = BASE_URL + "operations/redeem";
    //Url payment
    public static String URL_PAYMENT = BASE_URL + "operations/payment";
    //Url add funds
    public static String URL_ADD_FUNDS = BASE_URL+ "operations/addFunds";
    //Url Withdraw funds
    public static String URL_WITHDRAW_FUNDS = BASE_URL + "operations/withdrawFunds";
    // Url History
    public static String URL_HISTORY = BASE_URL + "stats/transferHistory";
    // Url Redeem History
    public static String URL_REDEEM_HISTORY = BASE_URL + "stats/redeemHistory";

    // Url Update Profile
    public static String URL_UPADTE_PROFILE = BASE_URL + "useraccount/update";


    // Url get Networks
    public static String URL_GET_NETWORKS = BASE_URL + "useraccount/usersNetwork";


    //Url get user_id by email
    public static String URL_GET_USER_ID_BY_EMAIL = BASE_URL + "useraccount/getUserIdByEmail";
    //Url get shop name by shopid
    public static String URL_GET_SHOP_NAME_By_ID = BASE_URL + "/shops/getShopById";
    // Url get children's emails
    public static String GET_CHILDREN_S_EMAIL = BASE_URL + "/useraccount/getEmails";
    // Url check mobile app version
    public static String GET_APP_VERSION = BASE_URL + "health/checkMobileAppVersion";

    //Url request for quote request (customer)
    public static  String URL_QUOTE_REQUEST = BASE_URL + "operations/quoteRequest";
    //Url check active quote request FOR CUSTOMER
    public static  String URL_CHECK_CUSTOMER_ACTIVE_QUOTE_REQUEST = BASE_URL + "operations/getCustomerActiveQuoteRequest";
    //Url close quote request
    public static  String URL_CLOSE_QUOTE_REQUEST = BASE_URL + "operations/closeQuoteRequest";
    //Url register For Quote Request
    public static  String URL_REGISTER_FOR_QUOTE_REQUEST = BASE_URL + "operations/registerForQuoteRequest";
    //Url unregister forqQuote request
    public static  String URL_UNREGISTER_FOR_QUOTE_REQUEST = BASE_URL + "operations/unregisterForQuoteRequest";

    //Url get quotes requests
    public static  String URL_GET_QUOTE_REQUEST = BASE_URL + "operations/getQuoteRequests";

    //Url select quote request
    public static  String URL_SELECT_QUOTE_REQUEST = BASE_URL + "operations/selectQuoteRequest";


    //Url pass quote request
    public static String URL_PASS_QUOTE_REQUEST = BASE_URL + "operations/passQuoteRequest";

    //Url get shops by owner
    public static String URL_GET_SHOPS_BY_OWNER = BASE_URL + "shops/getShopsByOwner";

    //Url ssign shop for merchant
    public static String URL_ASSIGN_SHOP_FOR_MERCHANT = BASE_URL + "shops/assignShopForMerchant";

    //URL get all shops
    public static String URL_GET_ALL_SHOPS = BASE_URL + "shops/getShops";

}
