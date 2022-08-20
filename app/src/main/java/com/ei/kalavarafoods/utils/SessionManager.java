package com.ei.kalavarafoods.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ei.kalavarafoods.ui.auth.LoginActivity;

import java.util.HashMap;

import static com.ei.kalavarafoods.utils.SharedPref.Keys.IS_LOGIN;
import static com.ei.kalavarafoods.utils.SharedPref.Keys.KEY_EMAIL;
import static com.ei.kalavarafoods.utils.SharedPref.Keys.KEY_UID;
import static com.ei.kalavarafoods.utils.SharedPref.Keys.KEY_USERROLE;
import static com.ei.kalavarafoods.utils.SharedPref.Keys.KEY_USER_TYPE;

public class SessionManager {
    private static final String NUMBER_VERIFIED = "isNumberVerified";
    public static final String LAST_LOGIN_EMAIL = "lastLoginEmail";
    private static final String OTP_NUMBER = "createdOTP";
    private static final String KEY_SUB_CAT_ID = "sub_cat_ID";
    public static final String KEY_LOCALITY = "locality";
    public static final String KEY_STATE = "administrative_area_level_1";
    public static final String KEY_POSTAL_CODE = "postal_code";
    private static final String KEY_CUR_TAB_LIST = "cur_tab_list";

    //    private static final String LAST_LOGIN_EMAIL = "lastLoginEmail" ;
    // Shared Preferences

    private SharedPref mSharedPref;

    private Context _context;

    // Sharedpref file name




    // Constructor
    public SessionManager(Context context) {
        this._context = context;
    }

    /**
     * Create login session
     */
    public void createLoginSession(String email, String userId, String user_type, String user_role) {
        Log.e("creating", "login session " + email + " " + user_type);
        if (!(email.equals(Constants.DEFAULT_USER_EMAIL) || (userId.equals(String.valueOf(Constants.DEFAULT_USER_INT))))) {
            SharedPref.putBoolean(IS_LOGIN, true);
            if (user_type.equals("normal"))
                SharedPref.putString(LAST_LOGIN_EMAIL, email);
        }
        SharedPref.putString(KEY_EMAIL, email);
        SharedPref.putString(KEY_UID, userId);
        SharedPref.putString(KEY_USER_TYPE, user_type);
        SharedPref.putString(KEY_USERROLE, user_role);
    }

    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();

        // user email id
        user.put(KEY_EMAIL, SharedPref.getString(KEY_EMAIL, null));

        // user name
        user.put(KEY_UID, SharedPref.getString(KEY_UID, null));

        // user role

        user.put(KEY_USERROLE, SharedPref.getString(KEY_USERROLE, null));

        // return user
        return user;
    }


    /**
     * Clear session details
     */
    public void logoutUser() {
        SharedPref.putBoolean(IS_LOGIN, false);
        SharedPref.putBoolean(NUMBER_VERIFIED, false);
        createLoginSession(Constants.DEFAULT_USER_EMAIL, String.valueOf(Constants.DEFAULT_USER_INT), "default_user", Constants.DEFAULT_ROLE);

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Add new Flag to start new Activity
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.putExtra("caller", "SessionManager");
        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
//        return false;
        return SharedPref.getBoolean(IS_LOGIN, false);
    }

    public boolean isNumberVerified() {
        return SharedPref.getBoolean(NUMBER_VERIFIED, false);
    }

    public String getLastLoginEmail() {
        return SharedPref.getString(LAST_LOGIN_EMAIL, null);
    }

    public boolean setNumberVerified(boolean value) {
        SharedPref.putBoolean(NUMBER_VERIFIED, value);
        return true;
    }

    public void putOTP(String otp) {
        SharedPref.putString(OTP_NUMBER, otp);
    }

    public String getOTPnumber() {
        return SharedPref.getString(OTP_NUMBER, null);
    }

    public String getUserType() {
        return SharedPref.getString(KEY_USER_TYPE, "");
    }

    public String getUserRole() {
        return SharedPref.getString(KEY_USERROLE, "");
    }
    public String getUserId(){
        return SharedPref.getString(KEY_UID, "");
    }

    public void putPostalCode(String postalCode) {
        SharedPref.putString(KEY_POSTAL_CODE, postalCode);
    }

    public String getPostalCode() {
        return SharedPref.getString(KEY_POSTAL_CODE, "");
    }

    public void putSubCatID(String SubCatID) {
        SharedPref.putString(KEY_SUB_CAT_ID, SubCatID);
    }

    public String getSubCatID() {
        return SharedPref.getString(KEY_SUB_CAT_ID, null);
    }

    public void putLocality(String locality) {
        SharedPref.putString(KEY_LOCALITY, locality);

    }

    public void putState(String state) {
        SharedPref.putString(KEY_STATE, state);
    }

    public String getLocality() {
        return SharedPref.getString(KEY_LOCALITY, "");
    }

    public String getState() {
        return SharedPref.getString(KEY_STATE, "");
    }

    public void putCurTabList(String tabList) {
        SharedPref.putString(KEY_CUR_TAB_LIST, tabList);
    }

    public String getCurTabList() {
        return SharedPref.getString(KEY_CUR_TAB_LIST, "");
    }

    public String getUserEmail() {
        return SharedPref.getString(KEY_EMAIL, null);
    }
}
