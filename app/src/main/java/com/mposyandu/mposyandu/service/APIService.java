package com.mposyandu.mposyandu.service;

import com.mposyandu.mposyandu.retrofitModel.BalitaModelPost;
import com.mposyandu.mposyandu.retrofitModel.UserModelPost;
import com.mposyandu.mposyandu.retrofitModel.UserModelResult;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    @POST("/user/{id}/register")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<UserModelResult> registerUser(@Path("id") int groupId, @Body UserModelPost userModelPost);

    @POST("/user/{userid}/posyandu/{posyanduid}/baby/register")
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    Call<UserModelResult> registerBalita(@Path("userid") int userid, @Path("posyanduid") int posyanduid, @Body BalitaModelPost balitaModelPost);
}
