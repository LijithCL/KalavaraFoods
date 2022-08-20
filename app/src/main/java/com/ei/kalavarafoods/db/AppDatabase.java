package com.ei.kalavarafoods.db;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.ei.kalavarafoods.db.dao.CategoryDao;
import com.ei.kalavarafoods.db.dao.MainCategoryDao;
import com.ei.kalavarafoods.db.dao.NotificationDao;
import com.ei.kalavarafoods.db.dao.OrderDao;
import com.ei.kalavarafoods.db.dao.ProductDao;
import com.ei.kalavarafoods.model.database.CategoryEntity;
import com.ei.kalavarafoods.model.database.MainCategoryEntity;
import com.ei.kalavarafoods.model.database.NotificationEntity;
import com.ei.kalavarafoods.model.database.OrderEntity;
import com.ei.kalavarafoods.model.database.ProductEntity;

/**
 * Created by ULLAS BABU on 01-Feb-18.
 */
@Database(entities = {
        ProductEntity.class,
        OrderEntity.class,
        CategoryEntity.class,
        MainCategoryEntity.class,
        NotificationEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract OrderDao orderDao();
    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();
    public abstract MainCategoryDao mainCategoryDao();
    public abstract NotificationDao notificationDao();

    public static AppDatabase getDatabase(Context context){
        if (INSTANCE != null){
            return INSTANCE;
        }else {
            INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "KalavaraFoods DB")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            return INSTANCE;
        }
    }

    public static void destroyDatabase(){
        INSTANCE = null;
    }
}
