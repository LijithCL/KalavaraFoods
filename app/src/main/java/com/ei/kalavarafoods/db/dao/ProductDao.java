package com.ei.kalavarafoods.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ei.kalavarafoods.model.database.ProductEntity;

import java.util.List;

/**
 * Created by ULLAS BABU on 09-Feb-18.
 */
@Dao
public interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProducts(List<ProductEntity> productEntityList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSingleProduct(ProductEntity productEntity);

    @Query("SELECT * FROM ProductEntity WHERE productCategory = :categoryId")
    List<ProductEntity> getProductsAsPerCategoryId(String categoryId);

    @Query("SELECT * FROM ProductEntity WHERE ProductEntity.productOrderQuantitiy > 0")
    LiveData<List<ProductEntity>> getItemsAddedToCartAsync();

    @Query("SELECT * FROM ProductEntity WHERE ProductEntity.productOrderQuantitiy > 0")
    List<ProductEntity> getItemsAddedToCart();

    @Query("UPDATE ProductEntity SET productOrderQuantitiy = :quantity WHERE productId = :itemId")
    void updateCartItems(String quantity, String itemId);

    @Query("UPDATE ProductEntity SET productOrderQuantitiy = 0")
    void clearCartItems();

    @Query("UPDATE ProductEntity SET productWish = 1 WHERE productId = :productId")
    void addToWish(String productId);

    @Query("UPDATE ProductEntity SET productWish = 0 WHERE productId = :productId")
    void removeFromWish(String productId);

    @Query("SELECT productOrderQuantitiy FROM ProductEntity WHERE productId = :productId")
    String getOrderQuantityById(String productId);
}
