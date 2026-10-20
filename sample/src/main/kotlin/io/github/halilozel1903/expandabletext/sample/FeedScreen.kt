package io.github.halilozel1903.expandabletext.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.halilozel1903.expandabletext.ExpandableText
import io.github.halilozel1903.expandabletext.rememberExpandableTextState

/**
 * The demo feed: posts collapse to three lines with an inline "… more". Each post's expanded state is saved by its
 * key, so it stays expanded after scrolling away and back.
 */
@Composable
fun FeedScreen(wide: Boolean, expandedPostId: String?, onToken: (String) -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "header") {
                ScreenHeader("Demo feed", "Bundled demo posts. Tap a post, a link or a tag.")
            }
            items(DemoFeed.posts, key = { it.id }) { post ->
                PostCard(post, initiallyExpanded = post.id == expandedPostId, onToken = onToken)
            }
        }
    }
}

@Composable
private fun PostCard(post: Post, initiallyExpanded: Boolean, onToken: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(post.author)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(post.author, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Text(
                        "${post.handle} · ${post.time}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.size(10.dp))
            ExpandableText(
                text = post.text,
                collapsedMaxLines = 3,
                state = rememberExpandableTextState(initiallyExpanded),
                style = MaterialTheme.typography.bodyLarge,
                onUrlClick = { onToken("Demo link: $it") },
                onMentionClick = { onToken("Demo profile: @$it") },
                onHashtagClick = { onToken("Demo tag: #$it") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.size(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Counter(DemoIcons.Heart, post.likes.toString())
                Counter(DemoIcons.Comment, post.comments.toString())
                Counter(DemoIcons.Share, "Share")
            }
        }
    }
}

@Composable
private fun Avatar(name: String) {
    val initials = name.split(' ').mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
    val colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
    Box(
        Modifier.size(40.dp).background(Brush.linearGradient(colors), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun Counter(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            icon,
            contentDescription = null,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
