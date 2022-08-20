package com.ei.kalavarafoods.ui.main.account;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.ei.kalavarafoods.model.api.AddressItem;
import com.ei.kalavarafoods.network.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountsViewModel extends AndroidViewModel {

    public AccountsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<AddressItem> getUserAddresses(String userId){
        MutableLiveData<AddressItem> addressItemMutableLiveData = new MutableLiveData<>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RetrofitClient.getApiInterface().getUserAddresses(jsonObject.toString())
                .enqueue(new Callback<AddressItem>() {
                    @Override
                    public void onResponse(Call<AddressItem> call, Response<AddressItem> response) {
                        if (response.isSuccessful()){
                            addressItemMutableLiveData.setValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<AddressItem> call, Throwable t) {

                    }
                });

        return addressItemMutableLiveData;
    }

}
