package com.lm.yandexdiskplayer.player

import androidx.compose.runtime.Stable
import com.lm.yandexapi.models.Song
import kotlin.time.Duration

@Stable
interface Player {

    fun playSong()

    fun playPlaylist(song: Song, pathsList: List<Song>)

    fun autoplayNext(): Song?

    fun playNextSong(): Song?

    fun playPrevSong(): Song?

    fun releasePlayer()

    fun playOrPause()


    fun timeProgress(newTime: Float)

    fun onSliderMove()
}