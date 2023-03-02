package com.lm.yandexdiskplayer

import android.media.AudioManager
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lm.yandexdiskplayer.media_browser.client.MediaClient
import com.lm.yandexdiskplayer.player.rememberPlayer
import com.lm.yandexdiskplayer.player.rememberPlayerUiStates
import com.lm.yandexdiskplayer.ui.cells.Logo
import com.lm.yandexdiskplayer.ui.cells.MainScreen
import com.lm.yandexdiskplayer.ui.cells.PlayingCard
import com.lm.yandexdiskplayer.ui.states.rememberMainScreenState

class MainActivity : ComponentActivity() {

    private val mediaClient by lazy { MediaClient(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val playerUiStates = rememberPlayerUiStates()
            val player = rememberPlayer(playerUiStates)
            val mainScreenState = rememberMainScreenState(player, playerUiStates)
            MainScreen(mainScreenState, playerUiStates)
            Logo(mainScreenState)
            PlayingCard(mainScreenState, playerUiStates)
        }
    }

    public override fun onStart() {
        super.onStart()
        mediaClient.mediaBrowser.connect()
    }

    public override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    public override fun onStop() {
        super.onStop()
        MediaControllerCompat.getMediaController(this)?.unregisterCallback(
            mediaClient.controllerCallback
        )
        mediaClient.mediaBrowser.disconnect()
    }
}












