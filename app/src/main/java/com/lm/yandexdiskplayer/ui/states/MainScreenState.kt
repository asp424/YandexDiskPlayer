package com.lm.yandexdiskplayer.ui.states

import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import com.lm.yandexapi.models.Folder
import com.lm.yandexapi.models.Song
import com.lm.yandexdiskplayer.player.Player

@Stable
interface MainScreenState {

    val Modifier.authByClick: Modifier
    val Modifier.columnModifier: Modifier
    val Modifier.textPathModifier: Modifier
    val Modifier.textDateModifier: Modifier
    val Modifier.rawModifier: Modifier
    val Modifier.cardFolderModifier: Modifier
    val Modifier.textSongsModifier: Modifier
    val Modifier.boxLogoModifier: Modifier
    val Modifier.playerBarPrevModifier: Modifier
    val Modifier.playerBarNextModifier: Modifier
    val Modifier.playerBarPauseModifier: Modifier
    fun Modifier.cardSongModifier(song: Song): Modifier
    val isAuth: Boolean
    val isExpand: Boolean
    fun LazyListScope.folders(item: @Composable LazyItemScope.(Folder) -> Unit)

    fun onSliderValueChange(): (Float) -> Unit

    fun onSliderValueChangeFinished(): () -> Unit

    fun loadFoldersList()
}


