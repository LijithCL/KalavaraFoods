package com.ei.kalavarafoods.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.ei.kalavarafoods.utils.Common;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.db.AppDatabase;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.model.api.PostResult;
import com.ei.kalavarafoods.model.api.Product;
import com.ei.kalavarafoods.model.api.BrandProductsList;
import com.ei.kalavarafoods.network.ApiInterface;
import com.ei.kalavarafoods.network.RetrofitClient;
import com.ei.kalavarafoods.repository.DataRepository;
import com.ei.kalavarafoods.utils.SharedPref;

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

/**
 * Created by ULLAS BABU on 22-Feb-18.
 */

public class BrandItemsViewModel extends AndroidViewModel {
    private AppDatabase mDb;
    private SessionManager mSessionManager;
    private ApiInterface mApiInterface;

    public MutableLiveData<List<Product>> liveDataProductList = new MutableLiveData<>();
    public MutableLiveData<Boolean> liveDataIsLoding = new MutableLiveData<>();
    public MutableLiveData<PostResult> livedataPostResult = new MutableLiveData<>();

    private DataRepository mDataRepository;

    public BrandItemsViewModel(@NonNull Application application) {
        super(application);
        mDb = AppDatabase.getDatabase(application);
        mSessionManager = new SessionManager(application);
        mApiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);
        mDataRepository = DataRepository.getInstance(application);
    }


    public void getBrandProducts(String brandId){
        if (mSessionManager.isLoggedIn()) {
            String userId = mSessionManager.getUserDetails().get(SharedPref.Keys.KEY_UID);
            JSONObject getBrandItemsJson = new JSONObject();
            try {
                getBrandItemsJson.put("brand_id", brandId);
                getBrandItemsJson.put("userid", userId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String brandIdToPost = getBrandItemsJson.toString();
            liveDataIsLoding.setValue(true);
            mApiInterface.getBrandProduct(brandIdToPost).enqueue(new Callback<BrandProductsList>() {
                @Override
                public void onResponse(Call<BrandProductsList> call, Response<BrandProductsList> response) {
                    BrandProductsList productList = response.body();
                    liveDataProductList.setValue(productList.getProduct());
                    liveDataIsLoding.setValue(false);
                }

                @Override
                public void onFailure(Call<BrandProductsList> call, Throwable t) {
                    liveDataIsLoding.setValue(false);
                }
            });
        }else {
            toLogin();
        }
    }

    private void toLogin() {
    }

    public Completable insertToProductsEntity(List<Product> productList){
        List<ProductEntity> productEntityList = Common.convertProductToProductEntity(productList);
        return Completable.fromAction(() -> mDb.productDao().insertProducts(productEntityList));
    }

    public LiveData<List<ProductEntity>> getCartItems(){
       return mDataRepository.getProductsInCart();
    }

    public void updateCartItem(String quantity, String itemId) {
        Completable.fromAction(() -> mDb.productDao().updateCartItems(quantity, itemId))
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

    public void addToWish(String productId){
        String userId = mSessionManager.getUserId();

        JSONObject removeProductJson = new JSONObject();
        try {
            removeProductJson.put("user_id", userId);
            removeProductJson.put("product_id", productId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mApiInterface.addWish(removeProductJson.toString()).enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {

            }
        });
    }


    public void removeWish(String productId){
        String userId = mSessionManager.getUserId();

        JSONObject removeProductJson = new JSONObject();
        try {
            removeProductJson.put("user_id", userId);
            removeProductJson.put("product_id", productId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mApiInterface.removeWish(removeProductJson.toString()).enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {

            }

            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {

            }
        });
    }
}
