package io.github.halilozel1903.expandabletext.core

/**
 * Where to cut a text so that a suffix such as "… more" fits on its last visible line.
 *
 * The functions only look at characters and ask a `fits` callback whether a given prefix plus the suffix still
 * fits in the collapsed line limit. The Compose module answers that with a `TextMeasurer`; tests answer it with a
 * fixed width per character. `fits` must be monotone: if a prefix fits, every shorter prefix fits as well.
 */
public object Truncation {

    /** Characters removed from the end of a cut before the ellipsis is added, besides whitespace. */
    public const val TRAILING_PUNCTUATION: String = ",;:.-–—([{/"

    /**
     * Returns the end (exclusive) of the prefix of [text] to show before the suffix.
     *
     * [lineStart] and [lineEnd] are the start and the visible end of the last line that fits in the collapsed line
     * limit when the whole text is laid out. The cut prefers the end of a word on that line ([breakCandidates]) and
     * takes the longest one that [fits]. When no word end fits (one long word or a URL fills the line, or the
     * suffix is wider than any word end leaves room for), it cuts between characters without splitting a surrogate
     * pair. When nothing on the line fits, the suffix starts the last line. The result never ends in whitespace or
     * [TRAILING_PUNCTUATION].
     */
    public fun findCut(text: CharSequence, lineStart: Int, lineEnd: Int, fits: (end: Int) -> Boolean): Int {
        require(lineStart in 0..text.length) { "lineStart $lineStart is outside 0..${text.length}" }
        val end = lineEnd.coerceIn(lineStart, text.length)

        val words = breakCandidates(text, lineStart, end)
            .map { trimEnd(text, it) }
            .filter { it > lineStart }
            .distinct()
        val byWord = lastFitting(words, fits)
        if (byWord != null) return byWord

        // No word end fits: cut between characters. Only worth it when it keeps something on the last line.
        val chars = (lineStart + 1..end).filter { !splitsSurrogatePair(text, it) }
        val byChar = lastFitting(chars, fits)?.let { trimEnd(text, it) }
        if (byChar != null && byChar > lineStart) return byChar

        return trimEnd(text, lineStart)
    }

    /**
     * Offsets in `(from, to]` where a word ends: the next character is whitespace or the end of the text, or the
     * offset follows a hyphen, a dash or a slash inside a word (where line breaking may also wrap). Ascending.
     */
    public fun breakCandidates(text: CharSequence, from: Int, to: Int): List<Int> {
        val start = from.coerceIn(0, text.length)
        val end = to.coerceIn(start, text.length)
        val result = ArrayList<Int>()
        for (i in start + 1..end) {
            val previous = text[i - 1]
            if (previous.isWhitespace()) continue
            val atWordEnd = i == text.length || text[i].isWhitespace()
            val afterBreakableMark = previous in BREAKABLE_MARKS && i < text.length && !text[i].isWhitespace()
            if (atWordEnd || afterBreakableMark) result += i
        }
        return result
    }

    /** Moves [end] back over whitespace and [TRAILING_PUNCTUATION], never below [floor]. */
    public fun trimEnd(text: CharSequence, end: Int, floor: Int = 0): Int {
        var i = end.coerceIn(0, text.length)
        while (i > floor && (text[i - 1].isWhitespace() || text[i - 1] in TRAILING_PUNCTUATION)) i--
        return i
    }

    /** Whether cutting at [index] would separate the two halves of a surrogate pair (an emoji, for example). */
    public fun splitsSurrogatePair(text: CharSequence, index: Int): Boolean =
        index in 1 until text.length && text[index - 1].isHighSurrogate() && text[index].isLowSurrogate()

    /** The largest of the ascending [candidates] that [fits], found by binary search, or null. */
    internal fun lastFitting(candidates: List<Int>, fits: (Int) -> Boolean): Int? {
        var low = 0
        var high = candidates.lastIndex
        var best: Int? = null
        while (low <= high) {
            val mid = (low + high) ushr 1
            if (fits(candidates[mid])) {
                best = candidates[mid]
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        return best
    }

    private const val BREAKABLE_MARKS = "-–—/"
}
