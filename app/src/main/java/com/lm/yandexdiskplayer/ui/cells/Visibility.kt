package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@ExperimentalAnimationApi
@Composable
fun Visibility(visible: Boolean, content: @Composable (AnimatedVisibilityScope.() -> Unit)) {
    val density = LocalDensity.current
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(

            initialOffsetY = { with(density) { -40.dp.roundToPx() } }
        ) + expandVertically(

            expandFrom = Alignment.Bottom
        ) + fadeIn(

            initialAlpha = 0.3f
        ),
        exit = slideOutVertically() + shrinkVertically() + fadeOut()
    ) {
        content(this)
    }

}