package com.lm.yandexdiskplayer.media_browser.client

import android.content.ComponentName
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.lm.yandexdiskplayer.MainActivity
import com.lm.yandexdiskplayer.media_browser.service.MediaService
import com.lm.yandexdiskplayer.player.ControllerUiStates
import com.lm.yandexdiskplayer.player.ControllerUiStatesImpl

class MediaClient(private val mainActivity: MainActivity, val controllerUiStates: ControllerUiStates) {

    var mediaController: MediaControllerCompat? = null

    fun buildTransportControls() {
        val mediaControllerCompat = MediaControllerCompat.getMediaController(mainActivity)
        mediaController = mediaControllerCompat
        mediaController?.registerCallback(controllerCallback)
        mediaController?.transportControls?.play()
    }

    val controllerCallback by lazy {
        object : MediaControllerCompat.Callback() {

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {}

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
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
}