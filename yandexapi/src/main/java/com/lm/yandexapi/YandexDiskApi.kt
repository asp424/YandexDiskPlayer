package com.lm.yandexapi

import android.content.Context
import com.lm.core.utils.getToken
import com.lm.yandexapi.models.Folder
import com.lm.yandexapi.models.Song
import com.yandex.disk.rest.Credentials
import com.yandex.disk.rest.DownloadListener
import com.yandex.disk.rest.ResourcesArgs
import com.yandex.disk.rest.RestClient
import com.yandex.disk.rest.json.Resource
import java.io.File
import java.io.OutputStream

val Context.songs
    get() = runCatching {
        RestClient(credential).getFlatResourceList(flatResourcesArgs).items.map { s ->
            Song(s.name, s.size.toString(), if(s.path.path.substringBefore(s.name) == "/")
                s.path.path.replace("/", "/root/") else s.path.path, s.getFolder)
        }.sortedBy { song -> song.folder }.filter { it.name.filterByType }
    }.getOrDefault(emptyList())
val Context.folders
    get() = runCatching {
        val resources = RestClient(credential).getFlatResourceList(flatResourcesArgs).items
        val foldersMap = resources.filter { it.name.filterByType }.groupBy { it.getFolder }
        foldersMap.keys.map {
            with(resources.findFolder(it)) {
                Folder(it, created.toString(), md5.toString(), foldersMap.getSongsList(it))
            }
        }
    }.getOrDefault(emptyList())

private fun Map<String, List<Resource>>.getSongsList(key: String)
= (get(key) ?: emptyList())
    .map { s -> Song(s.name, s.size.toString(), s.path.path, s.getFolder) }
    .sortedBy { song -> song.name }

private fun List<Resource>.findFolder(folderPath: String) =
    find { r -> r.getFolder == folderPath } ?: Resource()

private val Context.credential get() = Credentials("user", getToken)

fun Context.download(path: String, downloadListener: DownloadListener) =
    RestClient(credential).downloadFile(path, downloadListener)

private fun downloadListener(file: File) = object : DownloadListener() {
    override fun getOutputStream(append: Boolean): OutputStream = file.outputStream()
}

val Resource.getFolder
    get() = with(path.path.substringBefore(name)) { if (length > 1) this else "/root/" }

private val flatResourcesArgs
        by lazy { ResourcesArgs.Builder().setMediaType("audio").setLimit(2000).build() }

private val String.filterByType get() = endsWith(".mp3")
        || endsWith(".wav") || endsWith(".flac")








