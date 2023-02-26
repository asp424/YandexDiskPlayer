package com.lm.core

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import kotlin.reflect.KProperty
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
    get() = with(toDuration(DurationUnit.MILLISECONDS)
        .toString(DurationUnit.SECONDS).replace('s', ' ')){
        val f = filter { it.isDigit() }.toInt()
        val d = f % 60
        val minutes = ((f - d) / 60).toString()
        val seconds = d.toString()
        val zeroM = if (minutes.length == 1) "0$minutes" else minutes
        val zeroS = if (seconds.length == 1) "0$seconds" else seconds
        "$zeroM:$zeroS"
    }
