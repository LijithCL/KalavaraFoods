package com.ei.kalavarafoods.model.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * Created by ULLAS BABU on 09-Feb-18.
 */
@Entity
public class ProductEntity {

    @NonNull
    @PrimaryKey()
    private String productId;
    private String productName;
    private String productSku;
    private String productCategory;
    private String productBrand;
    private String productSize;
    private String productUnit;
    private String productUnitprice;
    private String productImage;
    private String productDescription;
    private String ingredients;
    private String howToUse;
    private String productCreatedat;
    private String searchKey;
    private String productTaxid;
    private String productStatus;
    private String productOfferprice = "noOffer";
    private String productWish;
    private String productOrderQuantitiy = "0";

    @NonNull
    public String getProductId() {
        return productId;
    }

    public void setProductId(@NonNull String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(String productUnit) {
        this.productUnit = productUnit;
    }

    public String getProductUnitprice() {
        return productUnitprice;
    }

    public void setProductUnitprice(String productUnitprice) {
        this.productUnitprice = productUnitprice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getHowToUse() {
        return howToUse;
    }

    public void setHowToUse(String howToUse) {
        this.howToUse = howToUse;
    }

    public String getProductCreatedat() {
        return productCreatedat;
    }

    public void setProductCreatedat(String productCreatedat) {
        this.productCreatedat = productCreatedat;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getProductTaxid() {
        return productTaxid;
    }

    public void setProductTaxid(String productTaxid) {
        this.productTaxid = productTaxid;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getProductOfferprice() {
        return productOfferprice;
    }

    public void setProductOfferprice(String productOfferprice) {
        this.productOfferprice = productOfferprice;
    }

    public String getProductWish() {
        return productWish;
    }

    public void setProductWish(String productWish) {
        this.productWish = productWish;
    }

    public String getProductOrderQuantitiy() {
        return productOrderQuantitiy;
    }

    public void setProductOrderQuantitiy(String productOrderQuantitiy) {
        this.productOrderQuantitiy = productOrderQuantitiy;
    }
}
