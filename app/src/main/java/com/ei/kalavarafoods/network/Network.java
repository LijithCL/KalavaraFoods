package com.ei.kalavarafoods.network;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

@SuppressWarnings("deprecation")
public class Network {
    private final ConnectivityManager connectivityManager;
    private final TelephonyManager telephonyManager;
    private static Network network;

    private Network(Activity activity) {
        this.connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
    }

    private boolean isConnected() {
        return isConnectedToCellNetwork() || isConnectedToWifiNetwork();
    }

    private boolean isConnectedToCellNetwork() {
        int simState = telephonyManager.getSimState();
        if (simState == TelephonyManager.SIM_STATE_READY) {
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return networkInfo.isConnectedOrConnecting();
        }
        return false;
    }

    private boolean isConnectedToWifiNetwork() {
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    public static boolean isOnline(Activity activity) {
        if (network == null) {
            network = new Network(activity);
        }
        return network.isConnected();
    }
}
