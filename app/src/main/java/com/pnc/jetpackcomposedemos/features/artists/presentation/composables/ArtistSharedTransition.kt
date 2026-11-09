package com.pnc.jetpackcomposedemos.features.artists.presentation.composables

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

//the two scopes a shared element needs
//passing them through artistdirectory -> artistList -> artistcard
//would change every signature, so we will pass them through compositionLocals instead

val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }
val LocalPaneAnimatedScope = compositionLocalOf<AnimatedVisibilityScope?> {null}

//marks an image as the same element on the list card and on the details pane, so it animates between them
//outside a sharedTransitionLayout (ex: previews, ui test) it does nothing
@Composable
fun Modifier.artistImageSharedElement(artistId:Int):Modifier{
    val sharedScope = LocalSharedTransitionScope.current ?: return this
    val animatedScope = LocalPaneAnimatedScope.current ?: return this

    return with(sharedScope){
        this@artistImageSharedElement.sharedElement(
            sharedContentState = rememberSharedContentState(key = "artists-image-$artistId"),
            animatedVisibilityScope = animatedScope
        )

    }

}