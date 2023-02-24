package com.lm.yandexapi.models

import androidx.compose.runtime.Immutable

@Immutable
data class Song(
    val name: String,
    val length: String,
    val path: String
)