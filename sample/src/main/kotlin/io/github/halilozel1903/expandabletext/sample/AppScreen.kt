package io.github.halilozel1903.expandabletext.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed as gridItemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.halilozel1903.expandabletext.ExpandableText
import io.github.halilozel1903.expandabletext.ExpandableTextLabels
import io.github.halilozel1903.expandabletext.rememberExpandableTextState

/**
 * An app store style page for "Demo Notes", a made-up app: release notes collapse to two lines and reviews to
 * three. Phones show one list; wide screens put the release notes beside a two column grid of reviews.
 */
@Composable
fun AppScreen(wide: Boolean, expandedReview: Int?, onToken: (String) -> Unit) {
    if (wide) {
        Row(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .weight(0.42f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            ) {
                AppHeader()
                Spacer(Modifier.height(24.dp))
                SectionTitle("What's new")
                Spacer(Modifier.height(8.dp))
                DemoApp.releases.forEach { release ->
                    ReleaseNotes(release, onToken)
                    Spacer(Modifier.height(16.dp))
                }
            }
            VerticalDivider()
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                contentPadding = PaddingValues(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp,
                modifier = Modifier.weight(0.58f).fillMaxHeight(),
            ) {
                item(key = "title", span = StaggeredGridItemSpan.FullLine) { RatingsHeader() }
                gridItemsIndexed(DemoApp.reviews, key = { _, review -> review.author }) { index, review ->
                    ReviewCard(review, initiallyExpanded = index == expandedReview)
                }
            }
        }
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "header") { AppHeader() }
                item(key = "whats-new") { SectionTitle("What's new", Modifier.padding(top = 8.dp)) }
                itemsIndexed(DemoApp.releases, key = { _, release -> release.version }) { _, release ->
                    ReleaseNotes(release, onToken)
                }
                item(key = "ratings") { RatingsHeader(Modifier.padding(top = 8.dp)) }
                itemsIndexed(DemoApp.reviews, key = { _, review -> review.author }) { index, review ->
                    ReviewCard(review, initiallyExpanded = index == expandedReview)
                }
            }
        }
    }
}

@Composable
private fun AppHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
        Box(
            Modifier.size(64.dp).background(Brush.linearGradient(colors), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                DemoIcons.Comment,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                modifier = Modifier.size(32.dp),
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(DemoApp.NAME, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(
                "${DemoApp.DEVELOPER} · a made-up app with demo reviews",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ReleaseNotes(release: Release, onToken: (String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(release.version, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.weight(1f))
            Text(release.date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(4.dp))
        ExpandableText(
            text = release.notes,
            collapsedMaxLines = 2,
            style = MaterialTheme.typography.bodyMedium,
            // Store pages usually have no "less"; the notes stay open.
            labels = ExpandableTextLabels(less = null),
            onUrlClick = { onToken("Demo link: $it") },
            onHashtagClick = { onToken("Demo tag: #$it") },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun RatingsHeader(modifier: Modifier = Modifier) {
    Column(modifier) {
        SectionTitle("Ratings and reviews")
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("4.5", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.width(12.dp))
            Column {
                Stars(5, size = 16.dp)
                Text(
                    "Demo reviews written for this sample",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review, initiallyExpanded: Boolean) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(review.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Stars(review.stars)
                Spacer(Modifier.width(8.dp))
                Text(
                    "${review.author} · ${review.date}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(8.dp))
            ExpandableText(
                text = review.text,
                collapsedMaxLines = 3,
                state = rememberExpandableTextState(initiallyExpanded),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
