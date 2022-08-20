package com.ei.kalavarafoods.ui.address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ei.kalavarafoods.network.RetrofitClient;
import com.ei.kalavarafoods.ui.address.model.AddressResponse;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressViewModel extends ViewModel {
    private MutableLiveData<AddressResponse> addressResponseMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public LiveData<AddressResponse> addAddress(String userId,
                               String address,
                               String addressPostcode,
                               String addressPlace,
                               String addressLandmark){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id", userId);
            jsonObject.put("address", address);
            jsonObject.put("address_pincode", addressPostcode);
            jsonObject.put("address_place", addressPlace);
            jsonObject.put("address_landmark", addressLandmark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isLoading.setValue(true);
        RetrofitClient.getApiInterface().addAddress(jsonObject.toString()).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful()){
                    addressResponseMutableLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                isLoading.setValue(false);
            }
        });

        return addressResponseMutableLiveData;
    }

    public LiveData<AddressResponse> editAddress(String addressId,
                            String userId,
                            String address,
                            String addressPostcode,
                            String addressPlace,
                            String addressLandmark){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("address_id", addressId);
            jsonObject.put("user_id", userId);
            jsonObject.put("address", address);
            jsonObject.put("address_pincode", addressPostcode);
            jsonObject.put("address_place", addressPlace);
            jsonObject.put("address_landmark", addressLandmark);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        isLoading.setValue(true);
        RetrofitClient.getApiInterface().editAddress(jsonObject.toString())
                .enqueue(new Callback<AddressResponse>() {
                    @Override
                    public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                        isLoading.setValue(false);
                        if (response.isSuccessful()){
                            addressResponseMutableLiveData.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<AddressResponse> call, Throwable t) {
                        isLoading.setValue(false);
                    }
                });

        return addressResponseMutableLiveData;
    }
}
