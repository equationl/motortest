package com.equationl.motortest.compose.ui

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.equationl.motortest.R
import com.equationl.motortest.compose.theme.*

private const val TAG = "MainScreen"

@Composable
fun MainScreen(isDarkTheme: Boolean = isSystemInDarkTheme(), clickScreen: () -> Boolean, slipUpScreen: () -> Unit) {
    MaterialTheme(
        colors = if (isDarkTheme) DarkColors else LightColors
    ) {
        Scaffold(
            topBar = {
                MainTopBar()
            }
        ) {
            Box(modifier = Modifier.padding(it)) {
                MainContent(clickScreen, slipUpScreen)
            }
        }
    }
}

@Composable
fun MainTopBar() {
    TopAppBar {
        TopAppBar (
            title = {
                Text(stringResource(R.string.app_name))
            }
        )
    }
}

@Composable
fun MainContent(clickScreen: () -> Boolean, slipUpScreen: () -> Unit) {
    var isVibrated by remember {
        mutableStateOf(false)
    }

    var offset by remember { mutableStateOf(0f) }

    Box(
        Modifier
            .scrollable(
                orientation = Orientation.Vertical,
                state = rememberScrollableState { delta ->
                    Log.i(TAG, "MainContent: delta=$delta")
                    offset += delta
                    Log.i(TAG, "MainContent: offset=$offset")
                    if (offset < -100) {
                        slipUpScreen.invoke()
                    }
                    delta
                }
            )
            .clickable {
                isVibrated = clickScreen.invoke()
            }
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(
                if (isVibrated) R.string.main_text_click_stop
                else R.string.main_text_click_start
            ), modifier = if (isVibrated) Modifier.absoluteOffset(vibrationTextXAnimation().dp, vibrationTextYAnimation().dp) else Modifier)
        }

        AnimatedVisibility(
            visible = !isVibrated,
            enter = fadeIn() + expandVertically(initialHeight = { it + 100}),
            exit = shrinkVertically(targetHeight = { it+100 }) + fadeOut()
        ) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_details_see_more),
                    contentDescription = stringResource(R.string.main_text_slip_tip),
                    Modifier
                        .alpha(slipUpAlphaAnimation())
                        .absoluteOffset(y = slipUpYAnimation().dp)
                )
                Text(stringResource(R.string.main_text_slip_tip))
            }
        }
    }
}

@Preview
@Composable
fun PreviewMainLight() {
    var isVibrated = false
    MainScreen(
        false,
        {
            isVibrated = !isVibrated
            isVibrated
        },
        {}
    )
}

@Preview
@Composable
fun PreviewMainDark() {
    var isVibrated = false
    MainScreen(
        true,
        {
            isVibrated = !isVibrated
            isVibrated
        },
        {}
    )
}