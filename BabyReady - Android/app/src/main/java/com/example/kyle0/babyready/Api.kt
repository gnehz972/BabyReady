package com.example.kyle0.babyready

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


data class PingResponse(
    val status: String
)

data class PredictResponse(
    val predict: String
)

interface Api {
    @GET("/api/ping")
    suspend fun ping(): PingResponse

    @Multipart
    @POST("/api/predict")
    suspend fun predict(@Part audio: MultipartBody.Part): PredictResponse
}

object RetrofitApi {
    private const val BASE_URL = "http://192.168.1.15:8000/"

    val instance: Api by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().addInterceptor(logging).build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}
