package com.noghre.sod.ui.accessibility

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics

fun Modifier.contentDescription(
    description: String,
    mergeDescendants: Boolean = false
): Modifier = this.semantics(mergeDescendants = mergeDescendants) {
    contentDescription = description
}

fun Modifier.clickableRole(
    label: String,
    role: Role = Role.Button
): Modifier = this.semantics {
    contentDescription = label
    this.role = role
}

fun Modifier.heading(): Modifier = this.semantics { heading() }

fun Modifier.imageDescription(description: String): Modifier =
    this.semantics {
        contentDescription = description
        role = Role.Image
    }
