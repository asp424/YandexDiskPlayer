package com.lm.yandexdiskplayer.player

import android.content.Context
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import androidx.annotation.RequiresApi
import com.lm.core.tryCatch
import com.lm.core.utils.getToken
import com.lm.yandexapi.models.Song
import com.lm.yandexdiskplayer.retrofit.api.Callback.startRequest
import com.lm.yandexdiskplayer.retrofit.api.LoadingResource
import com.lm.yandexdiskplayer.retrofit.api.fetch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class Player(
    private val context: Context
) {

    var isFree = true

    private val metadataBuilder = MediaMetadataCompat.Builder()

    var player: MediaPlayer? = null

    var currentSong: Song? = null

    var currentPlaylist: List<Song> = emptyList()

    fun playNew(onPrepare: MediaMetadataCompat.() -> Unit, onCompletion: () -> Unit) =
        prepare(onPrepare = {
            player?.apply {
                tryCatch({
                    start(); onPrepare(it)
                })
            }
        }, { onCompletion() })

    fun prepareNew(onPrepare: (MediaMetadataCompat) -> Unit) = prepare(onPrepare = {
        onPrepare(it)
    }) {}

    fun next(isPlayAble: MediaMetadataCompat.() -> Unit) {
        currentPlaylist.getOrNull(currentPlaylist.indexOf(currentSong) + 1)?.apply {
            currentSong = this
            isPlayAble(constructMetadata())
        }
    }

    fun prev(isPlayAble: MediaMetadataCompat.() -> Unit) {
        currentPlaylist.getOrNull(currentPlaylist.indexOf(currentSong) - 1)?.apply {
            currentSong = this
            isPlayAble(constructMetadata())
        }
    }

    private fun prepare(onPrepare: (MediaMetadataCompat) -> Unit, onCompletion: () -> Unit) {
        isFree = false
        releasePlayer()
        player = MediaPlayer()
        tryCatch({
            getUrl {
                player?.apply {
                    setDataSource(it)
                    prepareAsync()
                    setOnPreparedListener { onPrepare(constructMetadata()); isFree = true }
                    setOnCompletionListener { onCompletion(); isFree = true }
                }
            }
        })
    }

    fun playAfterPause(onPlay: (Long) -> Unit) {
        player?.apply {
            tryCatch({
                start()
                onPlay(currentPosition.toLong())
            })
        }
    }

    fun loadSongs(song: Song, pathsList: List<Song>) {
        currentPlaylist = pathsList
        currentSong = song
    }

    fun releasePlayer() {
        player?.apply { tryCatch({ stop(); release() }) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun seekTo(pos: Long) {
        tryCatch({ player?.seekTo(pos, MediaPlayer.SEEK_NEXT_SYNC) })
    }

    fun pause(onPause: (Long) -> Unit) {
        player?.apply { tryCatch({ pause(); onPause(currentPosition.toLong()) }) }
    }

    private fun getUrl(onGet: (String) -> Unit) =
        CoroutineScope(Main).launch {
            fetch(currentSong!!.path, context.getToken).startRequest().collect {
                if (it is LoadingResource.Success) onGet(it.data.href)
            }
        }

    private fun constructMetadata() = metadataBuilder
        .putText(MediaMetadata.METADATA_KEY_TITLE, currentSong?.name)
        .putText(MediaMetadata.METADATA_KEY_ARTIST, currentSong?.folder)
        .putLong(MediaMetadata.METADATA_KEY_DURATION, player?.duration?.toLong() ?: 0L)
        .build()
}



