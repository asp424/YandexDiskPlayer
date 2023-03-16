package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.yandexdiskplayer.player.ControllerUiStates
import com.lm.yandexdiskplayer.player.PlayerState
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayingCard(
    mainScreenState: MainScreenState,
    controllerUiStates: ControllerUiStates,
    modifier: Modifier = Modifier
) = with(mainScreenState) {
    Visibility(controllerUiStates.isPlayingCardVisible) {
        Box(
            modifier
                .fillMaxSize()
                .background(Color.White), Alignment.Center
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 300.dp), Arrangement.Center, Alignment.CenterHorizontally
            ) {
                Text(
                    controllerUiStates.nowPlayingSong.name, fontWeight = FontWeight.Bold, fontSize = 20.sp,
                    modifier = modifier.padding(start = 20.dp, end = 20.dp)
                )
                Text(
                    controllerUiStates.nowPlayingSong.folder,
                    modifier.padding(top = 10.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                    fontSize = 12.sp
                )
                SongProgress(mainScreenState, controllerUiStates)
                Row(
                    Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        controllerUiStates.timeTextProgress,
                        modifier
                            .padding(start = 35.dp)
                            .offset(y = (-15).dp),
                        fontSize = 12.sp, fontWeight = FontWeight.Bold
                    )
                    Text(
                        controllerUiStates.durationSong,
                        modifier
                            .padding(end = 35.dp)
                            .offset(y = (-15).dp),
                        fontSize = 12.sp, fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 65.dp)
                        .height(40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.SkipPrevious,
                        null, modifier.playerBarPrevModifier,
                        tint = if (controllerUiStates.enablePrev) Color.Black else Color.LightGray
                    )

                    Icon(
                        if (controllerUiStates.playerState == PlayerState.PLAYING)
                            Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                        null, modifier.playerBarPauseModifier,
                        tint = if (controllerUiStates.enablePlay) Color.Black else Color.LightGray
                    )
                    Icon(
                        Icons.Outlined.SkipNext,
                        null, modifier.playerBarNextModifier,
                        tint = if (controllerUiStates.enableNext) Color.Black else Color.LightGray
                    )
                }
            }
        }
    }
}