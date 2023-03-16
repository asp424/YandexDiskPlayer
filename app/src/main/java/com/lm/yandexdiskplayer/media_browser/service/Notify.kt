package com.lm.yandexdiskplayer.media_browser.service

import android.app.Notification
import android.media.MediaMetadata
import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.media.session.MediaButtonReceiver
import com.lm.yandexdiskplayer.R

context(MediaService)
class Notify(private val context: MediaService) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun notificationBuilder(action: Int) =
        NotificationCompat.Builder(
            context, notificationChannelId
        ).apply {
            setContentTitle(context.mediaSession?.controller?.metadata?.getString(
                MediaMetadata.METADATA_KEY_TITLE
            ))
            setContentText(context.mediaSession?.controller?.metadata?.getString(
                MediaMetadata.METADATA_KEY_ARTIST
            ))
            setLargeIcon(context.getDrawable(R.drawable.disk_logo)?.toBitmap())
            setCategory(Notification.CATEGORY_TRANSPORT)
            setAutoCancel(false)
            setOnlyAlertOnce(true)
            setShowWhen(false)
            setOngoing(true)
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context, PlaybackStateCompat.ACTION_STOP
                )
            )
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.disk_logo)
            color = ContextCompat.getColor(context, R.color.black)
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(context.mediaSession?.sessionToken)
            )
            addAction(
                NotificationCompat.Action(
                    R.drawable.play_skip_back, context.getString(R.string.back),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                    )
                )
            )
            if (action == 0) addAction(
                NotificationCompat.Action(
                    R.drawable.pause, "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_PAUSE
                    )
                )
            ) else addAction(
                NotificationCompat.Action(
                    R.drawable.play, "Play",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_PLAY
                    )
                )
            )
            addAction(
                NotificationCompat.Action(
                    R.drawable.play_skip_forward, context.getString(R.string.next),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    )
                )
            )
        }

    companion object{
        private const val notificationId = 1001
        private const val notificationChannelId = "default_channel_id"
    }
}