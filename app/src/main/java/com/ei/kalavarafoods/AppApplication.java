package com.ei.kalavarafoods;

import android.app.Application;
import android.content.Context;
import androidx.multidex.MultiDex;

import com.ei.kalavarafoods.utils.SharedPref;

public class AppApplication extends Application {
    private static AppApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        SharedPref.initiatePreferences(this);
    }

    public static AppApplication getmInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
