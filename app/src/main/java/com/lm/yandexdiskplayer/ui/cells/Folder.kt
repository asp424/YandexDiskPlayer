package com.lm.yandexdiskplayer.ui.cells

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lm.yandexapi.models.Folder
import com.lm.yandexdiskplayer.ui.states.MainScreenState

@Composable
fun Folder(
    mainScreenState: MainScreenState,
    folderInfo: Folder,
    modifier: Modifier = Modifier
) =
    with(mainScreenState) {
        with(modifier) {
            with(folderInfo) {
                Column {
                    Card(cardFolderModifier, border = BorderStroke(1.dp, Color.Black)) {
                        Row(modifier.rawModifier, verticalAlignment = CenterVertically) {
                            Icon(Icons.Outlined.Folder, null)
                            Column(modifier.fillMaxSize(), Center) {
                                Text(path, textPathModifier)
                                Text(date, textDateModifier)
                            }
                        }
                    }
                    Songs(mainScreenState, folderInfo)
                }
            }
        }
    }