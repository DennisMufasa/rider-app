package com.cog.arcaneRider.adapter;

/**
 * Created by Cogzidel.
 *
 * Constants used by this chatting application.
 * TODO: Replace PUBLISH_KEY and SUBSCRIBE_KEY with your personal keys.
 * TODO: Register app for GCM and replace GCM_SENDER_ID
 */
public class Constants {

    //Google API Key
    public static final String Google_API_KEY   = "Your Google API Key";

    //Google Direction Key
    public static final String Google_DIRECTION_KEY   = "Your Google Direction Key";

    /*public static final String LIVE_URL   =   "http://demo.cogzidel.com/arcane_lite/rider/";

    public static final String LIVE_URL_DRIVER   =   "http://demo.cogzidel.com/arcane_lite/driver/";

    public static final String REQUEST_URL   =   "http://demo.cogzidel.com/arcane_lite/requests/";

    public static final String CATEGORY_LIVE_URL   =   "http://demo.cogzidel.com/arcane_lite/";*/

    //Google Distance matrix base URL
    public static final String DISTANCE_MATRIX   = "https://maps.googleapis.com/maps/api/distancematrix/json?";

    public static final String LIVE_URL   =   "http://products.cogzidel.com/arcane/rider/";

    public static final String LIVE_URL_DRIVER   =   "http://products.cogzidel.com/arcane/driver/";

    public static final String REQUEST_URL   =   "http://products.cogzidel.com/arcane/requests/";

    public static final String CATEGORY_LIVE_URL   =   "http://products.cogzidel.com/arcane/";

    //Autocomplete
    public static final int ORIGIN_REQUEST_CODE_AUTOCOMPLETE = 1;
    public static final int DEST_REQUEST_CODE_AUTOCOMPLETE = 12;

    public static final String PAYMENT_TYPE_CASH   =   "cash";

    public static final String PAYMENT_TYPE_CARD   =   "stripe";

    //Web page URL
    public static final String WEB_PAGE_URL   = "http://www.cogzidel.com";

    //PREFS_KEY
    public static final String MY_PREFS_NAME ="Arcane";

    //State key
    public static final String MY_STATE_KEY="saveTripState";

    // Splash screen timer
    public static int SPLASH_TIME_OUT = 2000;

}
