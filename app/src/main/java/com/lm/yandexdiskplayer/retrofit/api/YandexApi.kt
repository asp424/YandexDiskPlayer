package com.lm.yandexdiskplayer.retrofit.api

import com.yandex.disk.rest.json.Link
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface Api {
    @GET("/v1/disk/resources/download")
    @Headers("Content-Type: application/json")
    fun getDownloadLink(@Query("path") path: String, @Header("Authorization") authToken: String): Call<Link>
}

val api: Api by lazy {
    Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(Api::class.java)
}

fun fetch(path: String, authToken: String) = api.getDownloadLink(path, authToken)

private const val URL = "https://cloud-api.yandex.net"
