package com.example.box.model.entities

import java.util.UUID

data class Item(
    val id: Int = 0,
    val imgId: UUID = UUID.randomUUID(),
    val name: String,
    val properties: String,
    val placeID: Int
) {
    val photoFileName
        get() = "IMG_$imgId.jpg"
}
