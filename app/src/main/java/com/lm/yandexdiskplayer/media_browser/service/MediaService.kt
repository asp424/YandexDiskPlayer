package com.lm.yandexdiskplayer.media_browser.service

import android.app.Notification
import android.app.NotificationManager
import android.content.Intent
import android.media.MediaMetadata
import android.net.Uri
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
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.STATE_BUFFERING
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import androidx.media.MediaBrowserServiceCompat
import androidx.media.session.MediaButtonReceiver
import com.lm.core.log
import com.lm.yandexapi.models.Song
import com.lm.yandexdiskplayer.R
import com.lm.yandexdiskplayer.player
import com.lm.yandexdiskplayer.player.Player
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.O)
class MediaService : MediaBrowserServiceCompat() {

    private var currentMetaData = MediaMetadataCompat.Builder()

    private var mediaSession: MediaSessionCompat? = null

    private val notificationManager by  lazy {
        getSystemService<NotificationManagerCompat>()
    }

    private val stateBuilder by lazy {
        PlaybackStateCompat.Builder()
            .setActions(
                ACTION_PLAY
                        or ACTION_PAUSE
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

            setSessionToken(sessionToken)
            isActive = true
        }
        startForegroundService(Intent(this@MediaService, MediaService::class.java))
        mediaSession?.setCallback(SessionCallback(player,
            { notificationBuilder(0).build() },
            { notificationBuilder(1).build() },
            this@MediaService))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        MediaButtonReceiver.handleIntent(mediaSession, intent)
        return super.onStartCommand(intent, flags, startId)
    }

    private class SessionCallback(
        private val player: Player,
        private val pauseNotify: () -> Notification,
        private val playNotify: () -> Notification,
        private val service: MediaService
    ) : MediaSessionCompat.Callback() {

        private val metadataBuilder = MediaMetadataCompat.Builder()

        private val controller = service.mediaSession?.controller

        override fun onPlay() {
            player.playSong{
                service.mediaSession?.setMetadata(
                    metadataBuilder
                        .putText(MediaMetadata.METADATA_KEY_TITLE, player.currentSong?.name)
                        .putText(MediaMetadata.METADATA_KEY_ARTIST, player.currentSong?.folder)
                        .putLong(MediaMetadata.METADATA_KEY_DURATION, player.player?.duration!!.toLong())
                        .build()
                )
                showNotify(pauseNotify)
            }
        }

        override fun onPause() {
            showNotify(playNotify)
            player.pause()
        }

        override fun onSkipToPrevious() {
            player.playPrevSong()
            showNotify(pauseNotify)
        }

        override fun onSkipToNext() {
            player.playNextSong()
            showNotify(pauseNotify)
        }

        override fun onSeekTo(pos: Long) {}
        override fun onStop() {}
        override fun onRewind() {}
        override fun onSkipToQueueItem(id: Long) {}

        private fun showNotify(notify: () -> Notification)
        = service.startForeground(1001, notify.invoke())
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
        runBlocking(IO) {
            result.sendResult(
                when (parentId) {
                    MediaId.songs ->
                        player.currentPlaylist.map { it.asBrowserMediaItem }.toMutableList()

                    else -> mutableListOf()
                }
            )
        }
    }

    private val Song.asBrowserMediaItem
        inline get() = MediaBrowserCompat.MediaItem(
            MediaDescriptionCompat.Builder()
                .setMediaId("$folder$path")
                .setTitle(path)
                .setSubtitle(folder)
                .setIconUri(Uri.EMPTY)
                .build(),
            MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
        )

    private fun notificationBuilder(action: Int) =
        NotificationCompat.Builder(
            this@MediaService, notificationChannelId
        ).apply {
            stateBuilder
                .setState(STATE_PLAYING, 0, 1f)
                .setBufferedPosition(0)
            mediaSession?.setPlaybackState(stateBuilder.build())
            mediaSession?.setMetadata(
                currentMetaData
                    .putText(MediaMetadata.METADATA_KEY_TITLE, player.currentSong?.name)
                    .putText(MediaMetadata.METADATA_KEY_ARTIST, player.currentSong?.folder)
                    .putLong(MediaMetadata.METADATA_KEY_DURATION, player.player?.duration!!.toLong())
                    .build()
            )
            setContentTitle(player.currentSong?.name)
            setContentText(player.currentSong?.folder)
            setLargeIcon(getDrawable(R.drawable.disk_logo)?.toBitmap())
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
            if(action == 0) addAction( NotificationCompat.Action(
                R.drawable.pause, "Pause",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@MediaService, ACTION_PAUSE
                )
            )) else addAction(NotificationCompat.Action(
                R.drawable.play, "Play",
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    this@MediaService, ACTION_PLAY
                )
            ))
            addAction(
                NotificationCompat.Action(
                    R.drawable.play_skip_forward, getString(R.string.next),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MediaService, PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                )
            )
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
        super.onDestroy()
    }

    companion object {
        private const val sessionTag = "MediaService"
        private const val notificationId = 1001
        private const val notificationChannelId = "default_channel_id"
    }
}
