package com.umaia.movesense.data.network

import com.umaia.movesense.data.responses.*
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ServerApi {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password : String
            ): LoginResponse

    @FormUrlEncoded
    @POST("addAccData")
    suspend fun addAccData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken : String
        ): UploadAccRespose

    @FormUrlEncoded
    @POST("addGyroData")
    suspend fun addGyroData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken : String
    ): UploadGyroRespose

    @FormUrlEncoded
    @POST("addMagnData")
    suspend fun addMagnData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken : String
    ): UploadMagnRespose

    @FormUrlEncoded
    @POST("addECGData")
    suspend fun addECGData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken : String
    ): UploadECGRespose

    @FormUrlEncoded
    @POST("addHrData")
    suspend fun addHrData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken : String
    ): UploadHrRespose

    @FormUrlEncoded
    @POST("addTempData")
    suspend fun addTempData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken : String
    ): UploadTempRespose
}
