package com.lm.yandexdiskplayer.ui.states

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.lm.yandexapi.models.Folder

@Stable
interface MainScreenState {

    val Modifier.authByClick: Modifier
    val Modifier.columnModifier: Modifier
    val Modifier.textPathModifier: Modifier
    val Modifier.textDateModifier: Modifier
    val Modifier.rawModifier: Modifier
    val Modifier.cardFolderModifier: Modifier
    fun Modifier.cardSongModifier(path: String): Modifier
    val isAuth: Boolean
    val isExpand: Boolean
    fun LazyListScope.folders(item: @Composable LazyItemScope.(Folder) -> Unit)
}


