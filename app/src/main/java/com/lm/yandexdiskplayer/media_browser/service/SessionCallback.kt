package com.lm.yandexdiskplayer.media_browser.service

import android.app.Notification
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PAUSE
import android.support.v4.media.session.PlaybackStateCompat.ACTION_PLAY
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SEEK_TO
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_NEXT
import android.support.v4.media.session.PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
import android.support.v4.media.session.PlaybackStateCompat.ACTION_STOP
import android.support.v4.media.session.PlaybackStateCompat.STATE_NONE
import android.support.v4.media.session.PlaybackStateCompat.STATE_PAUSED
import android.support.v4.media.session.PlaybackStateCompat.STATE_PLAYING
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import com.lm.core.log
import com.lm.yandexapi.models.Song
import com.lm.yandexdiskplayer.player

context(MediaService)
@RequiresApi(Build.VERSION_CODES.O)
class SessionCallback(
    private val notify: (Int) -> Notification
) : MediaSessionCompat.Callback() {

    private val notificationManager = getSystemService<NotificationManager>()

    override fun onPlay() {
        if (getState == STATE_PAUSED)
            player.playAfterPause {
                setStateBuilder(STATE_PLAYING, it)
                showNotify { notify(0) }
            } else {
            setStateBuilder(STATE_PAUSED, 0)
            player.playNew({
                setMetadata
                setStateBuilder(STATE_PLAYING, 0)
                startForeground(1001, notify(0))
            }) { player.next { nextPrev } }
        }
    }

    override fun onPause() {
        player.pause { setStateBuilder(STATE_PAUSED, it); showNotify { notify(1) } }
    }

    override fun onSkipToPrevious() {
        player.prev { nextPrev }
    }

    override fun onSkipToNext() {
        player.next { nextPrev }
    }

    override fun onSeekTo(pos: Long) {
        player.seekTo(pos); if (getState == STATE_PLAYING) setStateBuilder(STATE_PLAYING, pos)
    }

    override fun onStop() {
        setStateBuilder(STATE_NONE, 0); player.releasePlayer()
    }

    private fun showNotify(notify: () -> Notification) =
        notificationManager?.notify(1001, notify.invoke())

    override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
        val song = songsList.find { it.path == mediaId } ?: Song()
        val playList = songsMap[mediaId?.substringBefore(song.name)]?: emptyList()
        player.loadSongs(song, playList.sortedBy { it.name })
        mediaSession?.controller?.transportControls?.stop()
        mediaSession?.controller?.transportControls?.play()
        super.onPlayFromMediaId(mediaId, extras)
    }

    private val setState
        get() = run { mediaSession?.setPlaybackState(stateBuilder.build()) }

    private val getState get() = run { controller?.playbackState?.state }

    private val MediaMetadataCompat.setMetadata get() = run { mediaSession?.setMetadata(this) }

    private fun setStateBuilder(state: Int, dur: Long) {
        stateBuilder.setState(state, dur, 1f); setState
    }

    private val MediaMetadataCompat.nextPrev
        get() = run {
            if (getState == STATE_PLAYING) {
                setMetadata
                setStateBuilder(STATE_PLAYING, 0)
                showNotify { notify(0) }
                controller?.transportControls?.play()
            } else {
                setMetadata
                setStateBuilder(STATE_PAUSED, 0)
                showNotify { notify(1) }
                player.prepareNew {
                    it.setMetadata
                    setStateBuilder(STATE_PAUSED, 0)
                    showNotify { notify(1) }
                }
            }
        }

    private val controller by lazy { mediaSession?.controller }

    private val stateBuilder: PlaybackStateCompat.Builder by lazy {
        PlaybackStateCompat.Builder().setActions(
            ACTION_PLAY or ACTION_PAUSE or ACTION_STOP
                    or ACTION_SKIP_TO_PREVIOUS or ACTION_SKIP_TO_NEXT or ACTION_SEEK_TO
        )
    }
}