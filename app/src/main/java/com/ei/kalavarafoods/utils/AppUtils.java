package com.ei.kalavarafoods.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by vishnu.benny on 1/7/2018.
 */

public class AppUtils {

    public static void shareAppUrl(Context context) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "MetroMart");
        share.putExtra(Intent.EXTRA_TEXT, "Download Metromart \n https://www.metromartapp.com");
        context.startActivity(Intent.createChooser(share, "Share....!"));
    }

    public static void rateApp(Context mContext) {
        Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        if (null != goToMarket.resolveActivity(mContext.getPackageManager())) {
            mContext.startActivity(goToMarket);
        } else {
            try {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + mContext.getPackageName())));
            } catch (ActivityNotFoundException e) {
                Log.e(mContext.getClass().getSimpleName(), e.getMessage(), e);
            }
        }
    }

    public static boolean isGooglePlayServiceAvailable(Context context){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
