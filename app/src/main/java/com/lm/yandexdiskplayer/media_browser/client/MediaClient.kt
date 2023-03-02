package com.lm.yandexdiskplayer.media_browser.client

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.lm.core.log
import com.lm.yandexdiskplayer.MainActivity
import com.lm.yandexdiskplayer.media_browser.service.MediaService

class MediaClient(private val mainActivity: MainActivity) {

    fun buildTransportControls() {
        val mediaController = MediaControllerCompat.getMediaController(mainActivity)
        mediaController.registerCallback(controllerCallback)
        val pbState = mediaController.playbackState.state.log
        pbState.log
        mediaController.playbackInfo.currentVolume.log
    }

    val controllerCallback by lazy {
        object : MediaControllerCompat.Callback() {

            override fun onMetadataChanged(metadata: MediaMetadataCompat?) {}

            override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
                state.log
            }

            override fun onSessionDestroyed() {
                super.onSessionDestroyed()
                mediaBrowser.disconnect()
                "DIS".log
            }

            override fun onSessionReady() {
                super.onSessionReady()
                "READY".log
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
                "suspend".log
            }

            override fun onConnectionFailed() {
                "error".log
            }
        }
    }
}