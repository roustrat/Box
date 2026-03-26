package com.example.box.model

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.box.data.BoxDao
import com.example.box.data.BoxDatabase
import com.example.box.data.BoxRepository
import com.example.box.data.entities.PlaceItem
import com.example.box.data.entities.Item
import com.example.box.data.entities.Origin
import com.example.box.utils.getScaledBitmap
import java.io.File
import java.util.Collections.emptyList
import java.util.UUID

class BoxViewModel(application: Application): ViewModel() {

    private val repository: BoxRepository
    val originList: LiveData<List<Origin>>
    val placeList: LiveData<List<PlaceItem>>
    val itemList: LiveData<List<Item>>

    private val filesDir = application.applicationContext.filesDir

    init {
        val boxDB: BoxDatabase = BoxDatabase.getInstance(application)
        val boxDao: BoxDao = boxDB.boxDao()
        repository = BoxRepository(boxDao = boxDao)

        originList = repository.allOrigins
        placeList = repository.allPlaces
        itemList = repository.allItems
    }
    private var currentItem: Item = Item(
        name = "",
        properties = "",
        placeID = UUID.randomUUID()
    )

    var currentPhotoFile: File? by mutableStateOf(null)
    var currentPlaceIdState: UUID? by mutableStateOf(null )

    fun currentItemsInPlace(placeID: UUID): List<Item> {
        val list: MutableList<Item> = itemList.value?.toMutableList() ?: emptyList()
        return list.filter { item ->
            item.placeID == placeID
        }
    }

    // Пока с !!
    fun givePlaceNameFromId(id: UUID?): String {
        if (id == null) {
          return ""
        } else {
            val list = placeList.value?.filter { item ->
                item.id == id
            }
            return list?.get(0)?.name ?: ""
        }


    }

    // Пока с !!
    fun giveOriginFromId(id: UUID): Origin {
        val list = originList.value!!.filter { item ->
            item.id == id
        }
        return list[0]
    }

    // Пока с !!
    fun givePlaceFromId(id: UUID): PlaceItem {
        val list = placeList.value!!.filter { item ->
            item.id == id
        }
        return list[0]
    }

    // Пока с !!
    fun giveItemFromId(id: UUID): Item {
        val list = itemList.value!!.filter { item ->
            item.id == id
        }
        return list[0]
    }

    fun givePlacesFromOrigin(id: UUID): List<PlaceItem> {
        val list = placeList.value!!.filter { item ->
            item.originId == id
        }
        return list
    }

    fun takeItemForEdit(id: UUID) {
        val list = itemList.value!!.filter { item ->
            item.id == id
        }
        currentItem = list[0]
    }
    fun insertItemName(name: String) {
        currentItem.name = name
    }

    fun insertItemProperties(properties: String) {
        currentItem.properties = properties
    }

    fun insertItemPlaceID(placeID: UUID) {
        currentItem.placeID = placeID
    }

    fun getItemId(): UUID {
        return currentItem.id
    }

    fun getItemName(): String {
        return currentItem.name
    }

    fun getItemProperties(): String {
        return currentItem.properties
    }
    fun getPhotoFileName(): String {
        return currentItem.photoFileName
    }

    fun insertCurrentItemIntoBD() {
        repository.insertItem(currentItem)
        currentItem = Item(
            name = "",
            properties = "",
            placeID = UUID.randomUUID()
        )
    }

    fun setCurrentItem(id: UUID) {
        val list = itemList.value!!.filter { item ->
            item.id == id
        }
        currentItem = list[0]
    }

    fun resetCurrentItem() {
        currentItem = Item(
            name = "",
            properties = "",
            placeID = UUID.randomUUID()
        )
    }
    fun checkCurrentItem(): Int {
        return when {
            currentItem.name == "" -> 1
            givePlaceFromId(currentItem.placeID).name == "" -> 2
            else -> 0
        }
    }

    // CameraX section
    // https://medium.com/androiddevelopers/getting-started-with-camerax-in-jetpack-compose-781c722ca0c4

    // used to set up a link between the Camera and your UI.
//    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
//    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest
//    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
//    private var cameraControl: CameraControl? = null
//
//    private val cameraPreviewUseCase = Preview.Builder().build().apply {
//        setSurfaceProvider { newSurfaceRequest ->
//            _surfaceRequest.update { newSurfaceRequest }
//            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
//                newSurfaceRequest.resolution.width.toFloat(),
//                newSurfaceRequest.resolution.height.toFloat()
//            )
//        }
//    }
//
//    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
//        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
//        val camera = processCameraProvider.bindToLifecycle(
//            lifecycleOwner, DEFAULT_BACK_CAMERA, cameraPreviewUseCase
//        )
//        cameraControl = camera.cameraControl
//
//        // Cancellation signals we're done with the camera
//        try { awaitCancellation() } finally {
//            processCameraProvider.unbindAll()
//            cameraControl = null
//        }
//    }
//
//    fun tapToFocus(tapCoords: Offset) {
//        val point = surfaceMeteringPointFactory?.createPoint(tapCoords.x, tapCoords.y)
//        if (point != null) {
//            val meteringAction = FocusMeteringAction.Builder(point).build()
//            cameraControl?.startFocusAndMetering(meteringAction)
//        }
//    }

    fun setPhotoFile(file: File?) {
        val item = currentItem
        currentPhotoFile = file
    }

    // C четверкой обезопасить от !!
    fun getCurrentFile(): File {
        return currentPhotoFile!!
    }

//    fun setBitmap(bitmap: Bitmap) {
//        currentBitmap = bitmap
//    }

    fun setBitmapFromFile() {

    }
}