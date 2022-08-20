package com.ei.kalavarafoods.ui.number_verification.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtpResponse {
    @SerializedName("alert")
    @Expose
    private String alert;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("otp")
    @Expose
    private Integer otp;
    @SerializedName("data")
    @Expose
    private String data;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
