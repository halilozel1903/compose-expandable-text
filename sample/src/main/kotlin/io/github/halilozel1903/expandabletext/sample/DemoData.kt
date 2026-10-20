package io.github.halilozel1903.expandabletext.sample

/*
 * Demo content for the sample. Everything here is invented and generic: demo people in a demo feed, a demo product
 * and the release notes and reviews of "Demo Notes", an app that doesn't exist. Links point at example.com.
 */

/** A post in the demo feed. */
data class Post(
    val id: String,
    val author: String,
    val handle: String,
    val time: String,
    val text: String,
    val likes: Int,
    val comments: Int,
)

object DemoFeed {
    val posts = listOf(
        Post(
            id = "p1",
            author = "Alex Demo",
            handle = "@alex.demo",
            time = "2h",
            text = "Finally finished the weekend trail loop with @sam_demo and the whole running club. " +
                "Twelve kilometres, two steep climbs and one very muddy descent, but the view from the ridge made " +
                "every step worth it. We stopped at the old bridge for photos, shared far too many snacks and " +
                "still made it back before the rain. Route notes and the elevation profile are up at " +
                "https://example.com/trails/ridge-loop if you want to try it. Next month: the lake route! " +
                "#trailrunning #weekend #demo",
            likes = 128,
            comments = 14,
        ),
        Post(
            id = "p2",
            author = "Sam Demo",
            handle = "@sam_demo",
            time = "4h",
            text = "Tried a new bread recipe today: an overnight dough with a little rye, folded four times " +
                "and baked in a hot pot. The crust crackled for minutes after it came out of the oven, and the " +
                "crumb was open and soft. Thanks @chef.demo for the tip about steam. Writing the full method " +
                "up this week. #baking #sourdough",
            likes = 342,
            comments = 51,
        ),
        Post(
            id = "p3",
            author = "Riley Demo",
            handle = "@riley.demo",
            time = "Yesterday",
            text = "Short one today: the community garden needs volunteers this Saturday morning.",
            likes = 57,
            comments = 3,
        ),
        Post(
            id = "p4",
            author = "Jordan Demo",
            handle = "@jordan_demo",
            time = "Yesterday",
            text = "Reading list for the train this week: a long essay on city parks, a short novel about a " +
                "lighthouse keeper and a well-written guide to keeping houseplants alive through winter. I keep " +
                "the list at www.example.org/reading and add to it every Sunday, so send me your favourites. " +
                "#books #reading",
            likes = 89,
            comments = 22,
        ),
        Post(
            id = "p5",
            author = "Casey Demo",
            handle = "@casey.demo",
            time = "2d",
            text = "Moved the desk next to the window and it changed everything. Morning light, a plant on " +
                "the sill and a lamp for the evenings. Small changes, big difference to how the day feels. " +
                "Before and after photos in the thread below. #workspace #home",
            likes = 211,
            comments = 18,
        ),
    )
}

/** The demo product page. */
object DemoProduct {
    const val NAME = "Trail Pack 28 (demo)"
    const val PRICE = "€89.00"
    const val RATING = "4.6"
    const val REVIEWS = "1,204 demo ratings"

    const val DESCRIPTION =
        "A light daypack for long walks and short trips. The main compartment opens wide, so you can see " +
            "everything at once, and a padded sleeve holds a laptop of up to 15 inches or a water bladder.\n\n" +
            "The back panel is a breathable mesh over a curved frame that keeps the pack off your back on warm " +
            "days. Shoulder straps are shaped and padded, with a sternum strap and a removable hip belt with " +
            "two zipped pockets.\n\n" +
            "Outside there are two stretch bottle pockets, a front pocket for a rain jacket and loops for " +
            "poles. The fabric is recycled and water resistant, and a rain cover packs into the base."

    val highlights = listOf("28 litres", "860 g", "Recycled fabric", "Rain cover")
}

/** A release of the made-up "Demo Notes" app. */
data class Release(val version: String, val date: String, val notes: String)

/** A review of the made-up "Demo Notes" app. */
data class Review(val author: String, val stars: Int, val date: String, val title: String, val text: String)

object DemoApp {
    const val NAME = "Demo Notes"
    const val DEVELOPER = "Demo Studio"

    val releases = listOf(
        Release(
            version = "Version 4.2",
            date = "Oct 14",
            notes = "Folders now sync across all your devices, including notes you moved while offline. " +
                "Search finds words inside attached PDFs. The editor keeps your place when you rotate the screen, " +
                "dark mode looks better on tablets, and we fixed a crash when sharing a long checklist. " +
                "Full notes at https://example.com/notes/4.2 #changelog",
        ),
        Release(
            version = "Version 4.1",
            date = "Sep 30",
            notes = "New home screen widgets in three sizes, a quick action to start a voice note and faster " +
                "start up on older phones. Reminders now repeat on weekdays only if you want them to.",
        ),
        Release(
            version = "Version 4.0",
            date = "Sep 2",
            notes = "A fresh design with bigger text, a new tablet layout with the list and the note side by " +
                "side, shared notebooks for families and teams, and an export to Markdown. Thanks to everyone " +
                "who tested the beta!",
        ),
    )

    val reviews = listOf(
        Review(
            author = "Demo reviewer 1",
            stars = 5,
            date = "Oct 15",
            title = "Finally, sync that just works",
            text = "I switch between a phone and a tablet all day and the new folder sync has been flawless. " +
                "Notes I write on the train are on the tablet before I sit down at my desk. The search inside " +
                "PDFs is a lovely bonus, I use it for receipts and manuals.",
        ),
        Review(
            author = "Demo reviewer 2",
            stars = 4,
            date = "Oct 12",
            title = "Great widgets",
            text = "The medium widget shows my three pinned notes and a button for a new one, which is all I " +
                "need. One star off because I would love a dark widget that follows the system theme. Otherwise " +
                "it is the notes app I recommend to everyone.",
        ),
        Review(
            author = "Demo reviewer 3",
            stars = 5,
            date = "Oct 9",
            title = "Tablet layout is excellent",
            text = "Side by side list and note on the tablet is exactly what I wanted.",
        ),
        Review(
            author = "Demo reviewer 4",
            stars = 3,
            date = "Oct 3",
            title = "Good, but give me tags",
            text = "Folders are fine but I think in tags. Shared notebooks work well for our family shopping " +
                "list, and the checklist sharing crash is gone in 4.2. If tags arrive this gets five stars from " +
                "me, and I would happily pay for a yearly plan.",
        ),
        Review(
            author = "Demo reviewer 5",
            stars = 5,
            date = "Sep 28",
            title = "Voice notes are a game changer",
            text = "The quick action for a voice note from the home screen means I capture ideas while walking " +
                "the dog. Transcripts are accurate and land in the right notebook. Battery use is low too.",
        ),
        Review(
            author = "Demo reviewer 6",
            stars = 4,
            date = "Sep 20",
            title = "Clean and fast",
            text = "Starts quickly even on my old phone since 4.1. The Markdown export is handy for writing " +
                "blog drafts. Would like more font choices in the editor and a way to lock a single note.",
        ),
    )
}
