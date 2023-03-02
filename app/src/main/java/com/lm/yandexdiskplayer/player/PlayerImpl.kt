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
    private val context: Context,
    private val playerUiStates: PlayerUiStates
) : Player {

    @Stable
    var player: MediaPlayer? = null

    @Stable
    var playList: List<Song> = emptyList()

    private var timeProgressJob: Job = Job().apply { cancel() }

    override fun playSong() {
        playerUiStates.muteStates()
        releasePlayer()
        player = MediaPlayer()
        tryCatch({
            getUrl {
                player?.apply {
                    setDataSource(it)
                    prepareAsync()
                    setOnPreparedListener {
                        setStart()
                        playerUiStates.unMuteStates()
                    }
                    setOnCompletionListener { autoplayNext() }
                }
            }
        })
    }

    override fun playPlaylist(song: Song, pathsList: List<Song>) {
        playerUiStates.showPlayingCard()
        playerUiStates.setSongInPlayingCard(song)
        playList = pathsList
        playSong()
    }

    override fun autoplayNext(): Song? = with(playList) {
        checkNextIndex(1).apply {
            if (this == null) {
                releasePlayer()
            }
        }?.apply {
            playerUiStates.setSongInPlayingCard(this)
            playSong()
        }
    }

    override fun playPrevSong(): Song? = with(playList) {
        nextSong(-1)?.apply { playerUiStates.setSongInPlayingCard(this) }
    }

    override fun playNextSong(): Song? = with(playList) {
        nextSong(1)?.apply { playerUiStates.setSongInPlayingCard(this) }
    }

    override fun releasePlayer() {
        player?.apply {
            timeProgressJob.cancel()
            tryCatch({
                stop()
                release()
            }
            )
            playerUiStates.clearInfo()
            playerUiStates.playerState = PlayerState.NULL
        }
    }

    private fun List<Song>.nextSong(nextOrPrev: Int) = checkNextIndex(nextOrPrev)?.apply {
        tryCatch({
            if (playerUiStates.playerState == PlayerState.PLAYING) playSong()
            else releasePlayer()
        })
    }

    private fun List<Song>.checkNextIndex(nextOrPrev: Int) = getOrNull(
        indexOf(playerUiStates.nowPlayingSong) + nextOrPrev
    )

    override fun playOrPause() {
        player?.apply {
            tryCatch({
                when (playerUiStates.playerState) {
                    PlayerState.PLAYING -> setPause()
                    PlayerState.PAUSE -> setStart()
                    PlayerState.NULL -> playSong()
                    else -> Unit
                }
            })
        }
    }

    private fun setPause() {
        player?.apply {
            playerUiStates.playerState = PlayerState.PAUSE
            timeProgressJob.cancel()
            pause()
        }
    }

    private fun setStart() {
        player?.apply {
            playerUiStates.durationSong = duration.getSeconds
            playerUiStates.playerState = PlayerState.PLAYING
            startTimeProgress()
            start()
        }
    }

    private fun getUrl(onGet: (String) -> Unit) =
        CoroutineScope(Main).launch {
            fetch(playerUiStates.nowPlayingSong.path, context.getToken).startRequest().collect {
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

@Composable
fun rememberPlayer(
    playerUiStates: PlayerUiStates,
    context: Context = LocalContext.current
): Player =
    remember { PlayerImpl(context, playerUiStates) }

