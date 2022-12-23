package com.umaia.movesense.data.network

import com.umaia.movesense.data.responses.*
import com.umaia.movesense.data.responses.studies_response.OptionsResponse
import com.umaia.movesense.data.responses.studies_response.QuestionTypesResponse
import com.umaia.movesense.data.responses.studies_response.QuestionsOptionsResponses
import com.umaia.movesense.data.responses.studies_response.StudiesResponse
import retrofit2.http.*

interface ServerApi {
    @GET("questionsTypes")
    suspend fun getQuestionTypes(
        @Header("Authorization") authToken: String
    ): QuestionTypesResponse

    @GET("options")
    suspend fun getOptions(
        @Header("Authorization") authToken: String
    ): OptionsResponse

    @GET("questions/options")
    suspend fun getQuestionsOptions(
        @Header("Authorization") authToken: String
    ): QuestionsOptionsResponses


    @GET("studies/allInfo/{userId}")
    suspend fun getStudies(
        @Path("userId") userId: String, @Header("Authorization") authToken: String
    ): StudiesResponse


    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("addAccData")
    suspend fun addAccData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken: String
    ): UploadAccRespose

    @FormUrlEncoded
    @POST("addGyroData")
    suspend fun addGyroData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken: String
    ): UploadGyroRespose

    @FormUrlEncoded
    @POST("addMagnData")
    suspend fun addMagnData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken: String
    ): UploadMagnRespose

    @FormUrlEncoded
    @POST("addECGData")
    suspend fun addECGData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken: String
    ): UploadECGRespose

    @FormUrlEncoded
    @POST("addHrData")
    suspend fun addHrData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken: String
    ): UploadHrRespose

    @FormUrlEncoded
    @POST("addTempData")
    suspend fun addTempData(
        @Field("jsonString") jsonString: String,
        @Header("Authorization") authToken: String
    ): UploadTempRespose
}
