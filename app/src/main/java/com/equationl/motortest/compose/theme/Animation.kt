package com.equationl.motortest.compose.theme

import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue


@Composable
fun slipUpAlphaAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition()
    val slipUpAlphaAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    return slipUpAlphaAnimation
}

@Composable
fun slipUpYAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition()
    val slipUpYAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -30f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        )
    )
    return slipUpYAnimation
}

@Composable
fun vibrationTextXAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition()
    val vibrationTextXAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(40),
            repeatMode = RepeatMode.Reverse
        )
    )
    return vibrationTextXAnimation
}

@Composable
fun vibrationTextYAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition()
    val vibrationTextYAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(40),
            repeatMode = RepeatMode.Reverse
        )
    )
    return vibrationTextYAnimation
}