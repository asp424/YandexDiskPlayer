package com.lm.yandexdiskplayer.ui.states

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
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
import com.lm.core.log
import com.lm.core.utils.YandexAuthToken
import com.lm.core.utils.getToken
import com.lm.core.utils.preferences
import com.lm.core.utils.readValue
import com.lm.yandexapi.download
import com.lm.yandexapi.downloadListener
import com.lm.yandexapi.getPlayList
import com.lm.yandexdiskplayer.retrofit.api.Callback.startRequest
import com.lm.yandexdiskplayer.retrofit.api.fetch
import com.lm.yandexdiskplayer.player.Player
import com.yandex.disk.rest.json.Link
import com.yandex.disk.rest.json.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

@Stable
interface SongsListState {

    val songsList: SnapshotStateList<Resource>
    fun Modifier.cardModifier(item: Resource): Modifier
    val Modifier.columnModifier: Modifier
    val Modifier.textModifier: Modifier
    val isAuth: Boolean
    val loadList: Job
    fun setAuthState(state: Boolean)
    fun LazyListScope.songs(item: @Composable LazyItemScope.(Resource) -> Unit)
}

private class SongsListStateImpl(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val context: Context,
    private val player: Player = Player()
) :
    SongsListState {

    private var _isAuth by mutableStateOf(context.getToken.isNotEmpty())

    override var songsList: SnapshotStateList<Resource> = mutableStateListOf()

    override fun Modifier.cardModifier(item: Resource): Modifier = composed {
        val coroutineScope = rememberCoroutineScope()
        padding(start = 10.dp, end = 10.dp)
            .clickable(remember { MutableInteractionSource() }, null,
                onClick = remember { click(coroutineScope, item) }
            )
    }

    override val Modifier.columnModifier get() = fillMaxSize().padding(top = 10.dp)

    override val Modifier.textModifier get() = padding(10.dp, 10.dp, 10.dp, 16.dp)

    override var isAuth: Boolean
        get() = _isAuth
        private set(value) {
            _isAuth = value
        }

    override fun setAuthState(state: Boolean) { _isAuth = state; loadList }

    override fun LazyListScope.songs(item: @Composable LazyItemScope.(Resource) -> Unit) =
        items(songsList, key = { it.md5 }, contentType = { it }) { item(this, it) }

    init {
        if (_isAuth) loadList
    }

    override val loadList get() = CoroutineScope(coroutineDispatcher).launch {
        context.getPlayList.forEach { songsList.add(it) }
    }

    private fun click(coroutineScope: CoroutineScope, item: Resource): () -> Unit = {
        coroutineScope.launch(coroutineDispatcher) {
            fetch(item.path.path, context.getToken).startRequest().collect{
                if(it is com.lm.yandexdiskplayer.retrofit.api.Resource.Success)
                    player.createUrl(it.data.href)
            }
        }
    }
}

@Composable
fun rememberSongsListState(
    coroutineDispatcher: CoroutineDispatcher,
    context: Context = LocalContext.current
): SongsListState = remember { SongsListStateImpl(coroutineDispatcher, context) }
