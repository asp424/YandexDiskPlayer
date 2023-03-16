package com.lm.yandexdiskplayer.media_browser.client

import android.content.ComponentName
import android.media.MediaMetadata
import android.os.Build
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import com.lm.core.log
import com.lm.yandexapi.models.Song
import com.lm.yandexdiskplayer.MainActivity
import com.lm.yandexdiskplayer.media_browser.service.MediaService
import com.lm.yandexdiskplayer.player.ControllerUiStates
import com.lm.yandexdiskplayer.player.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class MediaClient(
    private val mainActivity: MainActivity,
    val controllerUiStates: ControllerUiStates
) {
    var mediaController: MediaControllerCompat? = null

    fun buildTransportControls() {
        mediaController = MediaControllerCompat.getMediaController(mainActivity)
            .apply { registerCallback(controllerCallback) }
        mediaBrowser.subscribe("root", controllerUiStates)
        mediaController?.playbackState?.position
    }

    val controllerCallback by lazy {
        object : MediaControllerCompat.Callback() {

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
                controllerUiStates.nowPlayingSong = Song(
                    metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)?:"",
                    folder = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)?:""
                )
            }

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                if(state?.state == PlaybackStateCompat.STATE_PLAYING) {
                    startPlay()
                }
                else timeJob.cancel()
                controllerUiStates.playerState =
                    if(state?.state == PlaybackStateCompat.STATE_PLAYING) {
                        controllerUiStates.unMuteStates()
                        PlayerState.PLAYING
                    }
                    else {
                        PlayerState.PAUSE
                    }
                if(state == null){
                    controllerUiStates.muteStates()
                }
            }

            override fun onSessionDestroyed() {
                super.onSessionDestroyed()
                mediaBrowser.disconnect()
            }

            override fun onSessionReady() {
                super.onSessionReady()
            }
        }
    }

    val mediaBrowser by lazy {
        MediaBrowserCompat(
            mainActivity,
            ComponentName(mainActivity, MediaService::class.java), connectionCallbacks, null
        )
    }

    private val connectionCallbacks: MediaBrowserCompat.ConnectionCallback by lazy {
        object : MediaBrowserCompat.ConnectionCallback() {

            override fun onConnected() {
                mediaBrowser.sessionToken.also { token ->
                    MediaControllerCompat.setMediaController(
                        mainActivity,
                        MediaControllerCompat(mainActivity, token)
                    )
                }
                buildTransportControls()
            }

            override fun onConnectionSuspended() {
            }

            override fun onConnectionFailed() {
            }
        }
    }

    var timeJob: Job = Job().apply { cancel() }
    fun startPlay() {
            timeJob.cancel()
            timeJob = CoroutineScope(IO).launch {
                delay(100)
                while (isActive && mediaController?.playbackState?.state == PlaybackStateCompat.STATE_PLAYING) {
                    with(
                        (1f / mediaController?.metadata?.getLong(
                            MediaMetadata.METADATA_KEY_DURATION
                        )!!.toFloat()) * mediaController!!.playbackState.position.toFloat()
                    ) {
                        controllerUiStates.timeProgress = if (!isNaN()) this else 0f
                    }
                   // controllerUiStates.timeTextProgress = mediaController?.playbackState?.position.getSeconds
                    delay(100)
                }
        }
    }
}