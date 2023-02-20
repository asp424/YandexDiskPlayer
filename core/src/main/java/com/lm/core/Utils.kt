package com.lm.core

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.sin

fun <T> Context.toast(any: T)
= Toast.makeText(this, any.toString(), Toast.LENGTH_SHORT).show()

val <T> T.log get() = Log.d("My", toString())

fun round(x: Float, y: Float, delta: MutableState<Float>) = Offset(
    150 * cos(10 * x * 0.0174444444 + delta.value).toFloat(),
    60 * sin(10 * y * 0.0174444444 + delta.value).toFloat()
)
