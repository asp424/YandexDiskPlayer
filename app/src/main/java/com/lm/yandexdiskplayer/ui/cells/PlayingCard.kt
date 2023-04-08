package com.lm.yandexdiskplayer.ui.cells

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.yandexdiskplayer.R
import com.lm.yandexdiskplayer.ui.states.ControllerUiStates
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
            Image(
                painterResource(R.drawable.disk_logo), contentDescription = null,
                modifier = modifier.size(160.dp).offset(0.dp, (-150).dp)
            )
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
                SongProgress(mainScreenState, controllerUiStates, 25.dp)
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
                        .padding(top = 65.dp, start = 60.dp, end = 60.dp)
                        .height(40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Buttons(size = 60.dp, controllerUiStates, mainScreenState)
                }
            }
        }
    }
}