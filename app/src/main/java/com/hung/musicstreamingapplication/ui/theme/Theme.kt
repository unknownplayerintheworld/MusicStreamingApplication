package com.hung.musicstreamingapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
@Suppress("DEPRECATION")
private val DarkColorScheme = ColorScheme(
    primary = Color(0xFFBB86FC),            // Màu tím sáng
    onPrimary = Color.White,                // Màu chữ trên nền primary
    primaryContainer = Color(0xFF3700B3),  // Màu nền cho container chính
    onPrimaryContainer = Color.White,       // Màu chữ trên primary container
    secondary = Color(0xFF03DAC6),          // Màu xanh ngọc
    onSecondary = Color.Black,              // Màu chữ trên secondary
    secondaryContainer = Color(0xFF018786), // Màu nền cho container secondary
    onSecondaryContainer = Color.White,     // Màu chữ trên secondary container
    tertiary = Color(0xFF6200EE),           // Màu tím tối
    onTertiary = Color.White,               // Màu chữ trên tertiary
    tertiaryContainer = Color(0xFF3700B3),  // Màu nền cho container tertiary
    onTertiaryContainer = Color.White,      // Màu chữ trên tertiary container
    error = Color(0xFFB00020),              // Màu đỏ cho lỗi
    onError = Color.White,                   // Màu chữ trên lỗi
    errorContainer = Color(0xFF3700B3),     // Màu nền cho container lỗi
    onErrorContainer = Color.White,          // Màu chữ trên container lỗi
    background = Color(0xFF000000),         // Nền tối
    onBackground = Color.White,              // Màu chữ trên nền
    surface = Color(0xFF121211),            // Bề mặt tối
    onSurface = Color(0xFFF1F1F1),                 // Màu chữ trên bề mặt
    surfaceVariant = Color(0xFF232322),     // Màu bề mặt phụ
    onSurfaceVariant = Color.White,          // Màu chữ trên bề mặt phụ
    outline = Color(0xFFBDBDBD),             // Màu viền
    inverseOnSurface = Color.Black,          // Màu chữ đảo ngược trên bề mặt
    inverseSurface = Color.White,            // Màu bề mặt đảo ngược
    inversePrimary = Color(0xFFBB86FC),
    outlineVariant = Color.Black,
    scrim = Color.Black,
    surfaceTint = Color.Black// Màu primary đảo ngược
)
@Suppress("DEPRECATION")
// Bảng màu cho chế độ light theme
private val LightColorScheme = ColorScheme(
    primary = Color(0xFF6200EE),            // Màu tím tối
    onPrimary = Color.White,                 // Màu chữ trên nền primary
    primaryContainer = Color(0xFFEDE7F6),    // Màu nền cho container chính
    onPrimaryContainer = Color(0xFF3700B3),  // Màu chữ trên primary container
    secondary = Color(0xFF03DAC6),          // Màu xanh ngọc
    onSecondary = Color.Black,               // Màu chữ trên secondary
    secondaryContainer = Color(0xFFB2DFDB), // Màu nền cho container secondary
    onSecondaryContainer = Color.Black,      // Màu chữ trên secondary container
    tertiary = Color(0xFFBB86FC),           // Màu tím sáng
    onTertiary = Color.Black,                // Màu chữ trên tertiary
    tertiaryContainer = Color(0xFFEDE7F6),   // Màu nền cho container tertiary
    onTertiaryContainer = Color.Black,       // Màu chữ trên tertiary container
    error = Color(0xFFB00020),               // Màu đỏ cho lỗi
    onError = Color.White,                    // Màu chữ trên lỗi
    errorContainer = Color(0xFFEF9A9A),      // Màu nền cho container lỗi
    onErrorContainer = Color.White,           // Màu chữ trên container lỗi
    background = Color(0xFFFFFFFF),          // Nền trắng
    onBackground = Color.Black,               // Màu chữ trên nền trắng
    surface = Color(0xFFFAFAFA),             // Bề mặt sáng màu
    onSurface = Color(0xFF222222),                  // Màu chữ trên bề mặt
    surfaceVariant = Color(0xFFE0E0E0),      // Màu bề mặt phụ
    onSurfaceVariant = Color.Black,           // Màu chữ trên bề mặt phụ
    outline = Color(0xFFBDBDBD),              // Màu viền
    inverseOnSurface = Color.White,           // Màu chữ đảo ngược trên bề mặt
    inverseSurface = Color.Black,             // Màu bề mặt đảo ngược
    inversePrimary = Color(0xFF6200EE),
    outlineVariant = Color.Black,
    scrim = Color.Black,
    surfaceTint = Color.Black/// Màu primary đảo ngược
)

@Composable
fun MusicStreamingApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Nếu bạn muốn tuỳ chỉnh cả typography
        content = content
    )
}