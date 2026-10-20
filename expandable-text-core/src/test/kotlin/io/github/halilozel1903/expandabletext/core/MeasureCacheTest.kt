package io.github.halilozel1903.expandabletext.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class MeasureCacheTest {

    @Test
    fun evictsTheLeastRecentlyUsedEntry() {
        val cache = MeasureCache<String, Int>(maxSize = 2)
        cache["a"] = 1
        cache["b"] = 2
        cache["a"] // a is now the most recently used
        cache["c"] = 3
        assertEquals(1, cache["a"])
        assertNull(cache["b"])
        assertEquals(3, cache["c"])
        assertEquals(2, cache.size)
    }

    @Test
    fun getOrPutComputesOnce() {
        val cache = MeasureCache<Int, String>(maxSize = 4)
        var computed = 0
        repeat(3) { cache.getOrPut(7) { computed++; "seven" } }
        assertEquals(1, computed)
        cache.clear()
        assertEquals(0, cache.size)
    }

    @Test
    fun rejectsANonPositiveSize() {
        assertFailsWith<IllegalArgumentException> { MeasureCache<String, String>(0) }
    }
}
