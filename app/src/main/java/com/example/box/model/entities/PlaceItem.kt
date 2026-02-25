package com.example.box.model.entities

import java.util.UUID

data class PlaceItem(
    val name: String,
    val id: Int = 0,
    val imgId: UUID = UUID.randomUUID(),
    val origin: String
)
