package com.lm.yandexapi.models

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable

@Immutable
@Stable
data class Song(
    val name: String = "",
    val length: String = "",
    val path: String = "",
    val folder: String = ""
)