package io.github.halilozel1903.expandabletext.sample

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** A violet theme with a coral accent for the Expandable Text sample. */
@Composable
fun SampleTheme(dark: Boolean, content: @Composable () -> Unit) {
    val colors = if (dark) {
        darkColorScheme(
            primary = Color(0xFFC9BFFF),
            onPrimary = Color(0xFF2E2276),
            primaryContainer = Color(0xFF453A8E),
            onPrimaryContainer = Color(0xFFE5DEFF),
            secondary = Color(0xFFC9C3DC),
            onSecondary = Color(0xFF312E41),
            secondaryContainer = Color(0xFF484459),
            onSecondaryContainer = Color(0xFFE5DFF9),
            tertiary = Color(0xFFFFB4A6),
            onTertiary = Color(0xFF5F160B),
            tertiaryContainer = Color(0xFF7E2B1F),
            onTertiaryContainer = Color(0xFFFFDAD3),
            background = Color(0xFF141318),
            onBackground = Color(0xFFE5E1E9),
            surface = Color(0xFF141318),
            onSurface = Color(0xFFE5E1E9),
            surfaceVariant = Color(0xFF47454F),
            onSurfaceVariant = Color(0xFFC9C5D0),
            surfaceContainerLowest = Color(0xFF0F0D13),
            surfaceContainerLow = Color(0xFF1C1B20),
            surfaceContainer = Color(0xFF201F25),
            surfaceContainerHigh = Color(0xFF2B292F),
            surfaceContainerHighest = Color(0xFF36343A),
            outline = Color(0xFF928F99),
            outlineVariant = Color(0xFF47454F),
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF5D52A7),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE5DEFF),
            onPrimaryContainer = Color(0xFF190A60),
            secondary = Color(0xFF5F5B71),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE5DFF9),
            onSecondaryContainer = Color(0xFF1C192B),
            tertiary = Color(0xFF9C4234),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFFDAD3),
            onTertiaryContainer = Color(0xFF3E0400),
            background = Color(0xFFFCF8FF),
            onBackground = Color(0xFF1C1B20),
            surface = Color(0xFFFCF8FF),
            onSurface = Color(0xFF1C1B20),
            surfaceVariant = Color(0xFFE5E0EC),
            onSurfaceVariant = Color(0xFF47454F),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF6F2FA),
            surfaceContainer = Color(0xFFF0ECF4),
            surfaceContainerHigh = Color(0xFFEAE6EE),
            surfaceContainerHighest = Color(0xFFE5E1E9),
            outline = Color(0xFF78757F),
            outlineVariant = Color(0xFFC9C5D0),
        )
    }
    MaterialTheme(colorScheme = colors, content = content)
}
