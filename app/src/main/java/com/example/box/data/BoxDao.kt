package com.example.box.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.box.data.entities.Item
import com.example.box.data.entities.Origin
import com.example.box.data.entities.PlaceItem
import java.util.UUID

@Dao
interface BoxDao {
    @Insert
    fun insertItem(item: Item)

    @Insert
    fun insertPlaceItem(placeItem: PlaceItem)

    @Insert
    fun insertOrigin(origin: Origin)

//    @Delete
//    fun deleteItem(item: Item)
//
//    @Delete
//    fun deletePlaceItem(placeItem: PlaceItem)
//
//    @Delete
//    fun deleteOrigin(origin: Origin)

    @Query(value = "SELECT * FROM ItemList")
    fun getAllItems(): LiveData<List<Item>>

    @Query(value = "SELECT * FROM PlaceList")
    fun getAllPlaces(): LiveData<List<PlaceItem>>

    @Query(value = "SELECT * FROM OriginList")
    fun getAllOrigins(): LiveData<List<Origin>>

    @Query(value = "SELECT * FROM ItemList WHERE name =:name")
    fun findItem(name: String): List<Item>
}