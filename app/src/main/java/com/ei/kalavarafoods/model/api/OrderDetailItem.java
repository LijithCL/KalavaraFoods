
package com.ei.kalavarafoods.model.api;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderDetailItem {

    @SerializedName("alert")
    @Expose
    private String alert;
    @SerializedName("orderitemslist")
    @Expose
    private List<OrderDetails> orderDetails = null;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public List<OrderDetails> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetails> orderDetails) {
        this.orderDetails = orderDetails;
    }

}
