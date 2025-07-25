package com.livefront.parcelableserializersample.serializer

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Base64
import androidx.core.os.ParcelCompat
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlin.reflect.KClass

/**
 * A custom [KSerializer] for serializing and deserializing [Parcelable] classes.
 *
 * This serializer is compatible with Jetpack Compose type-safe navigation routes. It allows for
 * complex [Parcelable] types to be easily serialized without needing to specify a `NavType` for
 * all non-primitive properties and properties nested within those types.
 *
 * For example:
 *
 * ```
 * @Parcelize
 * @Serializable(with = CustomRoute.Serializer::class)
 * data class CustomRoute(
 *     val data: Data,
 * ): Parcelable {
 *     object Serializer : ParcelableSerializer<CustomRoute>(CustomRoute::class)
 * }
 * ```
 *
 * where `Data` is a complex type implementing `Parcelable`.
 *
 * In addition, this serializer provides support for directly serializing to the parent types of a
 * sealed class when using `SavedStateHandle.toRoute()`. In order to achieve this while also
 * ensuring each route is unique, a  subclass of this serializer should be defined for each parent
 * and child type.
 *
 * Given the following type:
 *
 * ```
 * @Parcelize
 * @Serializable(with = Parent.Serializer::class)
 * sealed class Parent : Parcelable {
 *     object Serializer : ParcelableSerializer<Parent>(Parent::class)
 *
 *     @Parcelize
 *     @Serializable(with = Parent.Child1::class)
 *     data class Child1(
 *         val data1: Data1,
 *     ) : Parent() {
 *         object Serializer : ParcelableSerializer<Child1>(Child1::class)
 *     }
 *
 *     @Parcelize
 *     @Serializable(with = Child2.Serializer::class)
 *     data class Child2(
 *         val data2: Data2,
 *     ) : Parent() {
 *         object Serializer : ParcelableSerializer<Child2>(Child2::class)
 *     }
 * ```
 *
 * the route information for a navigation to the `Child1` destination could be derived using both:
 *
 * ```
 * savedStateHandle.toRoute<Parent.Child1>()
 * ```
 *
 * as well as
 *
 * ```
 * when (savedStateHandle.toRoute<Parent>()) {
 *     is Child1 -> // ...
 *     is Child2 -> // ...
 * }
 * ```
 *
 * The latter is useful in cases where the same `ViewModel` is used to handle these routes.
 */
open class ParcelableSerializer<T : Parcelable>(
    private val kClass: KClass<T>
) : KSerializer<T> {

    override val descriptor: SerialDescriptor
        get() = buildClassSerialDescriptor(
            serialName = kClass.qualifiedName!!,
        ) {
            element<String>("encodedData")
        }

    override fun deserialize(decoder: Decoder): T =
        decoder.decodeStructure(descriptor) {
            var encodedString: String? = null
            while (true) {
                when (decodeElementIndex(descriptor)) {
                    0 -> encodedString = decodeStringElement(descriptor, 0)
                    else -> break
                }
            }
            if (encodedString == null) {
                throw IllegalStateException(
                    "Invalid decoding for ${kClass.qualifiedName}.\n" +
                        "Encoded data is missing. Decoding attempted for data not first encoded " +
                        "with this serializer."
                )
            }
            encodedString
                .toParcelable<T>()
                ?: throw IllegalStateException(
                    "Invalid decoding for ${kClass.qualifiedName}.\n" +
                        "Encoded data cannot be decoded into the given type."
                )
        }

    override fun serialize(encoder: Encoder, value: T) {
        val valueAsString = value.toEncodedString()
        encoder.encodeStructure(descriptor) {
            encodeStringElement(descriptor, 0, valueAsString)
        }
    }

    // Helpers for encoding Parcelable data

    private fun Parcelable.toBytes(): ByteArray {
        val parcelable = this
        val parcel = Parcel.obtain().apply {
            writeParcelable(parcelable, 0)
        }
        return parcel
            .marshall()
            .also { parcel.recycle() }
    }

    private fun Parcelable.toEncodedString(): String =
        Base64.encodeToString(toBytes(), Base64.URL_SAFE)

    private fun <T> ByteArray.toParcelable(): T? {
        val bytes = this
        val parcel = Parcel.obtain().apply {
            unmarshall(bytes, 0, bytes.size)
            setDataPosition(0)
        }
        @Suppress("UNCHECKED_CAST")
        val value = try {
            ParcelCompat.readParcelable(
                parcel,
                ParcelableSerializer::class.java.classLoader,
                kClass.java,
            ) as T?
        } catch (_: IllegalArgumentException) {
            null
        } catch (_: IllegalStateException) {
            null
        }
        parcel.recycle()
        return value
    }

    private fun <T> String.toParcelable(): T? = Base64.decode(this, Base64.URL_SAFE).toParcelable()
}
