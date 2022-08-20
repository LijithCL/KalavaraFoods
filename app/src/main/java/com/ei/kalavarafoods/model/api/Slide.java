
package com.ei.kalavarafoods.model.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Slide {

    @SerializedName("slid")
    @Expose
    private String slid;

    public String getSlid() {
        return slid;
    }

    public void setSlid(String slid) {
        this.slid = slid;
    }

}
