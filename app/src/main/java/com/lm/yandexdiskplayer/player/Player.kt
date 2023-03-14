package com.lm.yandexdiskplayer.player

import android.media.MediaPlayer
import androidx.compose.runtime.Stable
import com.lm.yandexapi.models.Song

@Stable
interface Player {

    fun playSong(onPrepare: () -> Unit)

    fun playPlaylist(song: Song, pathsList: List<Song>)

    fun autoplayNext(): Song?

    fun playNextSong(): Song?

    fun playPrevSong(): Song?

    fun releasePlayer()

    fun pause()

    fun timeProgress(newTime: Float)

    fun onSliderMove()

    var player: MediaPlayer?

    val currentSong: Song?

    val currentPlaylist: List<Song>
}