package io.github.halilozel1903.expandabletext.sample

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/** Small icons drawn on a 24 unit grid, so the sample needs no icon library. They are tinted where they are used. */
object DemoIcons {
    val Feed = icon("Feed", "M4 4h16v4H4V4zm0 6h16v4H4v-4zm0 6h10v4H4v-4z")
    val Bag = icon("Bag", "M7 7V6a5 5 0 0 1 10 0v1h3l-1 15H5L4 7h3zm2 0h6V6a3 3 0 0 0-6 0v1z")
    val Apps = icon("Apps", "M4 4h7v7H4V4zm9 0h7v7h-7V4zM4 13h7v7H4v-7zm9 0h7v7h-7v-7z")
    val Heart = icon("Heart", "M12 21l-1.5-1.3C5.4 15.1 2 12.1 2 8.4 2 5.4 4.4 3 7.4 3c1.7 0 3.4.8 4.6 2.1C13.2 3.8 14.9 3 16.6 3 19.6 3 22 5.4 22 8.4c0 3.7-3.4 6.7-8.5 11.3L12 21z")
    val Comment = icon("Comment", "M3 4h18v13H7l-4 4V4zm2 2v10.2L6.2 15H19V6H5z")
    val Share = icon("Share", "M18 16a3 3 0 0 0-2.2 1l-7-3.6a3 3 0 0 0 0-2.8l7-3.6A3 3 0 1 0 15 5l-7 3.6a3 3 0 1 0 0 6.8l7 3.6A3 3 0 1 0 18 16z")
    val Star = icon("Star", "M12 2l3 6.9 7.5.7-5.7 5 1.7 7.4L12 18.1 5.5 22l1.7-7.4-5.7-5 7.5-.7L12 2z")
    val Check = icon("Check", "M9 16.2l-4.2-4.2L3.4 13.4 9 19 21 7l-1.4-1.4L9 16.2z")

    private fun icon(name: String, pathData: String): ImageVector =
        ImageVector.Builder(name = name, defaultWidth = 24.dp, defaultHeight = 24.dp, viewportWidth = 24f, viewportHeight = 24f)
            .addPath(pathData = addPathNodes(pathData), pathFillType = PathFillType.EvenOdd, fill = SolidColor(Color.Black))
            .build()
}
