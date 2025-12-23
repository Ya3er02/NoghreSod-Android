package com.noghre.sod.ui.animation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically

fun fadeInTransition(): EnterTransition = fadeIn()

fun fadeOutTransition(): ExitTransition = fadeOut()

fun slideInFromRightTransition(): EnterTransition =
    slideInHorizontally(initialOffsetX = { it })

fun slideInFromLeftTransition(): EnterTransition =
    slideInHorizontally(initialOffsetX = { -it })

fun slideInFromBottomTransition(): EnterTransition =
    slideInVertically(initialOffsetY = { it })

fun slideOutToRightTransition(): ExitTransition =
    slideOutHorizontally(targetOffsetX = { it })

fun slideOutToLeftTransition(): ExitTransition =
    slideOutHorizontally(targetOffsetX = { -it })

fun slideOutToBottomTransition(): ExitTransition =
    slideOutVertically(targetOffsetY = { it })
