package com.ei.kalavarafoods.ui.search;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.ei.kalavarafoods.model.api.Brand;
import com.ei.kalavarafoods.model.api.BrandItem;
import com.ei.kalavarafoods.model.api.PostResult;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.utils.Common;
import com.ei.kalavarafoods.utils.SessionManager;
import com.ei.kalavarafoods.db.AppDatabase;
import com.ei.kalavarafoods.model.api.Product;
import com.ei.kalavarafoods.model.api.SearchResult;
import com.ei.kalavarafoods.network.ApiInterface;
import com.ei.kalavarafoods.network.RetrofitClient;
import com.rx2androidnetworking.Rx2AndroidNetworking;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ULLAS BABU on 23-Mar-18.
 */

public class SearchViewModel extends AndroidViewModel {

    public MutableLiveData<List<Product>> liveDataProductList = new MutableLiveData<>();
    public MutableLiveData<List<Brand>> liveDataBrands = new MutableLiveData<>();
    public MutableLiveData<Boolean> liveDataIsLoading = new MutableLiveData<>();

    AppDatabase mDb;
    SessionManager mSessionManager;
    ApiInterface mApiInterface, mApiRxInterface;
    private AppDatabase mAppDatabase;

    private List<Product> products;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mDb = AppDatabase.getDatabase(application);
        mSessionManager = new SessionManager(application);
        mApiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);
        mApiRxInterface = RetrofitClient.getRxRetrofit().create(ApiInterface.class);
        mAppDatabase = AppDatabase.getDatabase(application);
    }

    public void searchProducts(String keyword){
        if (keyword.length()>=3) {
            products = new ArrayList<>();
            String userId = mSessionManager.getUserId();
            JSONObject searchJson = new JSONObject();
            try {
                searchJson.put("keyword", keyword);
                searchJson.put("userid", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            liveDataIsLoading.setValue(true);
            mApiInterface.searchProducts(searchJson.toString()).enqueue(new Callback<SearchResult>() {
                @Override
                public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                    liveDataProductList.setValue(response.body().getProduct());
                    insertProductsIntoDb(liveDataProductList.getValue());

                }

                @Override
                public void onFailure(Call<SearchResult> call, Throwable t) {

                }
            });
        }
    }

    private void insertProductsIntoDb(List<Product> productList) {
        List<ProductEntity> productEntityList = Common.convertProductToProductEntity(productList);
        Completable.fromAction(() -> mAppDatabase.productDao().insertProducts(productEntityList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
//                        liveDataIsLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    public String getProductOrderQuantity(String productId){
        return mAppDatabase.productDao().getOrderQuantityById(productId);
    }


    public Observable<SearchResult> rxSearchProducts(String keyword){
        String userId = mSessionManager.getUserId();
//        JSONObject searchJson = new JSONObject();
//        try {
//            searchJson.put("keyword", keyword);
//            searchJson.put("userid", userId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return Rx2AndroidNetworking.post("http://extantinfotech.com/KalavaraFoods/shopmob/search")
                .addBodyParameter("userid", userId)
                .addBodyParameter("keyword", keyword)
                .build()
                .getObjectObservable(SearchResult.class);
    }


    public LiveData<List<ProductEntity>> getCartItems() {
        return mDb.productDao().getItemsAddedToCartAsync();
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

    public void getBrands() {
        liveDataIsLoading.setValue(true);
        mApiInterface.getBrands().enqueue(new Callback<BrandItem>() {
            @Override
            public void onResponse(Call<BrandItem> call, retrofit2.Response<BrandItem> response) {
                if (response.body() != null) {
                    List<Brand> brandList = response.body().getBrands();
                    liveDataBrands.setValue(brandList);
                    liveDataIsLoading.setValue(false);
//                    mRvBrandNames.setAdapter(new SearchResultsActivity.RvBrandsAdapter(SearchResultsActivity.this, brandList));
                }
            }

            @Override
            public void onFailure(Call<BrandItem> call, Throwable t) {
                liveDataIsLoading.setValue(false);
            }
        });
    }
}
