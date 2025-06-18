package com.livefront.parcelableserializersample.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.livefront.parcelableserializersample.sealedclass.SealedClassRoute
import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

fun NavGraphBuilder.homeDestination(
    onSealedClassClick: (SealedClassRoute) -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onSealedClassClick = onSealedClassClick
        )
    }
}
