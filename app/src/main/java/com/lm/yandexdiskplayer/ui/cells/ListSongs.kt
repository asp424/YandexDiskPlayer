package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lm.core.log
import com.lm.yandexdiskplayer.ui.states.SongsListState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ListSongs(modifier: Modifier = Modifier, songsListState: SongsListState) =
    with(songsListState) {
        Visibility(songsListState.isAuth) {
            LazyColumn(
                modifier.columnModifier, content = {
                    songs {
                        Card(modifier.cardModifier(it)) { Text(it.name, modifier.textModifier) }
                    }
                })
        }
    }
