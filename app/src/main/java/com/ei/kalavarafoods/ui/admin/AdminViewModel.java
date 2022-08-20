package com.ei.kalavarafoods.ui.admin;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.annotation.NonNull;

import com.ei.kalavarafoods.db.AppDatabase;
import com.ei.kalavarafoods.model.database.OrderEntity;
import com.ei.kalavarafoods.model.api.Order;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ULLAS BABU on 13-Feb-18.
 */

public class AdminViewModel extends AndroidViewModel {
    private LiveData<List<OrderEntity>> mOrderListLiveData = new MutableLiveData<>();
    private AppDatabase mDb;
    private List<OrderEntity> mOrderEntities;

    public AdminViewModel(@NonNull Application application) {
        super(application);
        mDb = AppDatabase.getDatabase(application);
    }


    public Flowable<List<OrderEntity>> getOrderListLiveData(final String status){

        return mDb.orderDao().getOrderAsPerStatus(status);
    }

    public void insertOrders(List<Order> orderList){
        List<OrderEntity> orderEntityList = orderListToEntity(orderList);
//        mOrderEntities = orderEntityList;

        insertOrdersToDb(orderEntityList)
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

    private List<OrderEntity> orderListToEntity(List<Order> orders) {
        List<OrderEntity> orderEntityList = new ArrayList<>();

        for (Order order : orders){
            OrderEntity orderEntity = new OrderEntity();

            orderEntity.setAddress(order.getAddress());
            orderEntity.setAddressCity(order.getAddressCity());
            orderEntity.setAddressLandmark(order.getAddressLandmark());
            orderEntity.setAddressType(order.getAddressType());
            orderEntity.setAddressPincode(order.getAddressPincode());
            orderEntity.setAddrressState(order.getAddrressState());
            orderEntity.setOrderAmount(order.getOrderAmount());
            orderEntity.setOrderDeliveryDate(order.getOrderDeliveryDate());
            orderEntity.setOrderId(order.getOrderId());
            orderEntity.setOrderDeliveryTime(order.getOrderDeliveryTime());
            orderEntity.setOrderTime(order.getOrderTime());
            orderEntity.setStatus(order.getStatus());
            orderEntity.setUsername(order.getUsername());

            orderEntityList.add(orderEntity);
        }
        return orderEntityList;
    }

    private Completable insertOrdersToDb(List<OrderEntity> orderEntityList){
        return Completable.fromAction(() -> {
            for (OrderEntity orderEntity: orderEntityList){
                mDb.orderDao().insertOrder(orderEntity);
            }
        });
    }
}
