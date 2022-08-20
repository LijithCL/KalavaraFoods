package com.ei.kalavarafoods.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.ei.kalavarafoods.db.AppDatabase;
import com.ei.kalavarafoods.model.database.ProductEntity;

import java.util.List;

import io.reactivex.Completable;

/**
 * Created by ULLAS BABU on 22-Mar-18.
 */

public class DataRepository {
    private static DataRepository INSTANCE;
    private static AppDatabase mDb;

    private DataRepository(){
    }

    public static DataRepository getInstance(Application application){
        if (INSTANCE != null){
            return INSTANCE;
        }else {
            mDb = AppDatabase.getDatabase(application);
            return INSTANCE = new DataRepository();
        }
    }

    public Completable insertToProductsEntity(List<ProductEntity> productEntityList){
        return Completable.fromAction(() -> mDb.productDao().insertProducts(productEntityList));
    }

    public LiveData<List<ProductEntity>> getProductsInCart(){
        return mDb.productDao().getItemsAddedToCartAsync();
    }


}
