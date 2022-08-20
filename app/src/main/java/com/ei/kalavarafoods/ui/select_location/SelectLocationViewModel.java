package com.ei.kalavarafoods.ui.select_location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ei.kalavarafoods.network.RetrofitClient;
import com.ei.kalavarafoods.ui.select_location.model.LocationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectLocationViewModel  extends ViewModel {
    private MutableLiveData<LocationResponse> locationResponseLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> liveDataIsLoading = new MutableLiveData<>();

    public LiveData<LocationResponse> getDeliveryLocations(){
        liveDataIsLoading.setValue(true);
        RetrofitClient.getApiInterface().getDeliveryLocations().enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                liveDataIsLoading.setValue(false);
                if (response.isSuccessful()) {
                    locationResponseLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                liveDataIsLoading.setValue(false);
            }
        });

        return locationResponseLiveData;
    }
}
