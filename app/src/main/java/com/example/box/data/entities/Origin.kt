package com.example.box.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import java.util.UUID

@Entity(tableName = "OriginList", primaryKeys = ["id"])
data class Origin(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "id") val id: UUID = UUID.randomUUID()
)
