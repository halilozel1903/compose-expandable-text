package io.github.halilozel1903.expandabletext.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TruncationTest {

    /** A line of [width] characters on the last line: the prefix from [lineStart] plus the suffix must fit. */
    private fun lastLineFits(lineStart: Int, suffix: String, width: Int): (Int) -> Boolean =
        { end -> (end - lineStart) + suffix.length <= width }

    @Test
    fun cutsAtTheLastWordThatLeavesRoomForTheSuffix() {
        val text = "The quick brown fox jumps over the lazy dog"
        // One line of 20 characters; "… more" is 6.
        val cut = Truncation.findCut(text, lineStart = 0, lineEnd = 19, fits = lastLineFits(0, "… more", 20))
        // "The quick brown… more" would be 21 characters.
        assertEquals("The quick", text.substring(0, cut))
    }

    @Test
    fun keepsTheWholeLineWhenTheSuffixFitsAfterIt() {
        val text = "Short line here and more after"
        val cut = Truncation.findCut(text, 0, 15, lastLineFits(0, "…", 20))
        assertEquals("Short line here", text.substring(0, cut))
    }

    @Test
    fun cutsALongUrlAtASlash() {
        val text = "https://example.com/a/really/long/path/that/never/ends and then words"
        // Slashes count as word ends, so the URL is cut after a path segment rather than mid-word.
        val cut = Truncation.findCut(text, 0, 30, lastLineFits(0, "… more", 30))
        assertEquals("https://example.com/a", text.substring(0, cut))
    }

    @Test
    fun fallsBackToCharactersWithoutAnyBreak() {
        val text = "Supercalifragilisticexpialidocious and more"
        val cut = Truncation.findCut(text, 0, 20, lastLineFits(0, "… more", 20))
        assertEquals("Supercalifragi", text.substring(0, cut))
    }

    @Test
    fun putsTheSuffixAtTheLineStartWhenNothingFits() {
        val text = "First line.\nSecond line goes on"
        // Last line starts at 12; a suffix as wide as the line leaves no room.
        val cut = Truncation.findCut(text, 12, 31, lastLineFits(12, "x".repeat(20), 20))
        // Trimmed back over the newline and the period of the first line.
        assertEquals("First line", text.substring(0, cut))
    }

    @Test
    fun trimsTrailingPunctuationAndSpaces() {
        val text = "Hello, world; again"
        assertEquals(5, Truncation.trimEnd(text, 7))
        assertEquals(12, Truncation.trimEnd(text, 13))
        assertEquals(3, Truncation.trimEnd("ab.  ", 5, floor = 3))
    }

    @Test
    fun breakCandidatesAreWordEndsAndBreakableMarks() {
        val text = "one two-three  four/five"
        assertEquals(listOf(3, 8, 13, 20, 24), Truncation.breakCandidates(text, 0, text.length))
        assertEquals(listOf(8, 13), Truncation.breakCandidates(text, 4, 13))
    }

    @Test
    fun neverSplitsASurrogatePair() {
        val text = "😀😀😀😀😀" // five emoji, ten chars
        val cut = Truncation.findCut(text, 0, text.length, lastLineFits(0, "…", 6))
        assertEquals(4, cut)
        assertTrue(!Truncation.splitsSurrogatePair(text, cut))
        assertTrue(Truncation.splitsSurrogatePair(text, 5))
    }

    @Test
    fun usesBinarySearch() {
        val text = (1..200).joinToString(" ") { "w$it" }
        var calls = 0
        Truncation.findCut(text, 0, text.length) { end ->
            calls++
            end <= 300
        }
        assertTrue(calls <= 10, "expected a binary search, got $calls calls")
    }

    @Test
    fun endToEndWithGreedyWrapping() {
        val text = "Morning run along the river. Ten kilometres, new personal best, and the coffee afterwards " +
            "was even better. Thanks for the company everyone!"
        val width = 24
        val maxLines = 3
        val suffix = "… more"
        val lines = wrap(text, width)
        assertTrue(lines.size > maxLines)
        val (lineStart, lineEnd) = lines[maxLines - 1]
        val cut = Truncation.findCut(text, lineStart, lineEnd) { end -> wrap(text.substring(0, end) + suffix, width).size <= maxLines }

        val collapsed = text.substring(0, cut) + suffix
        assertEquals("Morning run along the river. Ten kilometres, new personal best… more", collapsed)
        assertTrue(wrap(collapsed, width).size <= maxLines)
        // One more word would not fit.
        val next = Truncation.breakCandidates(text, cut + 1, text.length).first()
        assertTrue(wrap(text.substring(0, next) + suffix, width).size > maxLines)
    }

    @Test
    fun lastFittingFindsTheLargest() {
        assertEquals(7, Truncation.lastFitting(listOf(1, 3, 5, 7, 9)) { it < 8 })
        assertEquals(null, Truncation.lastFitting(listOf(4, 5)) { it < 2 })
        assertEquals(null, Truncation.lastFitting(emptyList()) { true })
    }

    /** Greedy word wrapping at [width] characters, like a text layout with a monospace font. Returns (start, visible end). */
    private fun wrap(text: String, width: Int): List<Pair<Int, Int>> {
        val lines = ArrayList<Pair<Int, Int>>()
        var start = 0
        while (start < text.length) {
            var end = minOf(start + width, text.length)
            if (end < text.length && !text[end].isWhitespace()) {
                val space = text.lastIndexOf(' ', end - 1)
                if (space > start) end = space
            }
            var visibleEnd = end
            while (visibleEnd > start && text[visibleEnd - 1].isWhitespace()) visibleEnd--
            lines += start to visibleEnd
            start = end
            while (start < text.length && text[start] == ' ') start++
        }
        return lines
    }
}
