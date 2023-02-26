package com.lm.yandexdiskplayer.ui.cells

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.lm.yandexdiskplayer.MainActivity
import com.lm.yandexdiskplayer.player.PlayerUiStates
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    mainScreenState: MainScreenState,
    playerUiStates: PlayerUiStates,
    modifier: Modifier = Modifier
) = with(mainScreenState) {
    Visibility(isAuth) {
        LazyColumn(modifier.columnModifier) { folders { Folder(mainScreenState, it) } }
    }
    val mainActivity = LocalContext.current as MainActivity
        BackHandler {
            if (playerUiStates.isPlayingCardVisible) playerUiStates.hidePlayingCard()
            else mainActivity.finish()
    }
}


