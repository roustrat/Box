package com.example.box.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.box.model.entities.PlaceItem
import com.example.box.model.entities.Item
import com.example.box.screens.PlaceList
import java.util.Collections.emptyList

class BoxViewModel(application: Application): ViewModel() {
    val PlaceList: LiveData<List<PlaceItem>>
    val ItemList: LiveData<List<Item>>

    fun currentItemsInPlace(placeID: Int): List<Item> {
        val list: MutableList<Item> = ItemList.value?.toMutableList() ?: emptyList()
        return list.filter { item ->
            item.placeID == placeID
        }
    }

    fun givePlaceNameFromId(id: Int): String {
        val list = PlaceList.value.filter {item ->
            item.id == id
        }
        return list[0].name
    }
}