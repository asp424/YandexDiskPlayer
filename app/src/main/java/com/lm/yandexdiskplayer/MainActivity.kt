package com.lm.yandexdiskplayer

import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.lm.yandexdiskplayer.media_browser.client.MediaClient
import com.lm.yandexdiskplayer.ui.states.ControllerUiStates
import com.lm.yandexdiskplayer.ui.cells.Logo
import com.lm.yandexdiskplayer.ui.cells.MainScreen
import com.lm.yandexdiskplayer.ui.cells.PlayingCard
import com.lm.yandexdiskplayer.ui.states.rememberMainScreenState

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : ComponentActivity() {

    private val controllerUiStates: ControllerUiStates by lazy {
        ControllerUiStates()
    }

    private val mediaClient by lazy { MediaClient(this, controllerUiStates) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainScreenState = rememberMainScreenState(mediaClient)
            MainScreen(mainScreenState, controllerUiStates)
            Logo(mainScreenState)
            PlayingCard(mainScreenState, controllerUiStates)
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












