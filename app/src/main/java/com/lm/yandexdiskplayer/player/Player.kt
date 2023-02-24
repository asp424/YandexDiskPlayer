package com.lm.yandexdiskplayer.player

import android.content.Context
import android.media.MediaPlayer
import com.lm.core.log
import com.lm.core.utils.getToken
import com.lm.yandexdiskplayer.retrofit.api.Callback.startRequest
import com.lm.yandexdiskplayer.retrofit.api.LoadingResource
import com.lm.yandexdiskplayer.retrofit.api.fetch
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class Player {

    var player: MediaPlayer? = null

    fun playUrl(
        coroutineScope: CoroutineScope,
        coroutineDispatcher: CoroutineDispatcher,
        context: Context,
        path: String
    ) {
        player = MediaPlayer()
        tryCatch({
            player?.apply {
                getUrl(
                    coroutineScope,
                    coroutineDispatcher,
                    context,
                    path
                ) {
                    setDataSource(it)
                    prepareAsync()
                    setOnPreparedListener { start() }
                    setOnCompletionListener { }
                }
            }
        }, onFailure = { it.message.log })
    }

    private fun tryCatch(
        tryBlock: () -> Unit,
        onSuccess: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    ) = runCatching { tryBlock() }.onSuccess { onSuccess() }.onFailure { onFailure(it) }

    private fun getUrl(
        coroutineScope: CoroutineScope,
        coroutineDispatcher: CoroutineDispatcher,
        context: Context,
        path: String,
        onGet: (String) -> Unit
    ) {
        coroutineScope.launch(coroutineDispatcher) {
            fetch(path, context.getToken).startRequest().collect {
                if (it is LoadingResource.Success) onGet(it.data.href)
            }
        }
    }
}