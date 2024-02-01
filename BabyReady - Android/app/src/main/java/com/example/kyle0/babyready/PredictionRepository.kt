package com.example.kyle0.babyready

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class PredictionRepository(
    private val api: Api = RetrofitApi.instance,
) {

    suspend fun ping(): Boolean {
        return try {
            val response = api.ping()
            response.result == "OK"
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    suspend fun predict(audio: File): String {
        return try {
            val requestFile = audio.asRequestBody(MultipartBody.FORM)

            val body = MultipartBody.Part.createFormData("file", audio.name, requestFile)
            val response = api.predict(body)
            response.predict
        } catch (e: Exception) {
            ""
        }
    }
}