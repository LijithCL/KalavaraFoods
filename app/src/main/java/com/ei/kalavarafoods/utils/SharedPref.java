package com.ei.kalavarafoods.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.ei.kalavarafoods.utils.Constants.PREF_NAME;

public class SharedPref {

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    public SharedPref(Context context){
        mSharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void initiatePreferences(Context context){
        mSharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void putString(String key, String value){
        mEditor = mSharedPreferences.edit();
        mEditor.putString(key, value);
        mEditor.apply();
    }
    public static void putInt(String key, int data){
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(key, data);
        mEditor.apply();
    }
    public static void putFloat(String key, float data){
        mEditor = mSharedPreferences.edit();
        mEditor.putFloat(key, data);
        mEditor.apply();
    }
    public static void putLong(String key, long data){
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(key, data);
        mEditor.apply();
    }
    public static void putBoolean(String key, boolean data){
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(key, data);
        mEditor.apply();
    }

    public static String getString(String key, String defValue){
       return mSharedPreferences.getString(key, defValue);
    }
    public static int getInt(String key, int defValue){
        return mSharedPreferences.getInt(key, defValue);
    }
    public static float getFloat(String key, float defValue){
        return mSharedPreferences.getFloat(key, defValue);
    }
    public static long getLong(String key, long defValue){
        return mSharedPreferences.getLong(key, defValue);
    }
    public static boolean getBoolean(String key, boolean defValue){
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public static class Keys{
        public static final String SELECTED_LOCATION = "selected_location";
        public static final String SELECTED_LOCATION_ID = "selected_location_id";
        public static final String IS_LOGIN = "IsLoggedIn";
        public static final String KEY_EMAIL = "email";
        public static final String KEY_UID = "pass";
        public static final String KEY_USERROLE = "userrole";
        public static final String KEY_USER_TYPE = "logged_userType";
        public static final String PHONE_NUMBER = "phone_number";
    }

}
