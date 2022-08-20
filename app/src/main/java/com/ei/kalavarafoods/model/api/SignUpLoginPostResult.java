package com.ei.kalavarafoods.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ULLAS BABU on 09-Apr-18.
 */

public class SignUpLoginPostResult {

    @SerializedName("alert")
    @Expose
    private String alert;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("email")
    private String email;
    @SerializedName("userrole")
    @Expose
    private String userrole;
    @SerializedName("msg")
    @Expose
    private String msg;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserrole() {
        return userrole;
    }

    public void setUserrole(String userrole) {
        this.userrole = userrole;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SignUpLoginPostResult{" +
                "alert='" + alert + '\'' +
                ", uid='" + uid + '\'' +
                ", userrole='" + userrole + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
