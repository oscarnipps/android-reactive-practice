package com.example.reactivepractice.api.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/* Created by oscar ekesiobi on 3/6/2020*/

public class MemberGetApiBaseResponse {
    @SerializedName("page")
    @Expose
    public Integer page;
    @SerializedName("per_page")
    @Expose
    public Integer perPage;
    @SerializedName("total")
    @Expose
    public Integer total;
    @SerializedName("total_pages")
    @Expose
    public Integer totalPages;
    @SerializedName("data")
    @Expose
    public List<MemberApiRes> data = null;


    public class MemberApiRes {
        @SerializedName("id")
        @Expose
        public Integer id;

        @SerializedName("email")
        @Expose
        public String email;

        @SerializedName("first_name")
        @Expose
        public String firstName;

        @SerializedName("last_name")
        @Expose
        public String lastName;
    }


}
