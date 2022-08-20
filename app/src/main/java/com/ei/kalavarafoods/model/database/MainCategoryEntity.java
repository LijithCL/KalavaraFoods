package com.ei.kalavarafoods.model.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class MainCategoryEntity {

    @NonNull
    @PrimaryKey
    public String categoryTitle;
    public String categorySubTitle;
    public String categoryImage;

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategorySubTitle() {
        return categorySubTitle;
    }

    public void setCategorySubTitle(String categorySubTitle) {
        this.categorySubTitle = categorySubTitle;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }
}
