package com.lm.yandexapi.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
@Stable
data class Folder(
    val path: String = "",
    val date: String = "",
    val key: String = "",
    @Stable
    var listSongs: List<Song> = emptyList()
)