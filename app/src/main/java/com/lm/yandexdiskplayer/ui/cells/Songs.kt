package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import com.lm.yandexapi.models.Folder
import com.lm.yandexdiskplayer.ui.songCard
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Songs(
    mainScreenState: MainScreenState,
    folder: Folder,
    modifier: Modifier = Modifier
) {
    Visibility(mainScreenState.isExpand) {
        Column {
            folder.listSongs.map {
                Card(
                    with(mainScreenState){ modifier.cardSongModifier(it) },
                    colors = CardDefaults.cardColors(containerColor = songCard)
                ) {
                    Text(it.name, with(mainScreenState){ modifier.textSongsModifier }, color = White)
                }
            }
        }
    }
}

