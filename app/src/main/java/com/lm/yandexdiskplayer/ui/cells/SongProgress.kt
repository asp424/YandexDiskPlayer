package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lm.yandexdiskplayer.ui.states.ControllerUiStates
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@Composable
fun SongProgress(
    mainScreenState: MainScreenState, playerUiStates: ControllerUiStates, modifier: Modifier = Modifier
) = Slider(
    value = playerUiStates.timeProgress,
    onValueChange = mainScreenState.onSliderValueChange(),
    onValueChangeFinished = mainScreenState.onSliderValueChangeFinished(),
    modifier = modifier.padding(start = 25.dp, end = 25.dp),
    colors = SliderDefaults.colors(thumbColor = Color.Black),
    enabled = playerUiStates.enableSlider,
)
