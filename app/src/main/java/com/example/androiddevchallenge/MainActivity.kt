/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import android.view.Window
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import com.example.androiddevchallenge.ui.theme.grey
import com.example.androiddevchallenge.ui.theme.purple_background
import com.example.androiddevchallenge.ui.theme.timerCenter
import com.example.androiddevchallenge.ui.theme.timerEnd
import com.example.androiddevchallenge.ui.theme.timerStart

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.makeTransparentStatusBar()
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp() {
    val timerCount = 15000L
    val progress = remember { mutableStateOf(1F) }
    val timeLeft = remember { mutableStateOf(15) }
    val countdownStart = remember { mutableStateOf(timerCount) }
    val timerRunning = remember { mutableStateOf(false) }

    val timer: CountDownTimer = object : CountDownTimer(countdownStart.value, 10) {

        override fun onTick(millisUntilFinished: Long) {
            val value: Float = millisUntilFinished.toFloat() / (countdownStart.value.toFloat())
            progress.value = value
            timeLeft.value = (millisUntilFinished / 1000).toInt()
        }

        override fun onFinish() {
            cancel()
            timerRunning.value = false
            progress.value = 1F
            timeLeft.value = ((countdownStart.value / 1000).toInt())
        }
    }

    Surface(color = purple_background) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.weight(3f)
            ) {
                Box(
                    modifier = Modifier
                        .height(280.dp)
                        .width(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = 1F, strokeWidth = 6.dp, color = purple_background,
                        modifier = Modifier
                            .height(280.dp)
                            .width(280.dp)

                    )
                    CircularProgressIndicator(
                        progress = progress.value,
                        strokeWidth = 6.dp,
                        color = if (progress.value > 0.7) timerStart else if (progress.value > 0.3) timerCenter else timerEnd,
                        modifier = Modifier
                            .height(280.dp)
                            .width(280.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Crossfade(
                            targetState = getReadableTime(timeLeft.value),
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = LinearEasing
                            )

                        ) {
                            Text(
                                getReadableTime(timeLeft.value),
                                style = TextStyle(
                                    fontSize = if (timeLeft.value >= 3600) 48.sp else 72.sp,
                                    fontWeight = FontWeight.Light,
                                    color = grey
                                )
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconImage(resource = R.drawable.ic_remove, desc = "Remove Time") {
                        val value = 1000
                        timer.cancel()
                        timerRunning.value = false
                        progress.value = 1F
                        timeLeft.value = ((countdownStart.value / 1000).toInt())

                        if (countdownStart.value > value) {
                            countdownStart.value = countdownStart.value - value
                            timeLeft.value = ((countdownStart.value / 1000).toInt())
                        }
                    }
                    IconImage(resource = if ((timerRunning.value)) (R.drawable.ic_stop) else (R.drawable.ic_play), desc = "Play Icon") {
                        when (timerRunning.value) {
                            true -> {
                                timer.cancel()
                                timerRunning.value = false
                                progress.value = 1F
                                timeLeft.value = ((countdownStart.value / 1000).toInt())
                            }
                            false -> {
                                timer.start()
                                timeLeft.value = ((countdownStart.value / 1000).toInt())
                                timerRunning.value = true
                            }
                        }
                    }
                    IconImage(resource = R.drawable.ic_add, desc = "Add Time") {
                        val value = 1000
                        timer.cancel()
                        timerRunning.value = false
                        progress.value = 1F
                        timeLeft.value = ((countdownStart.value / 1000).toInt())

                        countdownStart.value = countdownStart.value + value
                        timeLeft.value = ((countdownStart.value / 1000).toInt())
                    }
                }
            }
        }
    }
}

@Composable
fun IconImage(resource: Int, desc: String, clickAction: () -> Unit) {
    val paintImage: Painter = painterResource(id = resource)
    Image(painter = paintImage, contentDescription = desc, modifier = Modifier.clickable(onClick = clickAction))
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

fun Window.makeTransparentStatusBar() {
    setFlags(
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
    )
    setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}
