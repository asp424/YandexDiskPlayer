package com.lm.yandexdiskplayer.ui.cells

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
            0.dp, animateDpAsState(if (!controllerUiStates.isBottomBarVisible) 140.dp else 0.dp)
                .value
        ), contentAlignment = Alignment.BottomCenter
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color.LightGray)
            .fillMaxWidth().height(100.dp)
            .padding(start = 20.dp, end = 20.dp)
            .clickable {
                controllerUiStates.showPlayingCard()
                controllerUiStates.hideBottomBar()
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
            SongProgress(mainScreenState, controllerUiStates, 0.dp)
        }
        Buttons(size = 28.dp, controllerUiStates, mainScreenState)
    }
    Box(
        Modifier
            .fillMaxWidth()
            .padding(10.dp), contentAlignment = Alignment.TopEnd) {
        Icon(
            Icons.Default.Close, null, modifier = modifier
                .size(20.dp)
                .offset(0.dp, (-65).dp)
                .clickable {
                    controllerUiStates.hideBottomBar()
                    mainScreenState.stop()
                }, tint = songCard
        )
    }
}
