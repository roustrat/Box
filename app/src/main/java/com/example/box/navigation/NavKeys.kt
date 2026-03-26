package com.example.box.navigation

import android.graphics.Bitmap
import androidx.navigation3.runtime.NavKey
import com.example.box.model.BoxViewModel
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.UUID

@Serializable
data object Home : NavKey

@Serializable
data class ItemInfo(val actionType: Int) : NavKey

@Serializable
data object Image : NavKey

@Serializable
data object CameraX : NavKey

@Serializable
data object CameraXAnother : NavKey

//@Serializable
//data object Logo : NavKey

// Custom serializer
// https://stackoverflow.com/questions/65398284/kotlin-serialization-serializer-has-not-been-found-for-type-uuid
object UUIDSerializer : KSerializer<UUID> {
    override val descriptor = PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): UUID {
        return UUID.fromString(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: UUID) {
        encoder.encodeString(value.toString())
    }
}


