package io.github.halilozel1903.expandabletext.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * A bottom bar on phones and a navigation rail on wide screens. Taps on links, mentions and hashtags show a
 * snackbar instead of opening anything, because every link in the demo data points at example.com.
 */
@Composable
fun SampleApp(scene: Scene?) {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val onToken: (String) -> Unit = { message ->
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar(message)
        }
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 840.dp
        var tab by rememberSaveable { mutableStateOf(scene?.tab ?: if (wide) Tab.App else Tab.Feed) }
        if (scene != null) SceneReady(scene)
        if (wide) {
            Scaffold(snackbarHost = { SnackbarHost(snackbar) }, contentWindowInsets = WindowInsets(0)) { padding ->
                Row(Modifier.fillMaxSize().padding(padding).windowInsetsPadding(WindowInsets.safeDrawing)) {
                    NavigationRail(Modifier.fillMaxHeight()) {
                        Tab.entries.forEach { entry ->
                            NavigationRailItem(
                                selected = tab == entry,
                                onClick = { tab = entry },
                                icon = { TabIcon(entry) },
                                label = { Text(entry.label) },
                            )
                        }
                    }
                    VerticalDivider()
                    Box(Modifier.fillMaxSize()) { TabContent(tab, scene, wide = true, onToken = onToken) }
                }
            }
        } else {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbar) },
                bottomBar = {
                    NavigationBar {
                        Tab.entries.forEach { entry ->
                            NavigationBarItem(
                                selected = tab == entry,
                                onClick = { tab = entry },
                                icon = { TabIcon(entry) },
                                label = { Text(entry.label) },
                            )
                        }
                    }
                },
            ) { padding ->
                Box(Modifier.fillMaxSize().padding(padding)) { TabContent(tab, scene, wide = false, onToken = onToken) }
            }
        }
    }
}

@Composable
private fun TabContent(tab: Tab, scene: Scene?, wide: Boolean, onToken: (String) -> Unit) {
    when (tab) {
        Tab.Feed -> FeedScreen(
            wide = wide,
            expandedPostId = if (scene == Scene.Expanded) DemoFeed.posts.first().id else null,
            onToken = onToken,
        )
        Tab.Product -> ProductScreen(wide = wide, onToken = onToken)
        Tab.App -> AppScreen(
            wide = wide,
            expandedReview = if (scene == Scene.TabletReviews) 1 else null,
            onToken = onToken,
        )
    }
}

@Composable
private fun TabIcon(tab: Tab) {
    val icon: ImageVector = when (tab) {
        Tab.Feed -> DemoIcons.Feed
        Tab.Product -> DemoIcons.Bag
        Tab.App -> DemoIcons.Apps
    }
    Image(icon, contentDescription = null, colorFilter = ColorFilter.tint(LocalContentColor.current), modifier = Modifier.size(24.dp))
}
