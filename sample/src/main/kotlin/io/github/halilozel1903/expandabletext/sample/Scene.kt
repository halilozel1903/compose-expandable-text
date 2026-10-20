package io.github.halilozel1903.expandabletext.sample

/** Screenshot scenes, picked with the `scene` intent extra. */
enum class Scene(val key: String, val tab: Tab) {
    /** The demo feed with every post collapsed to three lines and an inline "… more". */
    Feed("feed", Tab.Feed),

    /** The demo feed with the first post expanded, showing its links, mentions and hashtags and a "less" label. */
    Expanded("expanded", Tab.Feed),

    /** The demo product page, whose description uses the fade style. */
    Fade("fade", Tab.Product),

    /** Release notes and reviews of the made-up app side by side (tablets in landscape). */
    TabletReviews("tablet-reviews", Tab.App),
    ;

    companion object {
        fun from(key: String?): Scene? = entries.firstOrNull { it.key == key }
    }
}

/** The sample's three screens. */
enum class Tab(val label: String) {
    Feed("Feed"),
    Product("Product"),
    App("App"),
}
