package io.github.halilozel1903.expandabletext.core

/**
 * A small thread safe least-recently-used cache. The Compose module keeps truncation results in one, so a post that
 * scrolls out of a lazy list and back is not measured again.
 */
public class MeasureCache<K : Any, V : Any>(public val maxSize: Int) {

    init {
        require(maxSize > 0) { "maxSize must be positive, was $maxSize" }
    }

    private val map = object : LinkedHashMap<K, V>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean = size > maxSize
    }

    /** The number of cached entries. */
    public val size: Int get() = synchronized(map) { map.size }

    /** The value for [key], which also marks it as recently used, or null. */
    public operator fun get(key: K): V? = synchronized(map) { map[key] }

    /** Stores [value] for [key], evicting the least recently used entry when the cache is full. */
    public operator fun set(key: K, value: V) {
        synchronized(map) { map[key] = value }
    }

    /**
     * Returns the cached value for [key], or computes, stores and returns it. [compute] runs outside the lock, so two
     * threads may compute the same key; both get a correct value.
     */
    public inline fun getOrPut(key: K, compute: () -> V): V = get(key) ?: compute().also { set(key, it) }

    /** Removes every entry. */
    public fun clear() {
        synchronized(map) { map.clear() }
    }
}
