package io.github.halilozel1903.expandabletext.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier

/**
 * The compose-expandable-text sample: a social feed, a product page and an app's release notes and reviews, all
 * with bundled demo data. `scripts/screenshots.sh` starts it with `--es scene <scene>`:
 *
 * - `feed`: the feed with collapsed posts
 * - `expanded`: the feed with its first post expanded
 * - `fade`: the product page, whose description fades out
 * - `tablet-reviews`: release notes and reviews side by side (tablets in landscape)
 *
 * Without a scene it opens the feed on phones and the app page on wide screens.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val scene = Scene.from(intent.getStringExtra(EXTRA_SCENE))
        setContent {
            SampleTheme(dark = isSystemInDarkTheme()) {
                // The Surface makes text default to onBackground, so it stays readable in dark mode.
                Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
                    SampleApp(scene = scene)
                }
            }
        }
    }

    companion object {
        const val EXTRA_SCENE = "scene"
    }
}
