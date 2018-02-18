package com.cader831.ahmed.enther;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ahmed on 16/01/2018.
 */

public class Utility {
    public static final String FILE_COINSCONTROLLER = "coins_controller.dat";
    public static final String FILE_EXCHANGECONTROLLER = "exchange_controller.dat";

    public static final String PREFS_COINS_FILE_CREATED = "local_coins_created";
    public static final String PREFS_EXCHANGES_FILE_CREATED = "local_exchanges_created";
    public static final String PREFS_CUSTOM_PORTFOLIO_CREATED = "portfolio_created";

    public static final int ACTIVITYRESULT_PRIMARY_COIN_REQUEST = 1;
    public static final int ACTIVITYRESULT_SECONDARY_COIN_REQUEST = 2;
    public static final int ACTIVITYRESULT_EXCHANGE_REQUEST = 3;


    public static boolean networkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
