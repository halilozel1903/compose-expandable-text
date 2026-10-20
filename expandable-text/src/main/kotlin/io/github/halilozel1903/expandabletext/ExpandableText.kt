package io.github.halilozel1903.expandabletext

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.github.halilozel1903.expandabletext.core.TokenKind

/**
 * Text that shows at most [collapsedMaxLines] lines and expands to show the rest.
 *
 * The collapsed text is measured with a `TextMeasurer` and cut at the end of a word, so "… more" fits on the last
 * line. Tapping the label (or, with [toggleOnTextClick], anywhere outside a link) expands the text, and the height
 * animates. Links, mentions and hashtags in the [AnnotatedString] keep working; build them with [linkify] or use the
 * `String` overload. Results are kept in [LocalExpandableTextCache], so lazy lists scroll without measuring again.
 *
 * TalkBack reads the whole text, collapsed or not, with an expanded or collapsed state, expand and collapse
 * actions, and a custom action for every link.
 *
 * @param text the text, with any link annotations.
 * @param collapsedMaxLines how many lines to show while collapsed.
 * @param state whether the text is expanded; saved across configuration changes and lazy list scrolling.
 * @param style the text style; it defaults to [LocalTextStyle].
 * @param color the text color; [Color.Unspecified] uses the style's color or the content color.
 * @param collapsedStyle an inline "… more" label, or a fade with the label under the text.
 * @param labels the "more" and "less" labels.
 * @param labelStyle the style of the labels.
 * @param animationSpec how the height changes. It snaps when the user turned animations off.
 * @param fadeLength how far the fade of [CollapsedStyle.Fade] reaches; unspecified fades the whole last line.
 * @param toggleOnTextClick whether a tap anywhere on the text (except on a link) toggles it.
 * @param descriptions what TalkBack says for the state and actions.
 * @param onExpandedChange called after the user expanded or collapsed the text.
 */
@Composable
public fun ExpandableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 3,
    state: ExpandableTextState = rememberExpandableTextState(),
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    collapsedStyle: CollapsedStyle = CollapsedStyle.InlineLabel,
    labels: ExpandableTextLabels = ExpandableTextLabels(),
    labelStyle: SpanStyle = ExpandableTextDefaults.labelStyle(),
    animationSpec: FiniteAnimationSpec<IntSize> = ExpandableTextDefaults.AnimationSpec,
    fadeLength: Dp = ExpandableTextDefaults.FadeLength,
    toggleOnTextClick: Boolean = true,
    descriptions: ExpandableTextDescriptions = ExpandableTextDescriptions(),
    onExpandedChange: ((expanded: Boolean) -> Unit)? = null,
) {
    require(collapsedMaxLines >= 1) { "collapsedMaxLines must be at least 1, was $collapsedMaxLines" }
    val mergedStyle = if (color == Color.Unspecified) style else style.merge(TextStyle(color = color))
    val spec = if (animationsDisabled()) snap() else animationSpec
    val measurer = rememberTextMeasurer(cacheSize = 4)
    val cache = LocalExpandableTextCache.current
    val uriHandler = LocalUriHandler.current
    val onChange by rememberUpdatedState(onExpandedChange)
    val toggle: () -> Unit = remember(state) {
        {
            state.toggle()
            onChange?.invoke(state.expanded)
        }
    }

    BoxWithConstraints(modifier.animateContentSize(spec)) {
        val maxWidth = constraints.maxWidth
        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current
        val fontFamilyResolver = LocalFontFamilyResolver.current

        val measuredText = remember(text) { text.withLinkStyles() }
        val suffix = remember(labels, labelStyle) { measuredSuffix(labels, labelStyle) }
        val key = TruncationKey(
            text = measuredText.text,
            spanStyles = measuredText.spanStyles,
            paragraphStyles = measuredText.paragraphStyles,
            style = mergedStyle,
            maxWidth = maxWidth,
            maxLines = collapsedMaxLines,
            // The fade style puts the label under the text, so nothing is appended to the last line.
            suffix = if (collapsedStyle == CollapsedStyle.Fade) "" else suffix.text,
            suffixStyle = labelStyle,
            density = density.density,
            fontScale = density.fontScale,
            layoutDirection = layoutDirection,
        )
        val result = remember(key) {
            cache.entries.getOrPut(key) {
                measureTruncation(
                    measurer = measurer,
                    text = measuredText,
                    suffix = if (collapsedStyle == CollapsedStyle.Fade) AnnotatedString("") else suffix,
                    style = mergedStyle,
                    maxWidth = maxWidth,
                    maxLines = collapsedMaxLines,
                    layoutDirection = layoutDirection,
                    density = density,
                    fontFamilyResolver = fontFamilyResolver,
                )
            }
        }
        val overflows = result.overflows
        val expanded = state.expanded && overflows

        val linkActions = remember(text, descriptions, uriHandler) {
            text.getLinkAnnotations(0, text.length).map { range ->
                val label = text.text.substring(range.start, range.end)
                CustomAccessibilityAction(descriptions.openLink(label)) {
                    val link = range.item
                    val listener = link.linkInteractionListener
                    when {
                        listener != null -> listener.onClick(link)
                        link is LinkAnnotation.Url -> uriHandler.openUri(link.url)
                    }
                    true
                }
            }
        }

        val fullText = remember(text) { AnnotatedString(text.text) }
        val semanticsModifier = Modifier.clearAndSetSemantics {
            // The whole text, even while collapsed, without the ellipsis and the label.
            this.text = fullText
            if (linkActions.isNotEmpty()) customActions = linkActions
            if (overflows) {
                stateDescription = if (expanded) descriptions.expanded else descriptions.collapsed
                onClick(label = if (expanded) descriptions.collapseAction else descriptions.expandAction) {
                    toggle()
                    true
                }
                if (expanded) {
                    collapse(descriptions.collapseAction) { toggle(); true }
                } else {
                    expand(descriptions.expandAction) { toggle(); true }
                }
            }
        }
        val tapModifier = if (overflows && toggleOnTextClick) {
            // Links consume their own taps, so a tap on a link never toggles the text.
            Modifier.pointerInput(toggle) { detectTapGestures { toggle() } }
        } else {
            Modifier
        }

        when (collapsedStyle) {
            CollapsedStyle.InlineLabel -> {
                val display = remember(text, result, expanded, labels, labelStyle, toggle) {
                    val labelLink = LinkAnnotation.Clickable(
                        tag = ExpandableTextDefaults.LABEL_TAG,
                        styles = TextLinkStyles(style = labelStyle),
                    ) { toggle() }
                    when {
                        !overflows -> text
                        expanded && labels.less == null -> text
                        expanded -> buildAnnotatedString {
                            append(text)
                            append(labels.separator)
                            withLink(labelLink) { append(labels.less.orEmpty()) }
                        }
                        else -> buildAnnotatedString {
                            append(text.subSequence(0, result.cut))
                            append(labels.ellipsis)
                            append(labels.separator)
                            withLink(labelLink) { append(labels.more) }
                        }
                    }
                }
                Text(
                    text = display,
                    style = mergedStyle,
                    // The cut already fits; the limit only guards against a font that changed since measuring.
                    maxLines = if (expanded || !overflows) Int.MAX_VALUE else collapsedMaxLines,
                    overflow = TextOverflow.Clip,
                    modifier = semanticsModifier.then(tapModifier),
                )
            }

            CollapsedStyle.Fade -> {
                val fadePx = with(density) { if (fadeLength == Dp.Unspecified) result.lastLineHeight else fadeLength.toPx() }
                val fade = if (overflows && !expanded) {
                    Modifier
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    1f to Color.Black,
                                    startY = size.height - fadePx,
                                    endY = size.height,
                                ),
                                blendMode = BlendMode.DstOut,
                            )
                        }
                } else {
                    Modifier
                }
                Column(semanticsModifier.then(tapModifier)) {
                    Text(
                        text = text,
                        style = mergedStyle,
                        maxLines = if (expanded || !overflows) Int.MAX_VALUE else collapsedMaxLines,
                        overflow = TextOverflow.Clip,
                        modifier = fade,
                    )
                    val label = if (expanded) labels.less else labels.more
                    if (overflows && label != null) {
                        Text(
                            text = buildAnnotatedString { withStyle(labelStyle) { append(label) } },
                            style = mergedStyle,
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .clickable(role = Role.Button, onClick = toggle),
                        )
                    }
                }
            }
        }
    }
}

/**
 * [ExpandableText] for plain text, with URLs, @mentions and #hashtags turned into links (see [linkify]).
 *
 * URLs open in the browser unless [onUrlClick] is set. Mentions and hashtags are highlighted, and clickable when
 * [onMentionClick] or [onHashtagClick] is set. Pass `linkKinds = emptySet()` for plain text.
 */
@Composable
public fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier,
    collapsedMaxLines: Int = 3,
    state: ExpandableTextState = rememberExpandableTextState(),
    style: TextStyle = LocalTextStyle.current,
    color: Color = Color.Unspecified,
    collapsedStyle: CollapsedStyle = CollapsedStyle.InlineLabel,
    labels: ExpandableTextLabels = ExpandableTextLabels(),
    labelStyle: SpanStyle = ExpandableTextDefaults.labelStyle(),
    linkStyles: TextLinkStyles = ExpandableTextDefaults.linkStyles(),
    linkKinds: Set<TokenKind> = TokenKind.entries.toSet(),
    onUrlClick: ((url: String) -> Unit)? = null,
    onMentionClick: ((name: String) -> Unit)? = null,
    onHashtagClick: ((tag: String) -> Unit)? = null,
    animationSpec: FiniteAnimationSpec<IntSize> = ExpandableTextDefaults.AnimationSpec,
    fadeLength: Dp = ExpandableTextDefaults.FadeLength,
    toggleOnTextClick: Boolean = true,
    descriptions: ExpandableTextDescriptions = ExpandableTextDescriptions(),
    onExpandedChange: ((expanded: Boolean) -> Unit)? = null,
) {
    val annotated = rememberLinkifiedText(
        text = text,
        styles = linkStyles,
        kinds = linkKinds,
        onUrlClick = onUrlClick,
        onMentionClick = onMentionClick,
        onHashtagClick = onHashtagClick,
    )
    ExpandableText(
        text = annotated,
        modifier = modifier,
        collapsedMaxLines = collapsedMaxLines,
        state = state,
        style = style,
        color = color,
        collapsedStyle = collapsedStyle,
        labels = labels,
        labelStyle = labelStyle,
        animationSpec = animationSpec,
        fadeLength = fadeLength,
        toggleOnTextClick = toggleOnTextClick,
        descriptions = descriptions,
        onExpandedChange = onExpandedChange,
    )
}
