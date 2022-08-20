
package com.ei.kalavarafoods.model.api;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BrandItem {

    @SerializedName("alert")
    @Expose
    private String alert;
    @SerializedName("brands")
    @Expose
    private List<Brand> brands = null;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

}
