
package com.ei.kalavarafoods.model.api;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("category_image")
    @Expose
    private String categoryImage;
    @SerializedName("category_title")
    @Expose
    private String categoryTitle;
    @SerializedName("category_subtitle")
    @Expose
    private String categorySubtitle;
    @SerializedName("subcategory")
    @Expose
    private List<SubCategory> subcategory = null;

    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }

    public String getCategorySubtitle() {
        return categorySubtitle;
    }

    public void setCategorySubtitle(String categorySubtitle) {
        this.categorySubtitle = categorySubtitle;
    }

    public List<SubCategory> getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(List<SubCategory> subcategory) {
        this.subcategory = subcategory;
    }

}
