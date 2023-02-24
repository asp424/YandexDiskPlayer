package com.lm.core

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

fun <T> Context.toast(any: T) = Toast.makeText(this, any.toString(), Toast.LENGTH_SHORT).show()

val <T> T.log get() = Log.d("My", toString())

@Composable
fun animDp(start: Dp, end: Dp, isAuth: Boolean) = animateDpAsState(
    if (!isAuth) start else end, tween(700)
).value
