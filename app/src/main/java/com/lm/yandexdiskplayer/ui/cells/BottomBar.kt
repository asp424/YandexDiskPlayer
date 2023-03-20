package com.lm.yandexdiskplayer.ui.cells

import android.support.v4.media.session.PlaybackStateCompat
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SkipNext
import androidx.compose.material.icons.outlined.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.lm.yandexdiskplayer.ui.songCard
import com.lm.yandexdiskplayer.ui.states.ControllerUiStates
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@Composable
fun BottomBar(
    controllerUiStates: ControllerUiStates,
    mainScreenState: MainScreenState,
    modifier: Modifier = Modifier
) = Box(
    modifier
        .fillMaxSize()
        .offset(
            0.dp, animateDpAsState(
                targetValue = if (!controllerUiStates.isBottomBarVisible)
                    100.dp else 0.dp
            ).value
        ), contentAlignment = Alignment.BottomCenter
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color.LightGray)
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
            .clickable {
                controllerUiStates.isPlayingCardVisible = true
            }
    ) {
        Image(
            painterResource(R.drawable.disk_logo), contentDescription = null,
            modifier = modifier.size(40.dp)
        )
        Column(verticalArrangement = Arrangement.Center, modifier = modifier.width(200.dp)) {

            Text(
                controllerUiStates.nowPlayingSong.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = modifier
                    .padding(start = 20.dp, end = 20.dp, top = 5.dp)
                    .offset(0.dp, 10.dp)
            )
            Text(
                controllerUiStates.nowPlayingSong.folder,
                modifier
                    .padding(bottom = 5.dp, start = 20.dp, end = 20.dp, top = 5.dp)
                    .offset(0.dp, 10.dp),
                fontSize = 10.sp
            )
            Slider(
                value = controllerUiStates.timeProgress,
                onValueChange = mainScreenState.onSliderValueChange(),
                onValueChangeFinished = mainScreenState.onSliderValueChangeFinished(),
                colors = SliderDefaults.colors(
                    thumbColor = songCard,
                    activeTrackColor = songCard
                )
            )
        }
        with(mainScreenState) {
            Icon(
                Icons.Outlined.SkipPrevious,
                null, modifier.playerBarPrevModifier(25.dp),
                tint = if (controllerUiStates.enablePrev) songCard else Color.LightGray
            )

            Icon(
                if (controllerUiStates.playerState == PlaybackStateCompat.STATE_PLAYING)
                    Icons.Outlined.Pause else Icons.Outlined.PlayArrow,
                null, modifier.playerBarPauseModifier(25.dp),
                tint = if (controllerUiStates.enablePlay) songCard else Color.LightGray
            )
            Icon(
                Icons.Outlined.SkipNext,
                null, modifier.playerBarNextModifier(25.dp),
                tint = if (controllerUiStates.enableNext) songCard else Color.LightGray
            )
        }
    }
}