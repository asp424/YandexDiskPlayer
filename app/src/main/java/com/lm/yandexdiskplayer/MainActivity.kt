package com.lm.yandexdiskplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lm.yandexdiskplayer.ui.cells.AuthLogo
import com.lm.yandexdiskplayer.ui.cells.ListSongs
import com.lm.yandexdiskplayer.ui.states.rememberSongsListState
import kotlinx.coroutines.Dispatchers.IO

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val songsListState = rememberSongsListState(IO)
            ListSongs(songsListState = songsListState)
            AuthLogo(songsListState = songsListState)
        }
    }
}











