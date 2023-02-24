package com.lm.yandexapi

import android.content.Context
import com.lm.core.toast
import com.lm.core.utils.getToken
import com.lm.yandexapi.models.Folder
import com.lm.yandexapi.models.Song
import com.yandex.disk.rest.Credentials
import com.yandex.disk.rest.DownloadListener
import com.yandex.disk.rest.ResourcesArgs
import com.yandex.disk.rest.RestClient
import com.yandex.disk.rest.json.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStream

val Context.folders
    get() = runCatching {
        val resources = RestClient(credential).getFlatResourceList(flatResourcesArgs).items
        val map = resources.filter { it.name.sortByType }.groupBy { it.getFolder }
        map.keys.map {
            val resource = resources.find { r -> r.getFolder == it }
            Folder(it, resource?.created.toString(), resource?.md5.toString(),
                (map[it] ?: emptyList()).map { l ->
                    Song(
                        l.name,
                        l.size.toString(),
                        l.path.path
                    )
                }.sortedBy { s -> s.name }
            )
        }
    }.getOrDefault(emptyList())

val Context.credential get() = Credentials("user", getToken)

fun Context.download(path: String, downloadListener: DownloadListener) =
    RestClient(credential).downloadFile(path, downloadListener)

fun downloadListener(file: File) = object : DownloadListener() {
    override fun getOutputStream(append: Boolean): OutputStream = file.outputStream()
}

private val Resource.getFolder
    get() = with(path.path.substringBefore(name)) { if (length > 1) this else "/root/" }

private val flatResourcesArgs
        by lazy { ResourcesArgs.Builder().setMediaType("audio").setLimit(2000).build() }

private fun Context.folderInfo(path: String) = RestClient(credential)
    .getResources(
        ResourcesArgs.Builder().setPath(path).build()
    ).created.toString()

private val String.sortByType get() = endsWith(".mp3") || endsWith(".wav")








