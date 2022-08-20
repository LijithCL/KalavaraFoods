package com.ei.kalavarafoods.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ei.kalavarafoods.model.database.MainCategoryEntity;

import java.util.List;

@Dao
public interface MainCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMainCategories(List<MainCategoryEntity> mainCategoryEntities);

    @Query("SELECT * FROM MainCategoryEntity")
    LiveData<List<MainCategoryEntity>> getMainCategories();
}
