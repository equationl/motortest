package com.equationl.motortest.compose.ui

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.equationl.motortest.R
import com.equationl.motortest.compose.MyViewMode
import com.equationl.motortest.compose.theme.*
import kotlin.math.roundToInt

private const val TAG = "MainScreen"

private lateinit var viewMode: MyViewMode

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(isDarkTheme: Boolean = isSystemInDarkTheme(), clickScreen: () -> Boolean, slipUpScreen: () -> Unit) {
    viewMode = viewModel()

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val screenHeightPx = with(LocalDensity.current) { screenHeight.toPx() }

    val swipeableState = rememberSwipeableState(0)
    val anchors = mapOf(0f to 0, -screenHeightPx-100 to 1)
    val animaY = remember {
        if (viewMode.isFirstBoot) {
            androidx.compose.animation.core.Animatable(0f)
        }
        else {
            androidx.compose.animation.core.Animatable(-screenHeightPx)
        }
    }
    LaunchedEffect(key1 = viewMode.isFirstBoot) {
        if (!viewMode.isFirstBoot) {
            animaY.animateTo(0f)
        }
        else {
            viewMode.isFirstBoot = false
        }
    }
    val modifier = Modifier.absoluteOffset(y = animaY.value.dp)

    MaterialTheme(
        colors = if (isDarkTheme) DarkColors else LightColors
    ) {
        Scaffold(
            topBar = {
                MainTopBar()
            },
            modifier = if (viewMode.isVibrated || !viewMode.isSupportAdvanced) {
                modifier
            }
            else {
                modifier
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        thresholds = { _, _ -> FractionalThreshold(0.3f) },
                        orientation = Orientation.Vertical
                    )
                    .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
            }
        ) {
            Box(modifier = Modifier.padding(it)) {
                MainContent(clickScreen)
            }

            if (swipeableState.currentValue == 1) {
                slipUpScreen.invoke()
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
fun MainContent(clickScreen: () -> Boolean) {
    var isVibrated by remember {
        mutableStateOf(false)
    }
    Box(
        Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = {isVibrated = clickScreen.invoke()})
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