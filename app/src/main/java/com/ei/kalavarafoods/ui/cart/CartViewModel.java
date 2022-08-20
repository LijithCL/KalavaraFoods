package com.ei.kalavarafoods.ui.cart;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.ei.kalavarafoods.db.AppDatabase;
import com.ei.kalavarafoods.model.api.PostResult;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.network.ApiInterface;
import com.ei.kalavarafoods.network.RetrofitClient;
import com.ei.kalavarafoods.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartViewModel extends AndroidViewModel {
    private AppDatabase mAppDatabase;
    private SessionManager mSessionManager;
    private ApiInterface mApiInterface;
    public LiveData<Boolean> isOrderPlaced;

    public CartViewModel(@NonNull Application application) {
        super(application);
        mAppDatabase = AppDatabase.getDatabase(application);
        mSessionManager = new SessionManager(application);
        mApiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);
    }

    public List<ProductEntity> getCartItems(){
        return mAppDatabase.productDao().getItemsAddedToCart();
    }

    public LiveData<List<ProductEntity>> getCartItemsAsync(){
        return mAppDatabase.productDao().getItemsAddedToCartAsync();
    }

    public String getProductOrderQuantity(String productId){
        return mAppDatabase.productDao().getOrderQuantityById(productId);
    }

    public void updateCartItem(String quantity, String itemId) {
        Completable.fromAction(() -> mAppDatabase.productDao().updateCartItems(quantity, itemId))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    public void clearCart() {
        mAppDatabase.productDao().clearCartItems();
    }

    public void placeOrder(List<ProductEntity> itemsInCart, String deliveryDate,
                           String deliveryTime, String deliveryCharge, Object addressSetTag){

        JSONObject jsonObjPlaceOrder = new JSONObject();
        JSONArray jsonArrayItems = new JSONArray();
        String userId = mSessionManager.getUserId();
        MutableLiveData<Boolean> isOrderPlaced = new MutableLiveData<>();


        for (ProductEntity productEntity : itemsInCart){
            JSONObject jsonObjProduct = new JSONObject();
            try {
                jsonObjProduct.accumulate("product_id", productEntity.getProductId());
                jsonObjProduct.accumulate("product_price", productEntity.getProductUnitprice());
                jsonObjProduct.accumulate("product_variant_id","");
                jsonObjProduct.accumulate("product_quantity",productEntity.getProductOrderQuantitiy());
                jsonArrayItems.put(jsonObjProduct);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        try {
            jsonObjPlaceOrder.accumulate("deliverydate", deliveryDate);
            jsonObjPlaceOrder.accumulate("deliverytime", deliveryTime);
            jsonObjPlaceOrder.accumulate("deliverycharge", deliveryCharge);
            jsonObjPlaceOrder.accumulate("addressid", addressSetTag);
            jsonObjPlaceOrder.accumulate("user_id", userId);
            jsonObjPlaceOrder.accumulate("items", jsonArrayItems);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mApiInterface.placeOrder(jsonObjPlaceOrder.toString()).enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                if (response.body().getMessage().contains("success message")){
                    isOrderPlaced.setValue(true);
                }else {
                    isOrderPlaced.setValue(false);
                }
            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {

            }
        });

    }
}
