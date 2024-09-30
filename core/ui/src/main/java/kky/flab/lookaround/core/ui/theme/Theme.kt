package kky.flab.lookaround.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val LightColorScheme = lightColorScheme(
    primary = Purple,
    onPrimary = White,
    background = White,
    surface = White,
    onSurface = Black,
    onSurfaceVariant = Gray,
)

val DarkColorScheme = darkColorScheme(
    primary = Purple,
    onPrimary = White,
    background = Black,
    surface = DarkGray,
    onSurface = White,
    onSurfaceVariant = LightGray,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookaroundTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if(darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    val activity = view.context as Activity
    val window = activity.window
    WindowCompat.getInsetsController(window, activity.window.decorView).apply {
        isAppearanceLightStatusBars = darkTheme.not()
        isAppearanceLightNavigationBars = darkTheme.not()
    }
    window.statusBarColor = colorScheme.background.toArgb()
    window.navigationBarColor = colorScheme.background.toArgb()

    CompositionLocalProvider(
        LocalRippleConfiguration provides null
    ) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}
