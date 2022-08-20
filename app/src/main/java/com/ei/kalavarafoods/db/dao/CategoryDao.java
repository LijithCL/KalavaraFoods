package com.ei.kalavarafoods.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.ei.kalavarafoods.model.database.CategoryEntity;

import java.util.List;

/**
 * Created by ULLAS BABU on 19-Feb-18.
 */

@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<CategoryEntity> categoryEntities);

    @Query("SELECT * FROM CategoryEntity WHERE categoryMain = :categoryMain")
    List<CategoryEntity> getCategories(String categoryMain);
}
