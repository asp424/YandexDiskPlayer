package com.lm.yandexdiskplayer

import android.app.Application
import android.content.Context
import com.lm.yandexdiskplayer.player.Player
import com.lm.yandexdiskplayer.player.PlayerImpl

class App: Application() {

    val playerMain: Player by lazy {
        PlayerImpl(this)
    }
}

val Context.player: Player get() = when(this){
    is App -> playerMain
    else -> applicationContext.player
}