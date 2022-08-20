package com.ei.kalavarafoods.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ei.kalavarafoods.model.database.OrderEntity;

import java.util.List;

import io.reactivex.Flowable;

/**
 * Created by ULLAS BABU on 13-Feb-18.
 */
@Dao
public interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrder(OrderEntity orderEntity);

    @Query("SELECT * FROM OrderEntity WHERE status = :status")
    Flowable<List<OrderEntity>> getOrderAsPerStatus(String status);

    @Query("UPDATE OrderEntity SET status = :status WHERE orderId = :orderId")
    void updateOrderStatus(String orderId, String status);
}
