package io.github.halilozel1903.expandabletext

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import io.github.halilozel1903.expandabletext.core.MeasureCache

/** How a collapsed [ExpandableText] shows that there is more. */
public enum class CollapsedStyle {
    /** The last visible line ends at a word with "… more" right after it. */
    InlineLabel,

    /** The last visible line fades out and the "more" label sits under the text. */
    Fade,
}

/**
 * The labels an [ExpandableText] shows.
 *
 * @property ellipsis drawn after the cut, in the text's own style.
 * @property separator between the ellipsis (or the expanded text) and the label.
 * @property more the label that expands the text.
 * @property less the label that collapses it again; null shows no label once expanded.
 */
@Immutable
public data class ExpandableTextLabels(
    val more: String = "more",
    val less: String? = "less",
    val ellipsis: String = "…",
    val separator: String = " ",
)

/**
 * What TalkBack says. The whole text is always read, collapsed or not; these are the state and the actions.
 *
 * @property collapsed the state description while collapsed.
 * @property expanded the state description while expanded.
 * @property expandAction the label of the expand (and click) action.
 * @property collapseAction the label of the collapse (and click) action.
 * @property openLink the label of the custom action for a link, mention or hashtag, from its text.
 */
@Immutable
public data class ExpandableTextDescriptions(
    val collapsed: String = "Collapsed",
    val expanded: String = "Expanded",
    val expandAction: String = "Show more",
    val collapseAction: String = "Show less",
    val openLink: (text: String) -> String = { "Open $it" },
)

/**
 * Remembers truncation results across [ExpandableText]s, so items that scroll out of a lazy list and back are
 * not measured again. Results are keyed by text, style, width, line limit, labels, density and layout direction.
 */
public class ExpandableTextCache(maxSize: Int = 256) {
    internal val entries: MeasureCache<TruncationKey, TruncationResult> = MeasureCache(maxSize)

    /** Forgets every result, for example after the app's fonts changed. */
    public fun clear() {
        entries.clear()
    }

    public companion object {
        /** The cache [LocalExpandableTextCache] provides unless you provide another one. */
        public val Shared: ExpandableTextCache = ExpandableTextCache()
    }
}

/** The [ExpandableTextCache] that [ExpandableText] uses. */
public val LocalExpandableTextCache: ProvidableCompositionLocal<ExpandableTextCache> =
    staticCompositionLocalOf { ExpandableTextCache.Shared }

/** Defaults for [ExpandableText]. */
public object ExpandableTextDefaults {

    /** The tag of the "more" and "less" link annotations. */
    public const val LABEL_TAG: String = "expandable-text-label"

    /** The tag prefix of link annotations made by [linkify]: `url`, `mention` or `hashtag` follows it. */
    public const val TOKEN_TAG_PREFIX: String = "expandable-text-"

    /** The "more" and "less" label: semibold in the theme's secondary text color. */
    @Composable
    public fun labelStyle(): SpanStyle = SpanStyle(
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
    )

    /** Links, mentions and hashtags: the primary color, underlined while pressed. */
    @Composable
    public fun linkStyles(): TextLinkStyles {
        val base = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        return TextLinkStyles(
            style = base,
            pressedStyle = base.copy(textDecoration = TextDecoration.Underline),
        )
    }

    /** A soft spring for the height change. It snaps when the user turned animations off. */
    public val AnimationSpec: FiniteAnimationSpec<IntSize> =
        spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow)

    /** The fade of [CollapsedStyle.Fade]: [Dp.Unspecified] fades the whole last line. */
    public val FadeLength: Dp = Dp.Unspecified
}
