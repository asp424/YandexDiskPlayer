package com.lm.yandexdiskplayer.ui.states

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lm.core.round
import com.lm.core.utils.YandexAuthToken
import com.lm.core.utils.preferences
import com.lm.core.utils.saveValue
import com.lm.yandexapi.resultHandler
import com.lm.yandexapi.startAuth
import com.lm.yandexdiskplayer.MainActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private class AuthStateImpl
@OptIn(ExperimentalTextApi::class)
constructor(
    private val textMeasurer: TextMeasurer,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val songsListState: SongsListState
) : AuthState {

    private var isRunning by mutableStateOf(false)

    private var word by mutableStateOf(if (songsListState.isAuth) "" else "вход")

    private var delta = mutableStateOf(10.6f)

    private var logoX by mutableStateOf(if (songsListState.isAuth) 140.dp else 0.dp)

    private var logoY by mutableStateOf(if (songsListState.isAuth) (-330).dp else 0.dp)

    private var logoSize by mutableStateOf(if (songsListState.isAuth) 60.dp else 160.dp)

    private var job: Job = Job()

    private val startAnimation: () -> Unit
        get() = {
            job.cancel()
            job = CoroutineScope(coroutineDispatcher).launch {
                isRunning = true
                word = "авторизация..."
                runAnim()
            }
        }

    private val stopAnimation: () -> Unit
        get() = {
            job.cancel()
            isRunning = false
        }

    override val Modifier.authByClick: Modifier
        get() = composed {
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current as MainActivity
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                onResult = context.resultHandler(
                    onGetToken = {
                        context.preferences.saveValue(YandexAuthToken, it)
                        delta.value = 10f
                        word = "загрузка..."
                        coroutineScope.launch {
                            songsListState.setAuthState(true)
                            delay(3000)
                            stopAnimation()
                            word = ""
                            runMoving()
                        }
                    }, onFailure = {
                        word = "фэйл"
                        stopAnimation()
                        delta.value = 10.7f
                    })
            )
            clickable(
                remember { MutableInteractionSource() },
                null,
                onClick = remember {
                    {
                        if (!isRunning) {
                            if (word == "вход" || word == "фэйл") {
                                startAnimation()
                                coroutineScope.launch {
                                    delay(3000)
                                    launcher.launch(context.startAuth())
                                }
                            }
                        }
                    }
                }
            )
                .offset(logoX, logoY)
                .size(logoSize)
        }

    override val Modifier.canvasModifier: Modifier get() = fillMaxWidth().height(200.dp)

    override val Modifier.textBoxModifier: Modifier
        get() = padding(start = 216.dp, top = 140.dp).rotate(-25f)

    @OptIn(ExperimentalTextApi::class)
    override val drawText: DrawScope.() -> Unit
        get() = {
            word.forEachIndexed { i, c ->
                drawText(
                    textMeasurer, c.toString(), round(i.toFloat(), i.toFloat(), delta),
                    TextStyle(Color.White, 10.sp, FontWeight.Bold)
                )
            }
        }

    private suspend fun runAnim() {
        withContext(IO) {
            while (isActive) {
                delta.value += 0.01f
                delay(5)
            }
        }
    }

    private suspend fun runMoving() {
        withContext(IO) {
            while (isActive && logoX < 130.dp) {
                logoX += 0.08.dp
                logoY -= 0.2.dp
                logoSize -= 0.05.dp
                delay(1)
            }
        }
    }

    private suspend fun runMovingDown() {
        withContext(IO) {
            while (isActive && logoX >= 0.dp) {
                logoX -= 0.08.dp
                logoY += 0.2.dp
                logoSize += 0.05.dp
                delay(0.8.toLong())
            }
        }
    }
}

@Stable
interface AuthState {
    val Modifier.canvasModifier: Modifier
    val Modifier.textBoxModifier: Modifier
    val Modifier.authByClick: Modifier
    val drawText: DrawScope.() -> Unit
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun rememberAuthState(
    textMeasurer: TextMeasurer,
    coroutineDispatcher: CoroutineDispatcher,
    songsListState: SongsListState
): AuthState = remember { AuthStateImpl(textMeasurer, coroutineDispatcher, songsListState) }