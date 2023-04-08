package com.lm.yandexdiskplayer.ui.states

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import com.lm.core.animDp
import com.lm.core.getSeconds
import com.lm.yandexapi.models.Folder
import com.lm.yandexapi.models.Song
import com.lm.yandexapi.resultHandler
import com.lm.yandexapi.startAuth
import com.lm.yandexdiskplayer.media_browser.client.MediaClient
context(Context)

@RequiresApi(Build.VERSION_CODES.O)
private class MainScreenStateImpl(
    private val mediaClient: MediaClient
) : MainScreenState {

    private var _isExpand by mutableStateOf(false)

    override val Modifier.authByClick: Modifier
        get() = composed {
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                onResult = resultHandler(onGetToken = { mediaClient.mediaBrowser.connect() },
                    onFailure = {})
            )
            clickable(
                remember { MutableInteractionSource() }, null, onClick =
                remember { { launcher.launch(startAuth()) } },
                enabled = !mediaClient.controllerUiStates.isAuth
            )

                .offset(
                    animDp(0.dp, 140.dp, mediaClient.controllerUiStates.columnVisible),
                    animDp(0.dp, (-330).dp, mediaClient.controllerUiStates.columnVisible)
                )
                .size(animDp(160.dp, 70.dp, mediaClient.controllerUiStates.columnVisible))
        }

    override fun Modifier.cardFolderModifier(folder: Folder): Modifier = composed {
        padding(start = 10.dp, end = 10.dp).clickable(
            remember { MutableInteractionSource() }, null,
            onClick = remember { { _isExpand = !_isExpand } }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun Modifier.cardSongModifier(song: Song): Modifier = composed {
        padding(start = 50.dp).clickable(
            remember { MutableInteractionSource() }, null,
            onClick = remember {
                {
                    mediaClient.controllerUiStates.showPlayingCard()
                    mediaClient.controllerUiStates.setSongInPlayingCard(song)
                    mediaClient.controllerUiStates.timeProgress = 0f
                    if (mediaClient.mediaBrowser.isConnected) {
                        mediaClient.mediaController?.transportControls?.playFromMediaId(
                            song.path, null
                        )
                    }
                }
            }
        )
    }

    override val Modifier.columnModifier get() = fillMaxSize()

    override val Modifier.textPathModifier get() = padding(10.dp, 20.dp, 10.dp, 20.dp)

    override val Modifier.textDateModifier: Modifier get() = padding(10.dp, 0.dp, 10.dp, 10.dp)

    override val Modifier.rawModifier: Modifier get() = fillMaxSize().padding(start = 10.dp)

    override val Modifier.textSongsModifier: Modifier get() = padding(10.dp)

    override val Modifier.boxLogoModifier: Modifier get() = fillMaxSize()
    override fun Modifier.playerBarPrevModifier(size: Dp): Modifier =
        clickable(mediaClient.controllerUiStates.enablePrev) {
            mediaClient.mediaController?.transportControls?.skipToPrevious()
        }

            .size(size)

    override fun Modifier.playerBarNextModifier(size: Dp): Modifier =
        clickable(mediaClient.controllerUiStates.enableNext) {
            mediaClient.mediaController?.transportControls?.skipToNext()
        }.size(size)

    override fun Modifier.playerBarPauseModifier(size: Dp): Modifier =
        clickable(mediaClient.controllerUiStates.enablePlay) {
            if (mediaClient.controllerUiStates.playerState == PlaybackStateCompat.STATE_PLAYING)
                mediaClient.mediaController?.transportControls?.pause()
            else mediaClient.mediaController?.transportControls?.play()
        }.size(size)

    override var isExpand: Boolean
        get() = _isExpand
        private set(value) {
            _isExpand = value
        }

    override fun LazyListScope.folders(item: @Composable LazyItemScope.(Folder) -> Unit) {
        items(mediaClient.controllerUiStates.folderList, { it.path }, { it }) { item(it) }
    }

    override fun onSliderValueChange(): (Float) -> Unit = {
        with(mediaClient.controllerUiStates) {
            timeJob.cancel()
            timeProgress = it
            mediaClient.mediaController?.apply {
                timeTextProgress = duration.getTextProgress.getSeconds
            }
        }
    }

    override fun onSliderValueChangeFinished(): () -> Unit = {
        with(mediaClient.controllerUiStates) {
            mediaClient.mediaController?.apply {
                transportControls?.seekTo((duration / (1f / timeProgress)).toLong())
                startPlay(mediaClient.mediaController)
            }
        }
    }

    override fun disconnect() {
        mediaClient.mediaController?.transportControls?.rewind()
        mediaClient.mediaBrowser.disconnect()
    }

    override fun connect() {
        mediaClient.mediaBrowser.connect()
    }

    override fun stop() {
        mediaClient.mediaController?.transportControls?.pause()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun rememberMainScreenState(
    mediaClient: MediaClient,
    context: Context = LocalContext.current
): MainScreenState =
    remember { with(context){ MainScreenStateImpl(mediaClient) } }




