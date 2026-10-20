package io.github.halilozel1903.expandabletext.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** A screen title with a small "demo data" line under it. */
@Composable
fun ScreenHeader(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

/** A section title inside a screen. */
@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = modifier)
}

/** Five stars, [count] of them filled. */
@Composable
fun Stars(count: Int, size: Dp = 14.dp, color: Color = MaterialTheme.colorScheme.tertiary) {
    Row(Modifier.semantics(mergeDescendants = true) { contentDescription = "$count of 5 stars" }) {
        repeat(5) { index ->
            Image(
                DemoIcons.Star,
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (index < count) color else MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.size(size),
            )
        }
    }
}
