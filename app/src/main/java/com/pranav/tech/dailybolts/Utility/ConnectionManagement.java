package com.pranav.tech.dailybolts.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionManagement {
    public static boolean isConnected(Context context) {
        ConnectivityManager connected = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connected != null) {
            networkInfo = connected.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }
}
