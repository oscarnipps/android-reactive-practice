package com.example.reactivepractice.api;

import com.example.reactivepractice.api.model.MemberApiPostRes;
import com.example.reactivepractice.api.model.MemberGetApiBaseResponse;
import com.example.reactivepractice.api.model.MemberLoginDetails;
import com.example.reactivepractice.api.model.MemberLoginResponse;
import com.example.reactivepractice.api.model.MemberPostSchema;
import com.example.reactivepractice.api.model.SingleMemberResponse;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface MemberApiService {

    @GET("api/users")
    Single<Response<MemberGetApiBaseResponse>> getMembers();

    @POST("api/login")
    Single<Response< MemberLoginResponse>> loginMember(@Body MemberLoginDetails memberLoginDetails);

    @GET("api/users/2")
    Single<Response<SingleMemberResponse>> getSingleMember();



    @POST("api/users")
    Single<Response<MemberApiPostRes>> pushMemberToServer(@Body MemberPostSchema member);

    @POST("api/users")
    Single<Response<MemberApiPostRes>> logInUser(@Body MemberPostSchema member);
}
