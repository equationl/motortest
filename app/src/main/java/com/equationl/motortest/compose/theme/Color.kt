package com.equationl.motortest.compose.theme

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

// for light
val grayPrimary = Color(0xFF607D8B)
val grayPrimaryDark = Color(0xFF455A64)
val grayAccent = Color(0xFF263238)
val graySecondary = Color(0xFFCFD8Dc)

// for night
val grayDarkPrimary = Color(0xFF37474f)
val grayDarkPrimaryDark = Color(0xFF616161)
val grayDarkAccent = Color(0xFF424242)
val grayDarkSecondary = Color(0xFF78909c)

val DarkColors = darkColors(
    primary = grayDarkPrimary,
    secondary = grayDarkSecondary,
)
val LightColors = lightColors(
    primary = grayPrimary,
    secondary = graySecondary,
)