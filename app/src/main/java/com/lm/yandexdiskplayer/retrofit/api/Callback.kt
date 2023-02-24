package com.lm.yandexdiskplayer.retrofit.api

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Callback {
    suspend fun <T> Call<T>.startRequest() = callbackFlow<LoadingResource<T>> {
        enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                response.body()?.apply {
                    if (response.isSuccessful) trySendBlocking(LoadingResource.Success(this))
                    else trySendBlocking(LoadingResource.Exception(response.errorBody()))
                }
            }

            override fun onFailure(call: Call<T>, throwable: Throwable) {
                trySendBlocking(LoadingResource.Failure(throwable))
            }
        })
        awaitClose { cancel() }
    }.flowOn(IO)
}