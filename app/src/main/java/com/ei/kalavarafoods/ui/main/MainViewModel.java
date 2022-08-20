package com.ei.kalavarafoods.ui.main;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.ei.kalavarafoods.db.AppDatabase;
import com.ei.kalavarafoods.model.api.Category;
import com.ei.kalavarafoods.model.api.CategoryList;
import com.ei.kalavarafoods.model.api.PostResult;
import com.ei.kalavarafoods.model.api.Product;
import com.ei.kalavarafoods.model.api.ProductItem;
import com.ei.kalavarafoods.model.api.Slide;
import com.ei.kalavarafoods.model.api.SliderData;
import com.ei.kalavarafoods.model.api.SubCategory;
import com.ei.kalavarafoods.model.database.CategoryEntity;
import com.ei.kalavarafoods.model.database.MainCategoryEntity;
import com.ei.kalavarafoods.model.database.ProductEntity;
import com.ei.kalavarafoods.network.ApiInterface;
import com.ei.kalavarafoods.network.RetrofitClient;
import com.ei.kalavarafoods.utils.Common;
import com.ei.kalavarafoods.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
 * Created by ULLAS BABU on 01-Feb-18.
 */

public class MainViewModel extends AndroidViewModel {
    private AppDatabase mAppDatabase;
    private ApiInterface mApiInterface;
    private SessionManager mSessionManager;

    public MutableLiveData<Boolean> liveDataIsLoading = new MutableLiveData<>();
    public MutableLiveData<List<MainCategoryEntity>> liveDataMainCategoriesEntities = new MutableLiveData<>();
    public MutableLiveData<List<String>> liveDataCategoryMainIds = new MutableLiveData<>();
    public MutableLiveData<List<ProductEntity>> liveDataProductEntity = new MutableLiveData<>();
    public MutableLiveData<List<Slide>> liveDataSlideList = new MutableLiveData<>();
    public MutableLiveData<List<Product>> liveDataWishList = new MutableLiveData<>();
    private List<ProductEntity> productEntities;

    public String categoryMainId = "categoryMainId";

    public MainViewModel(@NonNull Application application) {
        super(application);
        mAppDatabase = AppDatabase.getDatabase(getApplication());
        mApiInterface = RetrofitClient.getRetrofit().create(ApiInterface.class);
        mSessionManager = new SessionManager(application);
    }

    public void getCategories(){
        liveDataIsLoading.setValue(true);
        mApiInterface.getCategories().enqueue(new Callback<CategoryList>() {
            @Override
            public void onResponse(Call<CategoryList> call, Response<CategoryList> response) {
                parseData(response.body());
            }
            @Override
            public void onFailure(Call<CategoryList> call, Throwable t) {
                liveDataIsLoading.setValue(false);
            }
        });
    }

    public void parseData(CategoryList categoryList) {
        List<Category> categories = categoryList.getCategories();
        List<CategoryEntity> categoryEntities = new ArrayList<>();
        List<MainCategoryEntity> mainCategoryEntityList = new ArrayList<>();
        List<String> categoryMainIds = new ArrayList<>();
        for (Category category : categories){
            MainCategoryEntity mainCategoryEntity = new MainCategoryEntity();
            mainCategoryEntity.setCategoryImage(category.getCategoryImage());
            mainCategoryEntity.setCategoryTitle(category.getCategoryTitle());
            mainCategoryEntity.setCategorySubTitle(category.getCategorySubtitle());
            mainCategoryEntityList.add(mainCategoryEntity);
            List<SubCategory> subCategories = category.getSubcategory();
            for (SubCategory subCategory: subCategories){
                CategoryEntity categoryEntity = new CategoryEntity();
                categoryEntity.setCategoryTitle(category.getCategoryTitle());
                categoryEntity.setCategoryId(subCategory.getCategoryId());
                categoryEntity.setCategoryMain(subCategory.getCategoryMain());
                String mainCategoryId = subCategory.getCategoryMain();
                if (!categoryMainIds.contains(mainCategoryId)){
                    categoryMainIds.add(mainCategoryId);
                }
                categoryEntity.setCategoryName(subCategory.getCategoryName());
                categoryEntities.add(categoryEntity);
            }
        }
        liveDataCategoryMainIds.setValue(categoryMainIds);
        insertCategoryToDb(categoryEntities);
        insertMainCategoryToDb(mainCategoryEntityList);
        liveDataMainCategoriesEntities.setValue(mainCategoryEntityList);

    }

    private void insertMainCategoryToDb(List<MainCategoryEntity> mainCategoryEntities) {
        Completable.fromAction(() -> mAppDatabase.mainCategoryDao().insertMainCategories(mainCategoryEntities))
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

    private void insertCategoryToDb(List<CategoryEntity> categoryEntities) {
        Completable.fromAction(() -> mAppDatabase.categoryDao().insertCategories(categoryEntities))
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





    public void getSliderData(){
        liveDataIsLoading.setValue(true);
        mApiInterface.getSliderData().enqueue(new Callback<SliderData>() {
            @Override
            public void onResponse(Call<SliderData> call, Response<SliderData> response) {
                liveDataSlideList.setValue(response.body().getSlides());
                liveDataIsLoading.setValue(false);
            }

            @Override
            public void onFailure(Call<SliderData> call, Throwable t) {
                liveDataIsLoading.setValue(false);
            }
        });
    }

    public List<ProductEntity> getProductsAsPerCategoryIdFromDb(String categoryId){
           return mAppDatabase.productDao().getProductsAsPerCategoryId(categoryId);
    }

    public LiveData<List<MainCategoryEntity>> getMainCategories() {
        return mAppDatabase.mainCategoryDao().getMainCategories();
    }

    class CategoryDataAsync extends AsyncTask<String, Void, List<ProductEntity>>{

        @Override
        protected List<ProductEntity> doInBackground(String ... params) {
            List<ProductEntity> productEntities = mAppDatabase.productDao().getProductsAsPerCategoryId(params[0]);
            return productEntities;
        }

        @Override
        protected void onPostExecute(List<ProductEntity> productEntities) {
            liveDataProductEntity.setValue(productEntities);
        }
    }

    public String getProductOrderQuantity(String productId){
        return mAppDatabase.productDao().getOrderQuantityById(productId);
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

    public void removeWish(int adapterPosition, Product productToRemove){
        String userId = mSessionManager.getUserId();
        String productId = productToRemove.getProductId();
        JSONObject removeProductJson = new JSONObject();
        try {
            removeProductJson.put("user_id", userId);
            removeProductJson.put("product_id", productId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        liveDataIsLoading.setValue(true);
        mApiInterface.removeWish(removeProductJson.toString()).enqueue(new Callback<PostResult>() {
            @Override
            public void onResponse(Call<PostResult> call, Response<PostResult> response) {
                liveDataIsLoading.setValue(false);
            }
            @Override
            public void onFailure(Call<PostResult> call, Throwable t) {
                liveDataWishList.getValue().add(adapterPosition, productToRemove);
            }
        });
    }

    public void getWishList(){
        String userId = mSessionManager.getUserId();

        liveDataIsLoading.setValue(true);
        mApiInterface.getWishList(userId).enqueue(new Callback<ProductItem>() {
            @Override
            public void onResponse(Call<ProductItem> call, Response<ProductItem> response) {
                List<Product> productList = response.body().getProduct();
                if (productList != null) {
                    liveDataWishList.setValue(productList);
                    insertProductsIntoDb(productList);
                }
            }

            @Override
            public void onFailure(Call<ProductItem> call, Throwable t) {
                liveDataIsLoading.setValue(false);
            }
        });
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

}
