package com.ei.kalavarafoods.ui.main.account.change_phone;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ei.kalavarafoods.network.RetrofitClient;
import com.ei.kalavarafoods.ui.number_verification.model.OtpResponse;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePhoneViewModel extends ViewModel {

    private MutableLiveData<OtpResponse> otpResponseMutableLiveData = new MutableLiveData<>();

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
