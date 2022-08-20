package com.ei.kalavarafoods.ui.profile;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.ei.kalavarafoods.model.api.UserOrder;
import com.ei.kalavarafoods.model.api.UserOrdersItem;
import com.ei.kalavarafoods.network.ApiInterface;
import com.ei.kalavarafoods.network.RetrofitClient;
import com.ei.kalavarafoods.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileViewModel extends AndroidViewModel {

    private ApiInterface mApiInterface;
    private SessionManager mSessionManager;

    public MutableLiveData<List<UserOrder>> liveDataUserOrders = new MutableLiveData<>();
    public MutableLiveData<Boolean> liveDateIsLoading = new MutableLiveData<>();


    public ProfileViewModel(@NonNull Application application) {
        super(application);
        mApiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);
        mSessionManager = new SessionManager(application);
    }

    public void getUserOrders(){
        String userId = mSessionManager.getUserId();
        JSONObject oderObj = new JSONObject();
        try {
            oderObj.put("user_id", userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        liveDateIsLoading.setValue(true);
        mApiInterface.getUserOrder(oderObj.toString()).enqueue(new Callback<UserOrdersItem>() {
            @Override
            public void onResponse(Call<UserOrdersItem> call, Response<UserOrdersItem> response) {
                liveDataUserOrders.setValue(response.body().getUserOrder());
                liveDateIsLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<UserOrdersItem> call, Throwable t) {
                liveDateIsLoading.setValue(false);
            }
        });

    }
}
