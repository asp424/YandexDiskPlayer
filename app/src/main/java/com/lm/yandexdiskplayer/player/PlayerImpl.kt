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

class PlayerImpl(
    private val context: Context
) : Player {

    private val metadataBuilder = MediaMetadataCompat.Builder()

    private var player: MediaPlayer? = null

    override var currentSong: Song? = null

    override var currentPlaylist: List<Song> = emptyList()

    override fun playNew(onPrepare: (MediaMetadataCompat) -> Unit) = prepare {
        player?.apply {
            tryCatch({
                start(); onPrepare(it)
            })
        }
    }

    override fun prepareNew(onPrepare: (MediaMetadataCompat) -> Unit) = prepare {
        onPrepare(it)
    }

    private fun prepare(onPrepare: (MediaMetadataCompat) -> Unit) {
        releasePlayer()
        player = MediaPlayer()
        tryCatch({
            getUrl {
                player?.apply {
                    setDataSource(it)
                    prepareAsync()
                    setOnPreparedListener { onPrepare(constructMetadata()) }
                    setOnCompletionListener { autoplayNext() }
                }
            }
        })
    }

    override fun play() {
        player?.apply {
            tryCatch({
                start()
            })
        }
    }

    override fun playAfterPause(onPlay: (Long) -> Unit) {
        player?.apply {
            tryCatch({
                start()
                onPlay(currentPosition.toLong())
            })
        }
    }

    override fun playPlaylist(song: Song, pathsList: List<Song>) {
        currentPlaylist = pathsList
        currentSong = song
    }

    override fun releasePlayer() {
        player?.apply { tryCatch({ stop(); release() }) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun seekTo(pos: Long) {
        tryCatch({ player?.seekTo(pos, MediaPlayer.SEEK_NEXT_SYNC) })
    }

    private fun List<Song>.nextSong(
        state: Int, nextOrPrev: Int, metadata: (MediaMetadataCompat) -> Unit, onPrepare: () -> Unit
    ) = checkNextIndex(nextOrPrev)?.apply {
        currentSong = currentPlaylist[indexOf(currentSong) + nextOrPrev]
        metadata(constructMetadata())
            if (state == 0) prepareNew { onPrepare() }
            else playNew { onPrepare() }
    }

    private fun List<Song>.checkNextIndex(nextOrPrev: Int) = getOrNull(
        indexOf(currentSong) + nextOrPrev
    )

    override fun autoplayNext(): Song? = with(currentPlaylist) {
        checkNextIndex(1).apply { if (this == null) releasePlayer() }?.apply { playNew {} }
    }

    override fun playPrevSong(
        state: Int, metadata: (MediaMetadataCompat) -> Unit, onPrepare: () -> Unit
    ): Song? =
        with(currentPlaylist) {
            nextSong(state, -1, metadata = { metadata(it) }, onPrepare = { onPrepare() })
        }

    override fun playNextSong(
        state: Int,
        metadata: (MediaMetadataCompat) -> Unit, onPrepare: () -> Unit
    ): Song? = with(currentPlaylist) {
        nextSong(state, 1, metadata = { metadata(it) }, onPrepare = { onPrepare() })
    }

    override fun pause(onPause: (Long) -> Unit) {
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



