package com.lm.yandexdiskplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lm.yandexdiskplayer.ui.cells.Logo
import com.lm.yandexdiskplayer.ui.cells.MainScreen
import com.lm.yandexdiskplayer.ui.states.rememberMainScreenState

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainScreenState = rememberMainScreenState()
            MainScreen(mainScreenState)
            Logo(mainScreenState)
        }
    }
}











