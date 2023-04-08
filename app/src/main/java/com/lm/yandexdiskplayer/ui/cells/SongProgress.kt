package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lm.yandexdiskplayer.ui.songCard
import com.lm.yandexdiskplayer.ui.states.ControllerUiStates
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@Composable
fun SongProgress(
    mainScreenState: MainScreenState,
    playerUiStates: ControllerUiStates,
    padding: Dp,
    modifier: Modifier = Modifier
) = Slider(
    value = playerUiStates.timeProgress,
    onValueChange = mainScreenState.onSliderValueChange(),
    onValueChangeFinished = mainScreenState.onSliderValueChangeFinished(),
    modifier = modifier.padding(start = padding, end = padding),
    colors = SliderDefaults.colors(
        thumbColor = songCard,
        activeTrackColor = songCard
    ),
    enabled = playerUiStates.enableSlider,
)
