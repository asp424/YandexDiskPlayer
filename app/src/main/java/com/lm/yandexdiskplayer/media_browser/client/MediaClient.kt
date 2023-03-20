package com.lm.yandexdiskplayer.media_browser.client

import android.content.ComponentName
import android.os.Build
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import com.lm.yandexdiskplayer.MainActivity
import com.lm.yandexdiskplayer.media_browser.service.MediaService
import com.lm.yandexdiskplayer.ui.states.ControllerUiStates

@RequiresApi(Build.VERSION_CODES.O)
class MediaClient(
    private val mainActivity: MainActivity,
    val controllerUiStates: ControllerUiStates
) {
    var mediaController: MediaControllerCompat? = null

    val controllerCallback by lazy {
        object : MediaControllerCompat.Callback() {

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
                controllerUiStates.setMetadata(metadata)
                controllerUiStates.timeProgress = 0f
            }

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                controllerUiStates.startPlayMetadata(state, mediaController)
            }

            override fun onSessionDestroyed() {
                super.onSessionDestroyed()
                mediaBrowser.disconnect()
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
                        mainActivity, MediaControllerCompat(mainActivity, token)
                    )
                }
                mediaController = MediaControllerCompat.getMediaController(mainActivity).apply {
                    registerCallback(controllerCallback)
                }
                mediaBrowser.subscribe("root", controllerUiStates)
                controllerUiStates.initStates(mediaController)
            }

            override fun onConnectionSuspended() {
            }

            override fun onConnectionFailed() {
            }
        }
    }
}