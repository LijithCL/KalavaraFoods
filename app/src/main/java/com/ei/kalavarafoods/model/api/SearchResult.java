package com.ei.kalavarafoods.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ULLAS BABU on 03-Apr-18.
 */

public class SearchResult {
    @SerializedName("alert")
    @Expose
    private String alert;
    @SerializedName("products")
    @Expose
    private List<Product> product = null;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }
}
