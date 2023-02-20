package com.lm.yandexapi

import android.content.Context
import androidx.activity.result.ActivityResult
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk

val Context.yandexDiskSdk
    get() = YandexAuthSdk(this, YandexAuthOptions.Builder(this).build())

fun Context.startAuth() = yandexDiskSdk.createLoginIntent(
    YandexAuthLoginOptions.Builder().build()
)

fun Context.resultHandler(onGetToken: (String) -> Unit, onFailure: (String) -> Unit)
        : (ActivityResult) -> Unit = { result ->
    runCatching { with(result) { yandexDiskSdk.extractToken(resultCode, data) } }
        .onSuccess { onGetToken(it?.value ?: "") }.onFailure { onFailure("null") }
}




