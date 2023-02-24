package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    mainScreenState: MainScreenState,
    modifier: Modifier = Modifier
) = Visibility(mainScreenState.isAuth) {
    LazyColumn(with(mainScreenState) { modifier.columnModifier }, rememberLazyListState()) {
        with(mainScreenState) { folders { FolderItem(mainScreenState, it) } }
    }
}

@Composable
fun rememberLazyListState(
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    return rememberSaveable(saver = LazyListState.Saver) {
        LazyListState(
            initialFirstVisibleItemIndex,
            initialFirstVisibleItemScrollOffset
        )
    }
}


