package com.example.xlisten.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Pink900,            // 默认按钮背景颜色
    primaryVariant = DarkGrey43,
    secondary = LightPink900,
    secondaryVariant = LightPink900,
    background = Color(0xFF121212),
    surface = DarkGrey30,
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

// private val LightColorPalette = lightColors(
// primary = Purple500,
// primaryVariant = Purple700,
// secondary = Teal200
//
// Other default colors to override
// background = Color.White,
// surface = Color.White,
// onPrimary = Color.White,
// onSecondary = Color.Black,
// onBackground = Color.Black,
// onSurface = Color.Black,
// )

@Composable
fun XListenTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    // val colors = if (darkTheme) { DarkColorPalette } else { LightColorPalette }
    val colors = DarkColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}