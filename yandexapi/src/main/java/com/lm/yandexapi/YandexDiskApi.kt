package com.lm.yandexapi

import android.content.Context
import com.lm.core.utils.YandexAuthToken
import com.lm.core.utils.getToken
import com.lm.core.utils.preferences
import com.lm.core.utils.readValue
import com.yandex.disk.rest.Credentials
import com.yandex.disk.rest.DownloadListener
import com.yandex.disk.rest.ResourcesArgs
import com.yandex.disk.rest.RestClient
import java.io.File
import java.io.OutputStream

val Context.getPlayList
    get() = RestClient(credential)
        .getFlatResourceList(ResourcesArgs.Builder().setMediaType("audio").setLimit(2000).build())
        .items.filter { it.name.endsWith(".mp3") || it.name.endsWith(".wav") }

val Context.credential get() = Credentials("user", getToken)

fun Context.download(path: String, downloadListener: DownloadListener)
= RestClient(credential).downloadFile(path, downloadListener)

fun downloadListener(file: File) = object :DownloadListener(){
    override fun getOutputStream(append: Boolean): OutputStream = file.outputStream()
}






