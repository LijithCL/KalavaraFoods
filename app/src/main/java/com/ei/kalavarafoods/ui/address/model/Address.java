package com.ei.kalavarafoods.ui.address.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("address_id")
    @Expose
    private String addressId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("address_type")
    @Expose
    private String addressType;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("address_pincode")
    @Expose
    private String addressPincode;
    @SerializedName("address_city")
    @Expose
    private String addressCity;
    @SerializedName("addrress_state")
    @Expose
    private String addrressState;
    @SerializedName("address_place")
    @Expose
    private String addressPlace;
    @SerializedName("address_landmark")
    @Expose
    private String addressLandmark;
    @SerializedName("address_status")
    @Expose
    private String addressStatus;
    @SerializedName("address_createdat")
    @Expose
    private String addressCreatedat;

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getAddressPlace() {
        return addressPlace;
    }

    public void setAddressPlace(String addressPlace) {
        this.addressPlace = addressPlace;
    }

    public String getAddressLandmark() {
        return addressLandmark;
    }

    public void setAddressLandmark(String addressLandmark) {
        this.addressLandmark = addressLandmark;
    }

    public String getAddressStatus() {
        return addressStatus;
    }

    public void setAddressStatus(String addressStatus) {
        this.addressStatus = addressStatus;
    }

    public String getAddressCreatedat() {
        return addressCreatedat;
    }

    public void setAddressCreatedat(String addressCreatedat) {
        this.addressCreatedat = addressCreatedat;
    }

}
