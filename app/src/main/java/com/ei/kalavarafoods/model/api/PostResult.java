
package com.ei.kalavarafoods.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostResult {

    @SerializedName("alert")
    @Expose
    private String alert;
    @SerializedName("message")
    @Expose
    private String message;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
