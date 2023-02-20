package com.lm.yandexdiskplayer.player

import android.media.MediaPlayer
import com.lm.core.log
import java.io.File

class Player {

    var player: MediaPlayer? = null

    fun create(file: String) {
        player = MediaPlayer()
        tryCatch({
                player?.apply {
                    setDataSource(file)
                    prepareAsync()
                    setOnPreparedListener { start() }
                    setOnCompletionListener {  }
                }
        }, onFailure = {  })
    }

    fun createUrl(url: String) {
        player = MediaPlayer()
        tryCatch({
            player?.apply {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { start() }
                setOnCompletionListener {  }
            }
        }, onFailure = {
            it.message.log
        })
    }

    private fun tryCatch(
        tryBlock: () -> Unit,
        onSuccess: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    ) = runCatching { tryBlock() }.onSuccess { onSuccess() }.onFailure {
        onFailure(it)
    }
}