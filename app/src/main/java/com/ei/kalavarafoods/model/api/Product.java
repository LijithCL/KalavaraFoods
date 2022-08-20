
package com.ei.kalavarafoods.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("product_id")
    @Expose
    private String productId;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("product_sku")
    @Expose
    private String productSku;
    @SerializedName("product_category")
    @Expose
    private String productCategory;
    @SerializedName("product_brand")
    @Expose
    private String productBrand;
    @SerializedName("product_size")
    @Expose
    private String productSize;
    @SerializedName("product_unit")
    @Expose
    private String productUnit;
    @SerializedName("product_unitprice")
    @Expose
    private String productUnitprice;
    @SerializedName("product_image")
    @Expose
    private String productImage;
    @SerializedName("product_description")
    @Expose
    private String productDescription;
    @SerializedName("ingredients")
    @Expose
    private String ingredients;
    @SerializedName("how_to_use")
    @Expose
    private String howToUse;
    @SerializedName("product_createdat")
    @Expose
    private String productCreatedat;
    @SerializedName("search_key")
    @Expose
    private Object searchKey;
    @SerializedName("product_taxid")
    @Expose
    private String productTaxid;
    @SerializedName("product_status")
    @Expose
    private String productStatus;
    @SerializedName("product_offerprice")
    @Expose
    private String productOfferprice = "noOffer";
    @SerializedName("product_wish")
    @Expose
    private String productWish;
    @SerializedName("product_orderQuantity")
    @Expose
    private String productOrderQuantitiy = "0";


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
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

    public Object getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(Object searchKey) {
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
