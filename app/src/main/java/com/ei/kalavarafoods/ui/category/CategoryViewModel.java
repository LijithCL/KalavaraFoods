package com.ei.kalavarafoods.ui.category;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import android.util.Log;

import com.ei.kalavarafoods.db.AppDatabase;
import com.ei.kalavarafoods.model.api.Product;
import com.ei.kalavarafoods.model.api.ProductItem;
import com.ei.kalavarafoods.model.database.CategoryEntity;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.network.ApiInterface;
import com.ei.kalavarafoods.network.RetrofitClient;
import com.ei.kalavarafoods.utils.Common;
import com.ei.kalavarafoods.utils.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends AndroidViewModel {
    private SessionManager mSessionManager;
    private ApiInterface mApiInterface;
    private AppDatabase mAppDatabase;

    public MutableLiveData<Boolean> liveDataIsLoading = new MutableLiveData<>();
    public List<String> tabIdsList = new ArrayList<>();

    public int fragmentPageNo;

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        mSessionManager = new SessionManager(application);
        mApiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);
        mAppDatabase = AppDatabase.getDatabase(application);
    }

    public void getProducts(String categoryMain){
        String userId = mSessionManager.getUserId();
        Map<String, String> params = new HashMap<>();
        params.put("main_categoryid", categoryMain);
        params.put("userid", userId);

        liveDataIsLoading.setValue(true);
        mApiInterface.getProducts(params).enqueue(new Callback<ProductItem>() {
            @Override
            public void onResponse(Call<ProductItem> call, Response<ProductItem> response) {
                List<Product> productList = response.body().getProduct();
                Log.i("CheckProductd>>>", productList.get(0).getProductName());
                insertProductsIntoDb(productList);
            }

            @Override
            public void onFailure(Call<ProductItem> call, Throwable t) {
                liveDataIsLoading.setValue(false);
            }
        });
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
                        liveDataIsLoading.setValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }

    public LiveData<List<ProductEntity>> getCartItems(){
        return mAppDatabase.productDao().getItemsAddedToCartAsync();
    }

    public List<CategoryEntity> getSubcategoriesFromDb(String categoryMain){
        return  mAppDatabase.categoryDao().getCategories(categoryMain);
    }
}
