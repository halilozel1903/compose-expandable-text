package io.github.halilozel1903.expandabletext.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.delay

/**
 * Logs `SCENE_READY <scene>` once a few frames have been drawn and any height animation has finished, so
 * `scripts/screenshots.sh` captures a settled screen. The data is bundled, so there is nothing to load.
 */
@Composable
fun SceneReady(scene: Scene) {
    LaunchedEffect(scene) {
        repeat(3) { withFrameNanos { } }
        delay(1_500)
        logScene("SCENE_READY ${scene.key}")
    }
}
