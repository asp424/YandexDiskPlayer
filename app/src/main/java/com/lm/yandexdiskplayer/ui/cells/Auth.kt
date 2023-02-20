package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import com.lm.yandexdiskplayer.R
import com.lm.yandexdiskplayer.ui.states.AuthState
import com.lm.yandexdiskplayer.ui.states.SongsListState
import com.lm.yandexdiskplayer.ui.states.rememberAuthState
import com.lm.yandexdiskplayer.ui.states.rememberSongsListState
import kotlinx.coroutines.Dispatchers.IO

@OptIn(ExperimentalTextApi::class)
@Composable
fun AuthLogo(
    modifier: Modifier = Modifier,
    songsListState: SongsListState,
    authState: AuthState = rememberAuthState(rememberTextMeasurer(), IO, songsListState),
    authByClick: Modifier = with(authState) { modifier.authByClick },
    canvasModifier: Modifier = with(authState) { modifier.canvasModifier },
    boxModifier: Modifier = with(authState) { modifier.textBoxModifier },
) = Box(modifier.fillMaxSize(), Alignment.Center) {
    Image(painterResource(R.drawable.disk_logo), null, authByClick)
    Box(boxModifier) { Canvas(canvasModifier, authState.drawText) }
}
