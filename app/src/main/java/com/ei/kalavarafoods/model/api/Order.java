
package com.ei.kalavarafoods.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("order_time")
    @Expose
    private String orderTime;
    @SerializedName("order_delivery_date")
    @Expose
    private String orderDeliveryDate;
    @SerializedName("order_delivery_time")
    @Expose
    private String orderDeliveryTime;
    @SerializedName("order_amount")
    @Expose
    private String orderAmount;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("address_type")
    @Expose
    private String addressType;
    @SerializedName("activity_address")
    @Expose
    private String address;
    @SerializedName("address_landmark")
    @Expose
    private String addressLandmark;
    @SerializedName("address_pincode")
    @Expose
    private String addressPincode;
    @SerializedName("address_city")
    @Expose
    private String addressCity;
    @SerializedName("addrress_state")
    @Expose
    private String addrressState;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressLandmark() {
        return addressLandmark;
    }

    public void setAddressLandmark(String addressLandmark) {
        this.addressLandmark = addressLandmark;
    }

    public String getAddressPincode() {
        return addressPincode;
    }

    public void setAddressPincode(String addressPincode) {
        this.addressPincode = addressPincode;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }

    public String getAddrressState() {
        return addrressState;
    }

    public void setAddrressState(String addrressState) {
        this.addrressState = addrressState;
    }

}
