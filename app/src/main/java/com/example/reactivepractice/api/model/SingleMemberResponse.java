package com.example.reactivepractice.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SingleMemberResponse {

    @SerializedName("data")
    @Expose
    private SingleMember data;

    public SingleMember getData() {
        return data;
    }

    public void setData(SingleMember data) {
        this.data = data;
    }
}
