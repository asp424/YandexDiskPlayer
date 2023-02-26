package com.lm.yandexdiskplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.lm.yandexdiskplayer.player.rememberPlayer
import com.lm.yandexdiskplayer.player.rememberPlayerUiStates
import com.lm.yandexdiskplayer.ui.cells.Logo
import com.lm.yandexdiskplayer.ui.cells.MainScreen
import com.lm.yandexdiskplayer.ui.cells.PlayingCard
import com.lm.yandexdiskplayer.ui.states.rememberMainScreenState

class MainActivity : ComponentActivity() {

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
}











