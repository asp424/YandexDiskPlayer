package com.lm.yandexdiskplayer.player

import android.media.MediaPlayer
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.runtime.Stable
import com.lm.yandexapi.models.Song

@Stable
interface Player {

    fun playNew(onPrepare: MediaMetadataCompat.() -> Unit, onCompletion: () -> Unit)

    fun next(isPlayAble: MediaMetadataCompat.() -> Unit)

    fun prev(isPlayAble: MediaMetadataCompat.() -> Unit)

    fun prepareNew(onPrepare: (MediaMetadataCompat) -> Unit)

    fun playAfterPause(onPlay: (Long) -> Unit)

    fun loadSongs(song: Song, pathsList: List<Song>)

    fun releasePlayer()

    fun seekTo(pos: Long)

    fun pause(onPause: (Long) -> Unit)

    val currentSong: Song?

    val currentPlaylist: List<Song>
}