package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.lm.yandexdiskplayer.R
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@Composable
fun Logo(
    mainScreenState: MainScreenState,
    modifier: Modifier = Modifier
) = with(mainScreenState){
    Box(modifier.boxLogoModifier, Center) {
        Image(painterResource(R.drawable.disk_logo), null, modifier.authByClick)
    }
}