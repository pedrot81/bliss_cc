package com.pdt.blissrecruitment.connector.entities;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by pdt on 27/07/2017.
 */

public class Status {
    private static final String OK = "OK";
    @SerializedName("status")
    @Expose
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return !TextUtils.isEmpty(status) && OK.equals(status);
    }

}
