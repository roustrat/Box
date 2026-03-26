package com.example.box.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.UUID

@Entity(tableName = "PlaceList", primaryKeys = ["id"])
data class PlaceItem(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "originId") val originId: UUID
)
