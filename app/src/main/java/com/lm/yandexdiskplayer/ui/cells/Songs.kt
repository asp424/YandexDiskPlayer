package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.lm.core.log
import com.lm.yandexapi.models.Folder
import com.lm.yandexapi.models.Song
import com.lm.yandexdiskplayer.ui.songCard
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Songs(
    mainScreenState: MainScreenState,
    listSongs: List<Song>,
    modifier: Modifier = Modifier
) = Visibility(mainScreenState.isExpand) {
    Column {

        listSongs.map {
            Card(
                with(mainScreenState) { modifier.cardSongModifier(it.path) },
                colors = CardDefaults.cardColors(
                    containerColor = songCard
                )
            ) {
                Text(it.name, modifier.padding(10.dp), color = White)
            }
        }
    }
}

