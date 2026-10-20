package io.github.halilozel1903.expandabletext.core

/** The kinds of tokens [TextTokens] finds. */
public enum class TokenKind {
    /** `https://…`, `http://…` or `www.…` */
    Url,

    /** `@name`: letters, digits, underscores and inner dots. Not the `@` of an email address. */
    Mention,

    /** `#tag`: letters, digits and underscores in any script, with at least one letter. */
    Hashtag,
}

/**
 * A token found in a text.
 *
 * @property start the first character, including the `@` or `#`.
 * @property end the end, exclusive.
 * @property value what the token points to: the URL (with `https://` added to a `www.` address), or the mention or
 * hashtag without its `@` or `#`.
 */
public data class TextToken(val kind: TokenKind, val start: Int, val end: Int, val value: String)

/** Finds URLs, @mentions and #hashtags in plain text. */
public object TextTokens {

    private val url = Regex("""(?i)\b(?:https?://|www\.)[^\s<>"]+""")
    private val mention = Regex("""(?<![\p{L}\p{N}_@.])@([A-Za-z0-9_](?:[A-Za-z0-9_.]*[A-Za-z0-9_])?)""")
    private val hashtag = Regex("""(?<![\p{L}\p{N}_#&/])#([\p{L}\p{N}_]*\p{L}[\p{L}\p{N}_]*)""")

    /** Characters a URL does not end with; they usually belong to the sentence around it. */
    private const val URL_TRAILING = ".,;:!?'\"*"

    /**
     * Returns the tokens of the requested [kinds] in [text], sorted by start and never overlapping. A mention or a
     * hashtag inside a URL belongs to the URL.
     */
    public fun find(text: String, kinds: Set<TokenKind> = TokenKind.entries.toSet()): List<TextToken> {
        val urls = url.findAll(text).mapNotNull { trimUrl(text, it.range.first, it.range.last + 1) }.toList()
        val result = ArrayList<TextToken>()
        if (TokenKind.Url in kinds) result += urls
        val insideUrl = { start: Int -> urls.any { start >= it.start && start < it.end } }
        if (TokenKind.Mention in kinds) {
            mention.findAll(text).forEach { match ->
                val start = match.range.first
                if (!insideUrl(start)) {
                    result += TextToken(TokenKind.Mention, start, match.range.last + 1, match.groupValues[1])
                }
            }
        }
        if (TokenKind.Hashtag in kinds) {
            hashtag.findAll(text).forEach { match ->
                val start = match.range.first
                if (!insideUrl(start)) {
                    result += TextToken(TokenKind.Hashtag, start, match.range.last + 1, match.groupValues[1])
                }
            }
        }
        return result.sortedBy { it.start }
    }

    /** Drops sentence punctuation and an unbalanced closing parenthesis from the end of a URL match. */
    private fun trimUrl(text: String, start: Int, matchEnd: Int): TextToken? {
        var end = matchEnd
        while (end > start) {
            val last = text[end - 1]
            val unbalancedParen = last == ')' &&
                text.substring(start, end).count { it == ')' } > text.substring(start, end).count { it == '(' }
            if (last in URL_TRAILING || unbalancedParen) end-- else break
        }
        val raw = text.substring(start, end)
        // "www." alone, or "https://" with nothing after it, is not a link.
        val host = raw.substringAfter("://", raw).removePrefix("www.").removePrefix("WWW.")
        if (host.isEmpty() || !host.first().isLetterOrDigit()) return null
        val value = if (raw.startsWith("www.", ignoreCase = true)) "https://$raw" else raw
        return TextToken(TokenKind.Url, start, end, value)
    }
}
