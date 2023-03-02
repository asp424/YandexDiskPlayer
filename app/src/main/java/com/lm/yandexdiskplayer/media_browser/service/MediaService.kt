package com.lm.yandexdiskplayer.media_browser.service

import android.app.Notification
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
import android.support.v4.media.session.MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.lm.core.log
import com.lm.yandexdiskplayer.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class MediaService : MediaBrowserServiceCompat() {

    private var mediaSession: MediaSessionCompat? = null

    private val stateBuilder by lazy {
        PlaybackStateCompat.Builder()
            .setActions(
                ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PAUSE
                        or ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_STOP
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM
                        or PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_REWIND
            )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(baseContext, MediaService::class.java.simpleName).apply {
            setFlags(FLAG_HANDLES_MEDIA_BUTTONS or FLAG_HANDLES_TRANSPORT_CONTROLS)
            setPlaybackState(stateBuilder.build())
            setCallback(SessionCallback())
            setSessionToken(this.sessionToken)
            isActive = true
            setMetadata(MediaMetadataCompat.Builder()
                .putText("path", "ass")
                .build()
            )
        }
        start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private class SessionCallback() : MediaSessionCompat.Callback() {

        override fun onPlay() {
            "play".log
        }

        override fun onPause() {
            "pause".log
        }

        override fun onSkipToPrevious() {
            "prev".log
        }

        override fun onSkipToNext() {
            "next".log
        }

        override fun onSeekTo(pos: Long) {}
        override fun onStop() {}
        override fun onRewind() {}
        override fun onSkipToQueueItem(id: Long) {}
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? = if (clientUid == Process.myUid() || clientUid == Process.SYSTEM_UID) {
        BrowserRoot(
            MediaId.root,
            bundleOf("android.media.browse.CONTENT_STYLE_BROWSABLE_HINT" to 1)
        )
    } else null

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        runBlocking(Dispatchers.IO) {
            result.sendResult(
                when (parentId) {
                    MediaId.root -> mutableListOf(
                        MediaBrowserCompat.MediaItem(
                            MediaDescriptionCompat.Builder()
                                .setMediaId(MediaId.songs)
                                .setTitle("Songs")
                                .build(),
                            MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
                        )
                    )

                    else -> mutableListOf()
                }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun start() {
        val controller = mediaSession?.controller
        val mediaMetadata = controller?.metadata?.bundle?.getString("ass")
        val builder = NotificationCompat.Builder(
            this, notificationChannelId
        ).apply {
            setContentTitle(mediaMetadata)
            setContentText(mediaMetadata)
            setSubText(mediaMetadata)
            setLargeIcon(getDrawable(R.drawable.disk_logo)?.toBitmap())
            setContentIntent(controller?.sessionActivity)
            setCategory(Notification.CATEGORY_TRANSPORT)
            setAutoCancel(false)
            setOnlyAlertOnce(true)
            setShowWhen(false)
            setOngoing(true)
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@MediaService, PlaybackStateCompat.ACTION_STOP
                )
            )
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.disk_logo)
            color = ContextCompat.getColor(this@MediaService, R.color.black)

            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSession?.sessionToken)
            )
            addAction(
                NotificationCompat.Action(
                    R.drawable.play_skip_back, getString(R.string.back),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MediaService, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                )
            )
            addAction(
                NotificationCompat.Action(
                    R.drawable.pause, getString(R.string.pause),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MediaService, PlaybackStateCompat.ACTION_PAUSE
                    )
                )
            )
            addAction(
                NotificationCompat.Action(
                    R.drawable.play_skip_forward, getString(R.string.next),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MediaService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                )
            )
        }

        startForegroundService(Intent(this, MediaService::class.java))
        startForeground(notificationId, builder.build())
    }

    private object MediaId {
        const val root = "root"
        const val songs = "songs"
        const val playlists = "playlists"
        const val albums = "albums"

        const val favorites = "favorites"
        const val offline = "offline"
        const val shuffle = "shuffle"

        fun forSong(id: String) = "songs/$id"
        fun forPlaylist(id: Long) = "playlists/$id"
        fun forAlbum(id: String) = "albums/$id"
    }

    override fun onDestroy() {
        mediaSession?.release()
        "destr".log
        super.onDestroy()
    }

    companion object {
        private const val sessionTag = "MediaService"
        private const val notificationId = 1001
        private const val notificationChannelId = "default_channel_id"
    }
}
