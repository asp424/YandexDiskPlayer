package com.lm.yandexdiskplayer.player

import androidx.compose.runtime.Stable
import com.lm.yandexapi.models.Song

@Stable
interface PlayerUiStates {

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

    fun muteStates()

    fun unMuteStates()

    fun clearInfo()

    fun hidePlayingCard()
}