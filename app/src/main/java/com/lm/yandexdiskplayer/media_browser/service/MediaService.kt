package com.lm.yandexdiskplayer.media_browser.service

import android.app.Notification
import android.content.Intent
import android.media.MediaMetadata
import android.media.MediaPlayer
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
import android.support.v4.media.session.PlaybackStateCompat.STATE_CONNECTING
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
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

    private val notificationManager by lazy {
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
        mediaSession?.setCallback(
            SessionCallback(
                player,
                { notificationBuilder(0).build() },
                { notificationBuilder(1).build() },
                this@MediaService
            )
        )
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

        private val controller = service.mediaSession?.controller

        override fun onPlay() {
            if (getState == STATE_PAUSED) {
                player.playAfterPause {
                    service.stateBuilder.setState(STATE_PLAYING, it, 1f)
                    setState
                    showNotify(pauseNotify)
                }
            } else {
                player.playNew {
                    service.mediaSession?.setMetadata(it)
                    service.stateBuilder.setState(STATE_PLAYING, 0, 1f)
                    setState
                    showNotify(pauseNotify)
                }
            }
        }

        override fun onPause() {
            player.pause {
                service.stateBuilder.setState(STATE_PAUSED, it, 1f)
                setState
                showNotify(playNotify)
            }
        }

        override fun onSkipToPrevious() {
            if (getState == STATE_PLAYING) {
                service.stateBuilder.setState(STATE_CONNECTING, 0, 1f)
                setState
                showNotify(pauseNotify)
                player.playPrevSong(1, onPrepare = {
                    player.play()
                }, metadata = {
                    service.mediaSession?.setMetadata(it)
                    service.stateBuilder.setState(STATE_PLAYING, 0, 1f)
                    setState
                    showNotify(pauseNotify)
                })
            } else {
                service.stateBuilder.setState(STATE_CONNECTING, 0, 1f)
                setState
                showNotify(playNotify)
                player.playPrevSong(0, onPrepare = {

                }, metadata = {
                    service.mediaSession?.setMetadata(it)
                    service.stateBuilder.setState(STATE_PAUSED, 0, 1f)
                    setState
                    showNotify(playNotify)
                })
            }
        }

        override fun onSkipToNext() {
            if (getState == STATE_PLAYING) {
                service.stateBuilder.setState(STATE_CONNECTING, 0, 1f)
                setState
                showNotify(pauseNotify)
                player.playNextSong(1, onPrepare = {
                    player.play()
                }, metadata = {
                    service.mediaSession?.setMetadata(it)
                    service.stateBuilder.setState(STATE_PLAYING, 0, 1f)
                    setState
                    showNotify(pauseNotify)
                })
            } else {
                service.stateBuilder.setState(STATE_CONNECTING, 0, 1f)
                setState
                showNotify(playNotify)
                player.playNextSong(0, onPrepare = {

                }, metadata = {
                    service.mediaSession?.setMetadata(it)
                    service.stateBuilder.setState(STATE_PAUSED, 0, 1f)
                    setState
                    showNotify(playNotify)
                })
            }
        }

        override fun onSeekTo(pos: Long) {
            player.seekTo(pos)
            if (getState == STATE_PLAYING) {
                service.stateBuilder.setState(STATE_PLAYING, pos, 1f)
                setState
            }
        }

        override fun onStop() {
            service.stateBuilder.setState(STATE_NONE, 0, 0f)
            setState
            player.releasePlayer()
        }

        override fun onRewind() {}
        override fun onSkipToQueueItem(id: Long) {}

        private fun showNotify(notify: () -> Notification) =
            service.startForeground(1001, notify.invoke())

        val setState
            get() = run {
                service.mediaSession?.setPlaybackState(service.stateBuilder.build())
            }

        val getState get() = run { controller?.playbackState?.state }
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
            setContentTitle(
                mediaSession?.controller?.metadata?.getString(
                    MediaMetadata.METADATA_KEY_TITLE
                )
            )
            setContentText(
                mediaSession?.controller?.metadata?.getString(
                    MediaMetadata.METADATA_KEY_ARTIST
                )
            )
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
            if (action == 0) addAction(
                NotificationCompat.Action(
                    R.drawable.pause, "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MediaService, ACTION_PAUSE
                    )
                )
            ) else addAction(
                NotificationCompat.Action(
                    R.drawable.play, "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        this@MediaService, ACTION_PLAY
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
