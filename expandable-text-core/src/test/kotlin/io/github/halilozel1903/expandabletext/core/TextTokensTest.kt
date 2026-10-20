package io.github.halilozel1903.expandabletext.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TextTokensTest {

    @Test
    fun findsUrlsMentionsAndHashtagsInOrder() {
        val text = "Shipped with @demo_team! Notes at https://example.com/notes #release #v2"
        val tokens = TextTokens.find(text)
        assertEquals(
            listOf(
                TextToken(TokenKind.Mention, 13, 23, "demo_team"),
                TextToken(TokenKind.Url, 34, 59, "https://example.com/notes"),
                TextToken(TokenKind.Hashtag, 60, 68, "release"),
                TextToken(TokenKind.Hashtag, 69, 72, "v2"),
            ),
            tokens,
        )
        tokens.forEach { assertTrue(text.substring(it.start, it.end).isNotBlank()) }
    }

    @Test
    fun dropsSentencePunctuationAfterAUrl() {
        val text = "See https://example.com/a/b. Or (www.example.org/page)!"
        val urls = TextTokens.find(text, setOf(TokenKind.Url))
        assertEquals(listOf("https://example.com/a/b", "https://www.example.org/page"), urls.map { it.value })
        assertEquals("www.example.org/page", text.substring(urls[1].start, urls[1].end))
    }

    @Test
    fun keepsBalancedParenthesesInAUrl() {
        val text = "Wiki: https://example.com/Foo_(bar) done"
        assertEquals("https://example.com/Foo_(bar)", TextTokens.find(text).single().value)
    }

    @Test
    fun ignoresEmailsAndWordsWithHashes() {
        val text = "Mail hello@example.com about C# and &#35; or issue#12"
        assertEquals(emptyList(), TextTokens.find(text, setOf(TokenKind.Mention, TokenKind.Hashtag)))
    }

    @Test
    fun hashtagsNeedALetterAndSupportOtherScripts() {
        val text = "#2026 #çay #日本 #a1"
        assertEquals(listOf("çay", "日本", "a1"), TextTokens.find(text).map { it.value })
    }

    @Test
    fun mentionsDoNotEndWithADot() {
        val text = "Thanks @alex.dev. And @sam."
        assertEquals(listOf("alex.dev", "sam"), TextTokens.find(text).map { it.value })
    }

    @Test
    fun mentionsAndHashtagsInsideUrlsBelongToTheUrl() {
        val text = "Open https://example.com/@team/#section now"
        val tokens = TextTokens.find(text)
        assertEquals(listOf(TokenKind.Url), tokens.map { it.kind })
    }

    @Test
    fun aBareSchemeIsNotALink() {
        assertEquals(emptyList(), TextTokens.find("https:// and www. alone", setOf(TokenKind.Url)))
    }

    @Test
    fun filtersByKind() {
        val text = "@a #b https://c.example"
        assertEquals(listOf(TokenKind.Hashtag), TextTokens.find(text, setOf(TokenKind.Hashtag)).map { it.kind })
    }
}
