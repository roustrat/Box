package com.example.box.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.box.data.entities.Item
import com.example.box.data.entities.Origin
import com.example.box.data.entities.PlaceItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BoxRepository (private val boxDao: BoxDao) {

    // Идея из книги Neil Smyth
    val allItems: LiveData<List<Item>> = boxDao.getAllItems()
    val allPlaces: LiveData<List<PlaceItem>> = boxDao.getAllPlaces()
    val allOrigins: LiveData<List<Origin>> = boxDao.getAllOrigins()
    val searchItems = MutableLiveData<List<Item>>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertItem(newItem: Item)  {
        coroutineScope.launch(Dispatchers.IO) {
            boxDao.insertItem(newItem)
        }
    }

    fun insertPlace(newPlace: PlaceItem)  {
        coroutineScope.launch(Dispatchers.IO) {
            boxDao.insertPlaceItem(newPlace)
        }
    }

    fun insertOrigin(newOrigin: Origin)  {
        coroutineScope.launch(Dispatchers.IO) {
            boxDao.insertOrigin(newOrigin)
        }
    }

    fun findItem(name: String) {
        coroutineScope.launch(Dispatchers.IO) {
            searchItems.value = asyncFind(name).await()
        }
    }

//    fun deleteItem(item: Item) {
//        coroutineScope.launch(Dispatchers.IO) {
//            boxDao.deleteItem(item)
//        }
//    }
//
//    fun deletePlace(placeItem: PlaceItem) {
//        coroutineScope.launch(Dispatchers.IO) {
//            boxDao.deletePlaceItem(placeItem)
//        }
//    }
//
//    fun deleteOrigin(origin: Origin) {
//        coroutineScope.launch(Dispatchers.IO) {
//            boxDao.deleteOrigin(origin = origin)
//        }
//    }

    // Изучить этот способ более глубже
    private fun asyncFind(name: String): Deferred<List<Item>?> =
        coroutineScope.async(Dispatchers.IO) {
            return@async boxDao.findItem(name)
        }

}