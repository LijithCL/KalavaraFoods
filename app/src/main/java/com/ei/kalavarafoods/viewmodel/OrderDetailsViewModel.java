package com.ei.kalavarafoods.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.ei.kalavarafoods.db.AppDatabase;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ULLAS BABU on 16-Feb-18.
 */

public class OrderDetailsViewModel extends AndroidViewModel {
    AppDatabase mDb;

    public OrderDetailsViewModel(@NonNull Application application) {
        super(application);
        mDb = AppDatabase.getDatabase(getApplication());
    }

    public  void updateOrderStatus(JSONObject updateJson){
       new UpdateOrderStatusAsync().execute(updateJson);
    }

    class UpdateOrderStatusAsync extends AsyncTask<JSONObject, Void, String>{
        @Override
        protected String doInBackground(JSONObject... jsonObjects) {
            String status, orderId;
            try {
                status = jsonObjects[0].getString("status");
                orderId = jsonObjects[0].getString("order_id");

                mDb.orderDao().updateOrderStatus(orderId, status);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
