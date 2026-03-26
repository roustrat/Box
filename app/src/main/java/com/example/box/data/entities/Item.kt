package com.example.box.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "ItemList", primaryKeys = ["id"])
data class Item(
    @ColumnInfo(name = "id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "properties") var properties: String,
    @ColumnInfo(name = "placeID") var placeID: UUID
) {
    val photoFileName
        get() = "IMG_$id.jpg"
}
