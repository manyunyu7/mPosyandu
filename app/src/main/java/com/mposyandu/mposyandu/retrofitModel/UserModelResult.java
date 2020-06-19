package com.mposyandu.mposyandu.retrofitModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModelResult {

    @SerializedName("data")
    @Expose
    private Integer data;
    @SerializedName("msg")
    @Expose
    private Integer msg;

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    public Integer getMsg() {
        return msg;
    }

    public void setMsg(Integer msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "UserModelResult{" +
                "data=" + data +
                ", msg=" + msg +
                '}';
    }
}