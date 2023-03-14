package com.lm.yandexdiskplayer.player

import androidx.compose.runtime.Stable
import com.lm.yandexapi.models.Folder
import com.lm.yandexapi.models.Song

@Stable
interface ControllerUiStates {

    var nowPlayingSong: Song
    var isPlayingCardVisible: Boolean
    var playerState: PlayerState
    var timeProgress: Float
    var timeTextProgress: String
    var durationSong: String
    var enableNext: Boolean
    var enablePrev: Boolean
    var enableSlider: Boolean
    var enablePlay: Boolean
    var foldersList: List<Folder>
    var columnVisible: Boolean

    fun muteStates()

    fun unMuteStates()

    fun clearInfo()

    fun hidePlayingCard()

    fun showPlayingCard()

    fun setSongInPlayingCard(song: Song)
}