package com.lm.yandexdiskplayer.retrofit.api

import okhttp3.ResponseBody

sealed class Resource<out T> {
	
	data class Success<T>(val data: T) : Resource<T>() {

	}
	
	data class Exception<T>(val error: ResponseBody?) : Resource<T>() {

	}
	
	data class Failure<T>(val throwable: Throwable) : Resource<T>() {

	}
	
	object Loading : Resource<Nothing>() {

	}
}