package com.lm.yandexdiskplayer.retrofit.api

import okhttp3.ResponseBody


sealed class LoadingResource<out T> {

    data class Success<T>(val data: T) : LoadingResource<T>()

    data class Exception<T>(val error: ResponseBody?) : LoadingResource<T>()

    data class Failure<T>(val throwable: Throwable) : LoadingResource<T>()
}