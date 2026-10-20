package io.github.halilozel1903.expandabletext.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.halilozel1903.expandabletext.CollapsedStyle
import io.github.halilozel1903.expandabletext.ExpandableText
import io.github.halilozel1903.expandabletext.ExpandableTextLabels

/**
 * The demo product page. The description uses [CollapsedStyle.Fade]: four lines, the last one fading out, with
 * "Read more" under it. Shipping notes below use the inline label.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductScreen(wide: Boolean, onToken: (String) -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier
                .widthIn(max = if (wide) 720.dp else 640.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            ProductImage()
            Spacer(Modifier.height(16.dp))
            Text(DemoProduct.NAME, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    DemoProduct.PRICE,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(16.dp))
                Stars(5)
                Spacer(Modifier.width(6.dp))
                Text(
                    "${DemoProduct.RATING} · ${DemoProduct.REVIEWS}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(12.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DemoProduct.highlights.forEach { Chip(it) }
            }
            Spacer(Modifier.height(20.dp))
            SectionTitle("Product details")
            Spacer(Modifier.height(8.dp))
            ExpandableText(
                text = DemoProduct.DESCRIPTION,
                collapsedMaxLines = 4,
                collapsedStyle = CollapsedStyle.Fade,
                labels = ExpandableTextLabels(more = "Read more", less = "Read less"),
                labelStyle = androidx.compose.ui.text.SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                ),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))
            SectionTitle("Shipping and returns")
            Spacer(Modifier.height(8.dp))
            ExpandableText(
                text = "Demo shipping: free delivery in 2 to 4 working days, and free returns within 30 days " +
                    "of delivery. Pack the item in its original box, print the label from your account and drop " +
                    "it at any parcel point. Details at https://example.com/returns",
                collapsedMaxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                onUrlClick = { onToken("Demo link: $it") },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp))
            Button(onClick = { onToken("This is a demo; nothing is bought.") }, modifier = Modifier.fillMaxWidth()) {
                Text("Add to bag")
            }
        }
    }
}

@Composable
private fun ProductImage() {
    val colors = listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.tertiaryContainer)
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Brush.linearGradient(colors), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            DemoIcons.Bag,
            contentDescription = "Demo product illustration",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier.size(88.dp),
        )
    }
}

@Composable
private fun Chip(label: String) {
    Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.secondaryContainer) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        )
    }
}
