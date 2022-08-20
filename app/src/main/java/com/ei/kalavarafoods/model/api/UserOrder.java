
package com.ei.kalavarafoods.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserOrder {

    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("order_userid")
    @Expose
    private String orderUserid;
    @SerializedName("order_delivery_date")
    @Expose
    private String orderDeliveryDate;
    @SerializedName("order_delivery_time")
    @Expose
    private String orderDeliveryTime;
    @SerializedName("order_amount")
    @Expose
    private String orderAmount;
    @SerializedName("order_deliveryaddress")
    @Expose
    private String orderDeliveryaddress;
    @SerializedName("order_time")
    @Expose
    private String orderTime;
    @SerializedName("delivery_charge")
    @Expose
    private String deliveryCharge;
    @SerializedName("status")
    @Expose
    private String status;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderUserid() {
        return orderUserid;
    }

    public void setOrderUserid(String orderUserid) {
        this.orderUserid = orderUserid;
    }

    public String getOrderDeliveryDate() {
        return orderDeliveryDate;
    }

    public void setOrderDeliveryDate(String orderDeliveryDate) {
        this.orderDeliveryDate = orderDeliveryDate;
    }

    public String getOrderDeliveryTime() {
        return orderDeliveryTime;
    }

    public void setOrderDeliveryTime(String orderDeliveryTime) {
        this.orderDeliveryTime = orderDeliveryTime;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderDeliveryaddress() {
        return orderDeliveryaddress;
    }

    public void setOrderDeliveryaddress(String orderDeliveryaddress) {
        this.orderDeliveryaddress = orderDeliveryaddress;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserOrder{" +
                "orderId='" + orderId + '\'' +
                ", orderUserid='" + orderUserid + '\'' +
                ", orderDeliveryDate='" + orderDeliveryDate + '\'' +
                ", orderDeliveryTime='" + orderDeliveryTime + '\'' +
                ", orderAmount='" + orderAmount + '\'' +
                ", orderDeliveryaddress='" + orderDeliveryaddress + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", deliveryCharge='" + deliveryCharge + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
