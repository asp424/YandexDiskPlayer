package com.lm.yandexdiskplayer.media_browser.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.media.MediaMetadata
import android.os.Build
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.graphics.drawable.toBitmap
import androidx.media.session.MediaButtonReceiver
import com.lm.core.activityPendingIntent
import com.lm.core.isAtLeastAndroid8
import com.lm.yandexdiskplayer.MainActivity
import com.lm.yandexdiskplayer.R

context(MediaService)
class Notify(private val context: MediaService) {

    private val notificationManager = getSystemService<NotificationManager>()
    @RequiresApi(Build.VERSION_CODES.O)
    fun notificationBuilder(action: Int) =
        NotificationCompat.Builder(
            context, notificationChannelId
        ).apply {
            setContentTitle(
                context.mediaSession?.controller?.metadata?.getString(
                    MediaMetadata.METADATA_KEY_TITLE
                )
            )
            setContentText(
                context.mediaSession?.controller?.metadata?.getString(
                    MediaMetadata.METADATA_KEY_ARTIST
                )
            )
            setLargeIcon(context.getDrawable(R.drawable.disk_logo)?.toBitmap())
            setCategory(Notification.CATEGORY_TRANSPORT)
            setAutoCancel(false)
            setOnlyAlertOnce(true)
            setShowWhen(false)
            setOngoing(true)
            setContentIntent(
                activityPendingIntent<MainActivity>(
                    flags = PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.disk_logo)
            color = ContextCompat.getColor(context, R.color.black)
            setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2, 3)
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
            addAction(
                NotificationCompat.Action(
                    R.drawable.close, context.getString(R.string.close),
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_REWIND
                    )
                )
            )
        }

    fun startServiceNotify() = NotificationCompat.Builder(
            context, notificationChannelId
        ).apply {
            setContentTitle(
                "YandexDiskPlayer service is running"
            )
            setLargeIcon(context.getDrawable(R.drawable.disk_logo)?.toBitmap())
            setCategory(Notification.CATEGORY_TRANSPORT)
            setAutoCancel(false)
            setOnlyAlertOnce(true)
            setShowWhen(false)
            setOngoing(true)
            setContentIntent(
                activityPendingIntent<MainActivity>(
                    flags = PendingIntent.FLAG_UPDATE_CURRENT
                )
            )
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setSmallIcon(R.drawable.disk_logo)
        }.build()

    fun createNotificationChannel(context: MediaService) {
        if (!isAtLeastAndroid8) return

        notificationManager?.run {
            if (getNotificationChannel(notificationChannelId) == null) {
                createNotificationChannel(
                    NotificationChannel(
                        notificationChannelId,
                        "Now playing",
                        NotificationManager.IMPORTANCE_LOW
                    ).apply {
                        setSound(null, null)
                        enableLights(false)
                        enableVibration(false)
                    }
                )
            }
        }
    }

    companion object {
        const val notificationId = 1001
        const val notificationIdStartService = 1002
        private const val notificationChannelId = "default_channel_id"
    }
}