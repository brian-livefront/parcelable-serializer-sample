package com.livefront.parcelableserializersample.sealedclass

import android.os.Parcelable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.livefront.parcelableserializersample.serializer.ParcelableSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable(with = SealedClassRoute.Serializer::class)
sealed class SealedClassRoute : Parcelable {
    class Serializer : ParcelableSerializer<SealedClassRoute>(SealedClassRoute::class)

    @Parcelize
    @Serializable(with = Type1.Serializer::class)
    data object Type1 : SealedClassRoute() {
        class Serializer : ParcelableSerializer<Type1>(Type1::class)
    }

    @Parcelize
    @Serializable(with = Type2.Serializer::class)
    data class Type2(
        val data: String
    ) : SealedClassRoute() {
        class Serializer : ParcelableSerializer<Type2>(Type2::class)
    }
}

fun NavGraphBuilder.sealedClassDestinationType1() {
    composable<SealedClassRoute.Type1> {
        // Same screen/VM, different route
        SealedClassScreen()
    }
}

fun NavGraphBuilder.sealedClassDestinationType2() {
    composable<SealedClassRoute.Type2> {
        // Same screen/VM, different route
        SealedClassScreen()
    }
}

fun NavHostController.navigateToSealedClass(sealedClassRoute: SealedClassRoute) {
    this.navigate(sealedClassRoute)
}
