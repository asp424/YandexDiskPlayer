package com.lm.yandexdiskplayer.ui.states

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.lm.core.animDp
import com.lm.core.utils.getToken
import com.lm.yandexapi.folders
import com.lm.yandexapi.models.Folder
import com.lm.yandexapi.resultHandler
import com.lm.yandexapi.startAuth
import com.lm.yandexdiskplayer.player.Player
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

private class MainScreenStateImpl(
    private val context: Context,
    private val coroutineDispatcher: CoroutineDispatcher = IO,
    private val player: Player = Player(),
    private val gson: Gson = Gson()
) : MainScreenState {

    private var _isAuth by mutableStateOf(context.getToken.isNotEmpty())

    private var _isExpand by mutableStateOf(false)

    init {
        if (_isAuth) loadList; _isAuth = false
    }

    private var foldersList: SnapshotStateList<Folder> = mutableStateListOf()

    private val loadList
        get() = CoroutineScope(coroutineDispatcher).launch {
            context.folders.map {
                foldersList.add(it); if (!_isAuth) _isAuth = true
            }
        }

    private fun play(coroutineScope: CoroutineScope, path: String): () -> Unit = {
        player.playUrl(coroutineScope, coroutineDispatcher, context, path)
    }

    override val Modifier.authByClick: Modifier
        get() = composed {
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                onResult = context.resultHandler(onGetToken = { loadList }, onFailure = {})
            )
            clickable(
                remember { MutableInteractionSource() }, null, onClick =
                remember { { launcher.launch(context.startAuth()) } }
            )
                .offset(animDp(0.dp, 140.dp, isAuth), animDp(0.dp, (-330).dp, isAuth))
                .size(animDp(160.dp, 70.dp, isAuth))
        }

    override val Modifier.cardFolderModifier: Modifier
        get() = composed {
            val coroutineScope = rememberCoroutineScope()
            padding(start = 10.dp, end = 10.dp).clickable(
                remember { MutableInteractionSource() }, null,
                onClick = remember { { _isExpand = !_isExpand } }
            )
        }

    override fun Modifier.cardSongModifier(path: String): Modifier = composed {
        val coroutineScope = rememberCoroutineScope()
        padding(start = 50.dp).clickable(
            remember { MutableInteractionSource() }, null,
            onClick = remember { play(coroutineScope, path) }
        )
    }

    override val Modifier.columnModifier get() = fillMaxSize()

    override val Modifier.textPathModifier get() = padding(10.dp, 10.dp, 10.dp, 0.dp)

    override val Modifier.textDateModifier: Modifier get() = padding(10.dp, 0.dp, 10.dp, 10.dp)

    override val Modifier.rawModifier: Modifier get() = fillMaxSize().padding(start = 10.dp)

    override var isAuth: Boolean
        get() = _isAuth
        private set(value) {
            _isAuth = value
        }

    override var isExpand: Boolean
        get() = _isExpand
        private set(value) {
            _isExpand = value
        }

    override fun LazyListScope.folders(item: @Composable LazyItemScope.(Folder) -> Unit) =
        items(foldersList, { it.key }, { it }) { item(it) }
}

@Composable
fun rememberMainScreenState(context: Context = LocalContext.current): MainScreenState =
    remember { MainScreenStateImpl(context) }




