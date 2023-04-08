package com.lm.yandexdiskplayer.ui.cells

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lm.yandexdiskplayer.ui.songCard
import com.lm.yandexdiskplayer.ui.states.ControllerUiStates
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@Composable
fun Buttons(
    size: Dp, controllerUiStates: ControllerUiStates,
    mainScreenState: MainScreenState,
    modifier: Modifier = Modifier
) = with(mainScreenState){
    Icon(
        Icons.Outlined.SkipPrevious,
        null,
        modifier.playerBarPrevModifier(size),
        tint = if (controllerUiStates.enablePrev) songCard else Color.LightGray
    )

    Icon(
        if (controllerUiStates.playerState == PlaybackStateCompat.STATE_PLAYING)
            Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
        null, modifier.playerBarPauseModifier(size),
        tint = if (controllerUiStates.enablePlay) songCard else Color.LightGray
    )
    Icon(
        Icons.Outlined.SkipNext,
        null, modifier.playerBarNextModifier(size),
        tint = if (controllerUiStates.enableNext) songCard else Color.LightGray
    )
}