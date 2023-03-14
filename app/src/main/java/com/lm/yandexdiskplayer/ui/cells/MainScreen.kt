package com.lm.yandexdiskplayer.ui.cells

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.lm.yandexdiskplayer.MainActivity
import com.lm.yandexdiskplayer.player.ControllerUiStates
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    mainScreenState: MainScreenState,
    controllerUiStates: ControllerUiStates,
    modifier: Modifier = Modifier
) = with(mainScreenState) {
    Visibility(controllerUiStates.columnVisible) {
        LazyColumn(modifier.columnModifier) { folders { Folder(mainScreenState, it) } }
    }
    val mainActivity = LocalContext.current as MainActivity
        BackHandler {
            if (controllerUiStates.isPlayingCardVisible) controllerUiStates.hidePlayingCard()
            else mainActivity.finish()
    }
}


