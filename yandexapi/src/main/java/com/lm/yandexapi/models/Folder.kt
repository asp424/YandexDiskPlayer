package com.lm.yandexapi.models

import androidx.compose.runtime.Immutable

@Immutable
data class Folder(
    val path: String = "",
    val date: String = "",
    val key: String = "",
    var listSongs: List<Song> = emptyList()
)