package com.umaia.movesense.data.network


import com.umaia.movesense.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteDataSource {
    companion object {
        private const val BASE_URL = "http://161.35.169.57:3000/api/"
    }

    fun <Api> buildApi(
        api: Class<Api>

    ): Api {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(
                OkHttpClient.Builder().also { client ->
                    client.connectTimeout(2, TimeUnit.MINUTES); // connect timeout
                    client.writeTimeout(2,TimeUnit.MINUTES)
                    client.readTimeout(2, TimeUnit.MINUTES)

                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                }.build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}