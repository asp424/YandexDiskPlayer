package com.lm.yandexapi

import android.content.Context
import androidx.activity.result.ActivityResult
import com.lm.core.toast
import com.lm.core.utils.YandexAuthToken
import com.lm.core.utils.preferences
import com.lm.core.utils.saveValue
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk

val Context.yandexDiskSdk
    get() = YandexAuthSdk(this, YandexAuthOptions.Builder(this).build())

fun Context.startAuth() = yandexDiskSdk.createLoginIntent(
    YandexAuthLoginOptions.Builder().build()
)

fun Context.resultHandler(onGetToken: () -> Unit, onFailure: (String) -> Unit)
        : (ActivityResult) -> Unit = { result ->
    runCatching { with(result) { yandexDiskSdk.extractToken(resultCode, data) } }
        .onSuccess {
            toast(it?.value)
            preferences.saveValue(YandexAuthToken, it?.value)
            onGetToken()
        }.onFailure { onFailure("null"); toast(it.message) }
}




