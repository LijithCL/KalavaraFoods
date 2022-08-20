package com.ei.kalavarafoods.ui.auth;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.Log;

import com.ei.kalavarafoods.ui.number_verification.model.OtpResponse;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.model.api.SignUpLoginPostResult;
import com.ei.kalavarafoods.network.ApiInterface;
import com.ei.kalavarafoods.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ULLAS BABU on 07-Apr-18.
 */

public class AuthViewModel extends AndroidViewModel {
    private static final String TAG = AuthViewModel.class.getSimpleName();
    private ApiInterface mApiInterface;
    private SessionManager mSessionManager;
    public MutableLiveData<SignUpLoginPostResult> liveDataSignUpPostResult = new MutableLiveData<>();
    public MutableLiveData<Boolean> liveDataIsLoading = new MutableLiveData<>();
    private MutableLiveData<OtpResponse> otpResponseMutableLiveData = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        mApiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);
        mSessionManager = new SessionManager(application);
    }

    public void signUpUser(String name, String phone, String password){
        JSONObject signUpJson = new JSONObject();
        try {
            signUpJson.put("name", name);
            signUpJson.put("phone", phone);
            signUpJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        liveDataIsLoading.setValue(true);
        mApiInterface.signUpUser(signUpJson.toString()).enqueue(new Callback<SignUpLoginPostResult>() {
            @Override
            public void onResponse(Call<SignUpLoginPostResult> call, Response<SignUpLoginPostResult> response) {
                liveDataIsLoading.setValue(false);
                liveDataSignUpPostResult.setValue(response.body());
            }

            @Override
            public void onFailure(Call<SignUpLoginPostResult> call, Throwable t) {
                liveDataIsLoading.setValue(false);
            }
        });
    }

    public void loginUser(String phone, String password){
        JSONObject loginJson = new JSONObject();
        try {
            loginJson.put("phone", phone);
            loginJson.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "loginUser: "+loginJson.toString());
        liveDataIsLoading.setValue(true);
        mApiInterface.loginUser(loginJson.toString()).enqueue(new Callback<SignUpLoginPostResult>() {
            @Override
            public void onResponse(Call<SignUpLoginPostResult> call, Response<SignUpLoginPostResult> response) {
                liveDataSignUpPostResult.setValue(response.body());
                liveDataIsLoading.setValue(false);
            }
            @Override
            public void onFailure(Call<SignUpLoginPostResult> call, Throwable t) {
                liveDataIsLoading.setValue(false);
            }
        });
    }

    public void userLoginSocial(String name, String email, String userSource){
        JSONObject userLoginSocialJson = new JSONObject();
        try {
            userLoginSocialJson.put("name", name);
            userLoginSocialJson.put("email", email);
            userLoginSocialJson.put("user_source", userSource);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        liveDataIsLoading.setValue(true);
        mApiInterface.signUpUserSocial(userLoginSocialJson.toString()).enqueue(new Callback<SignUpLoginPostResult>() {
            @Override
            public void onResponse(Call<SignUpLoginPostResult> call, Response<SignUpLoginPostResult> response) {
                if (response.isSuccessful()) {
                    liveDataSignUpPostResult.setValue(response.body());
                }
                liveDataIsLoading.setValue(false);
            }
            @Override
            public void onFailure(Call<SignUpLoginPostResult> call, Throwable t) {
                liveDataIsLoading.setValue(false);
            }
        });
    }

    public void userDataToSharedPref(String email, String userId, String userType, String userRole){
        mSessionManager.createLoginSession(email, userId, userType, userRole);
    }

    public static Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();
        try {
            String id = object.getString("id");
            URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=800&height=800");
            Log.i("profile_pic", profile_pic + "");
            bundle.putString("profile_pic", profile_pic.toString());
            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return bundle;
    }


    public LiveData<OtpResponse> getOtpMobile(String mobileNo){
        JSONObject postJsonObj = new JSONObject();
        try {
            postJsonObj.put("mob", mobileNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitClient.getApiInterface().getOtpMobile(postJsonObj.toString())
                .enqueue(new Callback<OtpResponse>() {
                    @Override
                    public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                        if (response.isSuccessful()) {
                            otpResponseMutableLiveData.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<OtpResponse> call, Throwable t) {

                    }
                });

        return otpResponseMutableLiveData;
    }
}
