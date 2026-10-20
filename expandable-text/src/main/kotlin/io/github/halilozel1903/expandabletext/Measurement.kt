package io.github.halilozel1903.expandabletext

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import io.github.halilozel1903.expandabletext.core.Truncation

/** Everything a truncation result depends on. Link listeners are left out, so new lambdas still hit the cache. */
internal data class TruncationKey(
    val text: String,
    val spanStyles: List<AnnotatedString.Range<SpanStyle>>,
    val paragraphStyles: List<AnnotatedString.Range<androidx.compose.ui.text.ParagraphStyle>>,
    val style: TextStyle,
    val maxWidth: Int,
    val maxLines: Int,
    val suffix: String,
    val suffixStyle: SpanStyle,
    val density: Float,
    val fontScale: Float,
    val layoutDirection: LayoutDirection,
)

/**
 * @property overflows whether the text needs more than the collapsed line limit.
 * @property cut where the collapsed prefix ends, before the suffix.
 * @property lastLineHeight the height of the last collapsed line in pixels, for the fade.
 */
internal data class TruncationResult(val overflows: Boolean, val cut: Int, val lastLineHeight: Float)

/**
 * The text as it is drawn: [androidx.compose.foundation.text.BasicText] applies link styles itself, so a
 * [TextMeasurer] would measure a bold link narrower than it is drawn. This adds each link's style as a span.
 */
internal fun AnnotatedString.withLinkStyles(): AnnotatedString {
    val links = getLinkAnnotations(0, length)
    if (links.isEmpty()) return this
    return buildAnnotatedString {
        append(this@withLinkStyles)
        links.forEach { range -> range.item.styles?.style?.let { addStyle(it, range.start, range.end) } }
    }
}

/** The suffix after the cut: the ellipsis and separator in the text style, then the label in [labelStyle]. */
internal fun measuredSuffix(labels: ExpandableTextLabels, labelStyle: SpanStyle): AnnotatedString = buildAnnotatedString {
    append(labels.ellipsis)
    append(labels.separator)
    withStyle(labelStyle) { append(labels.more) }
}

internal fun measureTruncation(
    measurer: TextMeasurer,
    text: AnnotatedString,
    suffix: AnnotatedString,
    style: TextStyle,
    maxWidth: Int,
    maxLines: Int,
    layoutDirection: LayoutDirection,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver,
): TruncationResult {
    val constraints = if (maxWidth == Constraints.Infinity) Constraints() else Constraints(maxWidth = maxWidth)
    fun layout(value: AnnotatedString): TextLayoutResult = measurer.measure(
        text = value,
        style = style,
        overflow = TextOverflow.Clip,
        softWrap = true,
        maxLines = maxLines,
        constraints = constraints,
        layoutDirection = layoutDirection,
        density = density,
        fontFamilyResolver = fontFamilyResolver,
        skipCache = true,
    )

    val full = layout(text)
    val lastLine = (minOf(maxLines, full.lineCount) - 1).coerceAtLeast(0)
    val lastLineHeight = if (full.lineCount == 0) 0f else full.getLineBottom(lastLine) - full.getLineTop(lastLine)
    if (!full.hasVisualOverflow) return TruncationResult(overflows = false, cut = text.length, lastLineHeight)

    val lineStart = full.getLineStart(lastLine)
    val lineEnd = full.getLineEnd(lastLine, visibleEnd = true)
    val cut = Truncation.findCut(text.text, lineStart, lineEnd) { end ->
        val candidate = buildAnnotatedString {
            append(text.subSequence(0, end))
            append(suffix)
        }
        !layout(candidate).hasVisualOverflow
    }
    return TruncationResult(overflows = true, cut = cut, lastLineHeight = lastLineHeight)
}
