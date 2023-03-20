package com.lm.yandexdiskplayer.media_browser.service

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.RequiresApi
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.lm.yandexapi.models.Song
import com.lm.yandexapi.songs
import com.lm.yandexdiskplayer.media_browser.service.Notify.Companion.notificationId
import com.lm.yandexdiskplayer.player.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.O)
class MediaService : MediaBrowserServiceCompat() {

    var mediaSession: MediaSessionCompat? = null

    private val notification by lazy { Notify(this) }

    var songsMediaItems = mutableListOf<MediaBrowserCompat.MediaItem>()

    var songsList = listOf<Song>()

    var songsMap = mapOf<String, List<Song>>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        startForegroundService(Intent(this@MediaService, MediaService::class.java))
        startForeground(notificationId, notification.notificationBuilder(0).build())
        mediaSession = MediaSessionCompat(baseContext, "session").apply {
            setSessionToken(sessionToken)
            isActive = true
            setCallback(SessionCallback { notification.notificationBuilder(it).build() })
        }

        runBlocking {
            CoroutineScope(IO).launch {
                songsList = songs
                songsMap = songsList.groupBy { it.folder }
                songsMediaItems = songsList.map { it.songMediaItem }.toMutableList()
            }.join()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?)
            : BrowserRoot? = if (clientUid == Process.myUid() || clientUid == Process.SYSTEM_UID) {
        BrowserRoot("root", null)
    } else null

    override fun onLoadChildren(
        parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        if (parentId == "root") result.sendResult(songsMediaItems)
    }

    private val Song.songMediaItem
        inline get() = MediaBrowserCompat.MediaItem(
            MediaDescriptionCompat.Builder()
                .setMediaId(path).setTitle(name).setSubtitle(folder).build(),
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        )

    override fun onDestroy() {
        mediaSession?.release()
        super.onDestroy()
    }
}
