package io.github.halilozel1903.expandabletext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Whether an [ExpandableText] is expanded. Create it with [rememberExpandableTextState] so it survives configuration
 * changes, process death and items scrolling out of a lazy list (which saves item state by key).
 */
@Stable
public class ExpandableTextState(initiallyExpanded: Boolean = false) {

    /** True while the whole text is shown. */
    public var expanded: Boolean by mutableStateOf(initiallyExpanded)

    /** Shows the whole text. */
    public fun expand() {
        expanded = true
    }

    /** Goes back to the collapsed line limit. */
    public fun collapse() {
        expanded = false
    }

    /** Expands a collapsed text and collapses an expanded one. */
    public fun toggle() {
        expanded = !expanded
    }

    public companion object {
        /** Saves the expanded flag. */
        public val Saver: Saver<ExpandableTextState, Boolean> = Saver<ExpandableTextState, Boolean>(
            save = { it.expanded },
            restore = { ExpandableTextState(it) },
        )
    }
}

/** An [ExpandableTextState] that is saved with [rememberSaveable]. */
@Composable
public fun rememberExpandableTextState(initiallyExpanded: Boolean = false): ExpandableTextState =
    rememberSaveable(saver = ExpandableTextState.Saver) { ExpandableTextState(initiallyExpanded) }
