package com.ei.kalavarafoods.utils;

public class Constants {


    public static final String CUSTOMER_NAME = "customer_name" ;
    public static final String CUSTOMER_ADDRESS = "customer_address";
    public static final String ORDER_STATUS = "order_status";
    public static final String PREF_NAME = "KalavaraFoodsPref";
    public static final String SUCCESS = "success";
    public static final String OTP = "otp";
    public static final String PHONE = "phone";

    Constants() {
    }

    public static String API_KEY = "extantmetro";
    public static final String HEADER = "X-API-KEY";
    private static String URL_BASE = "http://www.extantinfotech.com/";
    private static String URL_APPNAME = "KalavaraFoods/";
    private static String URL_APP = URL_BASE + URL_APPNAME;
    public static String CATEGORY = URL_APP + "shopmob/home_categories?" + HEADER + "=" + API_KEY;
    static String CATEGORY_NEW = URL_APP + "shopmob/maincategoryproducts?" + HEADER + "=" + API_KEY + "&main_categoryid=";
    //CATEGORY_NEW have two parameters to pass, main category id and user id. userid is adding from the calling location
    public static String HOME_SLIDER = URL_APP + "shopmob/home_sliders?" + HEADER + "=" + API_KEY;
    public static String CHECKOUT = URL_APP + "shopmob/proceedcheckout";
    public static String RESET_PASS = URL_APP + "usermob/resetpassword";
    public static String RESET_REQUEST = URL_APP + "usermob/forgetpassword";
    public static String ADDRESS = URL_APP + "shopmob/addaddress";
    public static String LIST_ADDRESS = URL_APP + "shopmob/listaddress";
    public static String EDIT_ADDRESS = URL_APP + "shopmob/editaddress";
    public static String DEL_ADDRESS = URL_APP + "shopmob/deleteaddress";
    public static String SIGNIN = URL_APP + "usermob/signin";
    public static String SIGNUP = URL_APP + "usermob/signUp";

    public static String CHANGE_PASS = URL_APP + "usermob/resetoldpassword";
    public static String SEARCH = URL_APP + "shopmob/search";
    public static String SHOP_HISTORY = URL_APP + "shopmob/historyshopping";
    public static String HISTORY_ITEMS = URL_APP + "shopmob/historyitems";

    static String OTP_REQUEST = URL_APP + "shopmob/home_categories?" + HEADER + "=" + API_KEY;
    public static final String SMS_ORIGIN = "DM-";
    public static final String OTP_DELIMITER = ":";
    public static final int OTP_LENGTH = 6;
    public static final String URL_VERIFY_OTP = URL_APP + "shopmob/home_categories?" + HEADER + "=" + API_KEY;
    public static final String URL_PRODUCT_WISH = URL_APP + "shopmob/addmywish";
    public static final String URL_PRODUCT_WISH_REMOVE = URL_APP + "shopmob/removemywish";
    public static final String URL_LOCATION_GOOGLE_API = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
    public static final String URL_USER_WHISHLIST = URL_APP + "shopmob/mywishtlist";
    public static final String URL_UPDATE_REGISTRATION_TOKEN = URL_APP + "usermob/registerDevice";

    public static final String DEFAULT_USER_EMAIL = "user@KalavaraFoods.com";
    public static final String SIGNUP_FB = URL_APP + "usermob/fbsignup";
    public static final int DEFAULT_USER_INT = 0;
    public static final int KNOWN_USER_INT = 1;
    public static final String DEFAULT_ROLE = "defaultRole" ;
    public static final String BRAND_ID = "brand_id";
    public static final String BRAND_NAME = "brand_name";
    public static final String ORDER_ID = "order_id" ;
    public static final String CATEGORY_IMAGE = "category_image";
    public static final String CATEGORY_TITLE = "category_title";
    public static final String CATEGORY_SUBTITLE = "category_subtitle";
    public static final String CATEGORY_MAIN_ID = "category_main_id";
    public static final int REQUEST_PERMISSION_COARSE_AND_FINE_LOCATION = 100;


    public class AuthMode {
        public static final String MODE = "Logout";
    }
}
