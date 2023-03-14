package com.lm.core

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.core.content.getSystemService
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun <T> Context.toast(any: T) = Toast.makeText(this, any.toString(), Toast.LENGTH_SHORT).show()

val <T> T.log get() = Log.d("My", toString())

@Composable
fun animDp(start: Dp, end: Dp, isAuth: Boolean) = animateDpAsState(
    if (!isAuth) start else end, tween(700)
).value

fun tryCatch(
    tryBlock: () -> Unit,
    onSuccess: () -> Unit = {},
    onFailure: (Throwable) -> Unit = {}
) = runCatching { tryBlock() }.onSuccess { onSuccess() }.onFailure { onFailure(it) }

val Int.getSeconds
    get() = with(
        toDuration(DurationUnit.MILLISECONDS)
            .toString(DurationUnit.SECONDS).replace('s', ' ')
    ) {
        val f = filter { it.isDigit() }.toInt()
        val d = f % 60
        val minutes = ((f - d) / 60).toString()
        val seconds = d.toString()
        val zeroM = if (minutes.length == 1) "0$minutes" else minutes
        val zeroS = if (seconds.length == 1) "0$seconds" else seconds
        "$zeroM:$zeroS"
    }

inline val isAtLeastAndroid8
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

inline val isAtLeastAndroid12
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

inline val isAtLeastAndroid6
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

val Context.isIgnoringBatteryOptimizations: Boolean
    get() = if (isAtLeastAndroid6) {
        getSystemService<PowerManager>()?.isIgnoringBatteryOptimizations(packageName) ?: true
    } else {
        true
    }

inline fun <reified T : Activity> Context.activityPendingIntent(
    requestCode: Int = 0,
    flags: Int = 0,
    block: Intent.() -> Unit = {},
): PendingIntent =
    PendingIntent.getActivity(
        this,
        requestCode, intentOf<T>().apply(block), PendingIntent.FLAG_IMMUTABLE or flags
    )

inline fun <reified T> Context.intentOf(): Intent = Intent(this, T::class.java)

inline fun <reified T : BroadcastReceiver> Context.broadCastPendingIntent(
    requestCode: Int = 0,
    flags: Int = PendingIntent.FLAG_IMMUTABLE,
): PendingIntent =
    PendingIntent.getBroadcast(this, requestCode, intentOf<T>(), flags)




