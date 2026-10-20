package io.github.halilozel1903.expandabletext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import io.github.halilozel1903.expandabletext.core.TextTokens
import io.github.halilozel1903.expandabletext.core.TokenKind

/**
 * Turns URLs, @mentions and #hashtags in [text] into [LinkAnnotation]s styled with [styles].
 *
 * - URLs become [LinkAnnotation.Url]s, which open in the browser, unless [onUrlClick] is set; then they call it.
 * - Mentions and hashtags become [LinkAnnotation.Clickable]s that call [onMentionClick] or [onHashtagClick] with the
 *   name without its `@` or `#`. Without a callback they are only styled.
 *
 * @param kinds the kinds of tokens to look for.
 */
public fun linkify(
    text: String,
    styles: TextLinkStyles?,
    kinds: Set<TokenKind> = TokenKind.entries.toSet(),
    onUrlClick: ((url: String) -> Unit)? = null,
    onMentionClick: ((name: String) -> Unit)? = null,
    onHashtagClick: ((tag: String) -> Unit)? = null,
): AnnotatedString = buildAnnotatedString {
    append(text)
    TextTokens.find(text, kinds).forEach { token ->
        when (token.kind) {
            TokenKind.Url -> {
                val listener = onUrlClick?.let { click -> LinkInteractionListener { click(token.value) } }
                addLink(LinkAnnotation.Url(token.value, styles, listener), token.start, token.end)
            }
            TokenKind.Mention, TokenKind.Hashtag -> {
                val click = if (token.kind == TokenKind.Mention) onMentionClick else onHashtagClick
                if (click != null) {
                    val tag = ExpandableTextDefaults.TOKEN_TAG_PREFIX + token.kind.name.lowercase() + ":" + token.value
                    addLink(
                        LinkAnnotation.Clickable(tag, styles) { click(token.value) },
                        token.start,
                        token.end,
                    )
                } else {
                    styles?.style?.let { addStyle(it, token.start, token.end) }
                }
            }
        }
    }
}

/**
 * [linkify], remembered for [text], [styles] and [kinds]. The callbacks may change between compositions without
 * rebuilding the string; the links always call the latest ones.
 */
@Composable
public fun rememberLinkifiedText(
    text: String,
    styles: TextLinkStyles?,
    kinds: Set<TokenKind> = TokenKind.entries.toSet(),
    onUrlClick: ((url: String) -> Unit)? = null,
    onMentionClick: ((name: String) -> Unit)? = null,
    onHashtagClick: ((tag: String) -> Unit)? = null,
): AnnotatedString {
    val url by rememberUpdatedState(onUrlClick)
    val mention by rememberUpdatedState(onMentionClick)
    val hashtag by rememberUpdatedState(onHashtagClick)
    // Whether each callback exists changes what is built; which lambda it is does not.
    val hasUrl = onUrlClick != null
    val hasMention = onMentionClick != null
    val hasHashtag = onHashtagClick != null
    return remember(text, styles, kinds, hasUrl, hasMention, hasHashtag) {
        linkify(
            text = text,
            styles = styles,
            kinds = kinds,
            onUrlClick = if (hasUrl) { value -> url?.invoke(value) } else null,
            onMentionClick = if (hasMention) { value -> mention?.invoke(value) } else null,
            onHashtagClick = if (hasHashtag) { value -> hashtag?.invoke(value) } else null,
        )
    }
}
