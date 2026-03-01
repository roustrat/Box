package com.example.box.model.entities

import java.util.UUID

data class Item(
    val id: Int = 0,
    val imgId: UUID = UUID.randomUUID(),
    var name: String,
    var properties: String,
    var placeID: Int
) {
    val photoFileName
        get() = "IMG_$imgId.jpg"
}
