
package com.ei.kalavarafoods.model.api;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserOrdersItem {

    @SerializedName("alert")
    @Expose
    private String alert;
    @SerializedName("history")
    @Expose
    private List<UserOrder> userOrders = null;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public List<UserOrder> getUserOrder() {
        return userOrders;
    }

    public void setUserOrder(List<UserOrder> userOrder) {
        this.userOrders = userOrder;
    }

}
