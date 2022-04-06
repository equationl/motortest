package com.equationl.motortest.compose.ui

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equationl.motortest.R
import com.equationl.motortest.compose.MyViewMode
import com.equationl.motortest.compose.theme.*
import com.equationl.motortest.util.VibratorHelper
import com.equationl.motortest.view.MyTouchView
import java.util.*
import kotlin.concurrent.fixedRateTimer

private var timingsText = "0"
private var amplitudeText  = "0"
private var timerText = 0
private var startTime = 0

private var durationShowText by mutableStateOf("0")
private var amplitudeShowText by mutableStateOf("0")

private lateinit var refreshTextTimer: Timer
private lateinit var viewModel: MyViewMode

@Composable
fun VisualizationScreen(isDarkTheme: Boolean = isSystemInDarkTheme()) {
    viewModel = viewModel()

    initData()

    MaterialTheme(
        colors = if (isDarkTheme) DarkColors else LightColors
    ) {
        Column(Modifier.fillMaxSize().background(if (isDarkTheme) Color.DarkGray else Color.White)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .weight(9f)
            ) {
                VisualSideBar(isDarkTheme)
                VisualTouchView()
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                VisualBottomBar()
            }
        }
    }
}

@Composable
fun VisualBottomBar() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Button(onClick = {
            VibratorHelper.instance.cancel()
            viewModel.currentPage = 1
        }) {
            Text(stringResource(R.string.visualization_btn_back))
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp)
        ) {
            Text(durationShowText)
            Text(amplitudeShowText)
        }

        Button(onClick = {
            viewModel.visualAmplitude = amplitudeText
            viewModel.visualTimings = timingsText
            viewModel.isBackFromVisual = true
            viewModel.currentPage = 1
        }) {
            Text(stringResource(R.string.visualization_btn_save))
        }
    }
}

@Composable
fun VisualSideBar(isDarkTheme: Boolean) {
    val brush = Brush.verticalGradient(
        if (isDarkTheme)
            listOf(
                grayDarkPrimaryDark,
                grayDarkSecondary
            )
        else
            listOf(
                grayPrimaryDark,
                graySecondary
            )
    )

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 4.dp)
            .background(brush)
    ) {
        Text(
            stringResource(R.string.visualization_sideBar_text_strong),
            color = if (isDarkTheme) grayDarkSecondary else graySecondary,
            modifier = Modifier.padding(2.dp)
        )

        Text(
            stringResource(R.string.visualization_sideBar_text_weak),
            color = if (isDarkTheme) grayDarkPrimaryDark else grayPrimaryDark,
            modifier = Modifier.padding(2.dp)
        )
    }
}

@Composable
fun VisualTouchView() {
    AndroidView(modifier = Modifier.fillMaxSize(),
        factory = { context ->
            MyTouchView(context).apply {
                setOnTouchActionListener(getTouchListener(this))
            }
        }
    )
}

fun initData() {
    timingsText = "0"
    amplitudeText  = "0"
    timerText = 0
    startTime = 0

    durationShowText = "0"
    amplitudeShowText = "0"
}

private fun getTouchListener(myTouchView: MyTouchView): MyTouchView.OnTouchActionListener {
    return object : MyTouchView.OnTouchActionListener {
        override fun onDown(motionEvent: MotionEvent) {
            val amplitude = getAmplitude(motionEvent.y, myTouchView.top, myTouchView.bottom)
            VibratorHelper.instance.vibrateOneShot(120000, amplitude.coerceIn(1, 255))
            startTime = timerText
            amplitudeShowText = amplitude.toString()
            refreshTextTimer = fixedRateTimer(null, false, 0, 1) {
                timerText++
                durationShowText = String.format("%.3f s", timerText.toFloat()/1000)
            }
        }

        override fun onMove(motionEvent: MotionEvent) {
            val amplitude = getAmplitude(motionEvent.y, myTouchView.top, myTouchView.bottom)
            recordTouch(amplitude)
            startTime = timerText
            VibratorHelper.instance.vibrateOneShot(120000, amplitude.coerceIn(1, 255))
        }

        override fun onUp(motionEvent: MotionEvent) {
            val amplitude = getAmplitude(motionEvent.y, myTouchView.top, myTouchView.bottom)
            recordTouch(amplitude)
            startTime = timerText
            VibratorHelper.instance.cancel()
            refreshTextTimer.cancel()
        }

    }
}

private fun getAmplitude(y: Float, top: Int, bottom: Int): Int {
    val touchTop = top.toFloat() + 50
    val touchBottom = bottom.toFloat() - 50
    val relativeScreenHeight = touchBottom - touchTop
    val touchY = y.coerceIn(
        0f,
        touchBottom
    )
    val amplitude = 255 - touchY * 255 / relativeScreenHeight
    return amplitude.coerceIn(0f, 255f).toInt()
}

private fun recordTouch(amplitude: Int) {
    val timings = timerText - startTime
    timingsText = "$timingsText, $timings"
    amplitudeText = "$amplitudeText, $amplitude"
    amplitudeShowText = amplitude.toString()
}