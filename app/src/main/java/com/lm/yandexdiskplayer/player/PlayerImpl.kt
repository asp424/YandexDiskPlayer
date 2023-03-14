package com.lm.yandexdiskplayer.player

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.lm.core.getSeconds
import com.lm.core.tryCatch
import com.lm.core.utils.getToken
import com.lm.yandexapi.folders
import com.lm.yandexapi.models.Song
import com.lm.yandexdiskplayer.retrofit.api.Callback.startRequest
import com.lm.yandexdiskplayer.retrofit.api.LoadingResource
import com.lm.yandexdiskplayer.retrofit.api.fetch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlayerImpl(
    private val context: Context
) : Player {

    override var player: MediaPlayer? = null

    override var currentSong: Song? = null

    override var currentPlaylist: List<Song> = emptyList()

    private var timeProgressJob: Job = Job().apply { cancel() }

    override fun playSong(onPrepare: () -> Unit) {
        releasePlayer()
        player = MediaPlayer()
        tryCatch({
            getUrl {
                player?.apply {
                    setDataSource(it)
                    prepareAsync()
                    setOnPreparedListener { start(); onPrepare() }
                    setOnCompletionListener { autoplayNext() }
                }
            }
        })
    }

    override fun playPlaylist(song: Song, pathsList: List<Song>) {
        currentPlaylist = pathsList
        currentSong = song
    }



    override fun releasePlayer() {
        player?.apply {
            timeProgressJob.cancel()
            tryCatch({
                stop()
                release()
            }
            )
        }
    }

    private fun List<Song>.nextSong(nextOrPrev: Int) = checkNextIndex(nextOrPrev)?.apply {
        currentSong = currentPlaylist[indexOf(currentSong) + nextOrPrev]
        tryCatch({ playSong{} })
    }

    private fun List<Song>.checkNextIndex(nextOrPrev: Int) = getOrNull(
        indexOf(currentSong) + nextOrPrev
    )

    override fun autoplayNext(): Song? = with(currentPlaylist) {
        checkNextIndex(1).apply {
            if (this == null) {
                releasePlayer()
            }
        }?.apply {
            playerUiStates.setSongInPlayingCard(this)
            playSong{}
        }
    }

    override fun playPrevSong(): Song? = with(currentPlaylist) {
        nextSong(-1)
    }

    override fun playNextSong(): Song? = with(currentPlaylist) {
        nextSong(1)
    }

    override fun pause() { player?.apply { tryCatch({ pause() }) } }

    private fun getUrl(onGet: (String) -> Unit) =
        CoroutineScope(Main).launch {
            fetch(currentSong!!.path, context.getToken).startRequest().collect {
                if (it is LoadingResource.Success) onGet(it.data.href)
            }
        }

    private fun startTimeProgress() {
        player?.apply {
            timeProgressJob = CoroutineScope(IO).launch {
                while (isActive && playerUiStates.playerState == PlayerState.PLAYING) {
                    with((1f / duration.toFloat()) * currentPosition.toFloat()) {
                        playerUiStates.timeProgress = if (!isNaN()) this else 0f
                    }
                    playerUiStates.timeTextProgress = currentPosition.getSeconds
                    delay(10)
                }
            }
        }
    }

    override fun timeProgress(newTime: Float) {
        player?.apply {
            timeProgressJob.cancel()
            playerUiStates.timeProgress = newTime
            playerUiStates.timeTextProgress = duration.getTextProgress.getSeconds
        }
    }

    private val Int.getTextProgress get() = (this / (1f / playerUiStates.timeProgress)).toInt()

    override fun onSliderMove() {
        player?.apply { seekTo(duration.getTextProgress) }
        startTimeProgress()
    }
}

val playerUiStates: ControllerUiStates by lazy { ControllerUiStatesImpl() }


