package com.example.box.model

import android.app.Application
import android.content.Context
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.CameraSelector.DEFAULT_FRONT_CAMERA
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.box.model.entities.PlaceItem
import com.example.box.model.entities.Item
import com.example.box.model.entities.Origin
import com.example.box.screens.PlaceList
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.util.Collections.emptyList

class BoxViewModel(application: Application): ViewModel() {

    val OriginList: LiveData<List<Origin>>
    val PlaceList: LiveData<List<PlaceItem>>
    val ItemList: LiveData<List<Item>>

    private val currentItem: Item = Item(
        name = "",
        properties = "",
        placeID = 0
    )

    fun currentItemsInPlace(placeID: Int): List<Item> {
        val list: MutableList<Item> = ItemList.value?.toMutableList() ?: emptyList()
        return list.filter { item ->
            item.placeID == placeID
        }
    }

    // Пока с !!
    fun givePlaceNameFromId(id: Int): String {
        val list = PlaceList.value!!.filter { item ->
            item.id == id
        }
        return list[0].name
    }

    // Пока с !!
    fun giveOriginFromId(id: Int): Origin {
        val list = OriginList.value!!.filter { item ->
            item.id == id
        }
        return list[0]
    }

    // Пока с !!
    fun givePlaceFromId(id: Int): PlaceItem {
        val list = PlaceList.value!!.filter { item ->
            item.id == id
        }
        return list[0]
    }

    // Пока с !!
    fun giveItemFromId(id: Int): Item {
        val list = ItemList.value!!.filter { item ->
            item.id == id
        }
        return list[0]
    }

    fun givePlacesFromOrigin(id: Int): List<PlaceItem> {
        val list = PlaceList.value!!.filter { item ->
            item.originId == id
        }
        return list
    }

    fun insertItemName(name: String) {
        currentItem.name = name
    }

    fun insertItemProperties(properties: String) {
        currentItem.properties = properties
    }

    fun insertItemPlaceID(placeID: Int) {
        currentItem.placeID = placeID
    }

    fun getItemId(): Int {
        return currentItem.id
    }

    fun getItemName(): String {
        return currentItem.name
    }

    fun insertCurrentItemIntoBD() {

    }
    fun checkCurrentItem(): Int {
        return when {
            currentItem.name == "" -> 1
            currentItem.placeID == 0 -> 2
            else -> 0
        }
    }

    // CameraX section
    // https://medium.com/androiddevelopers/getting-started-with-camerax-in-jetpack-compose-781c722ca0c4

    // used to set up a link between the Camera and your UI.
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest
    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
    private var cameraControl: CameraControl? = null

    private val cameraPreviewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider { newSurfaceRequest ->
            _surfaceRequest.update { newSurfaceRequest }
            surfaceMeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                newSurfaceRequest.resolution.width.toFloat(),
                newSurfaceRequest.resolution.height.toFloat()
            )
        }
    }

    suspend fun bindToCamera(appContext: Context, lifecycleOwner: LifecycleOwner) {
        val processCameraProvider = ProcessCameraProvider.awaitInstance(appContext)
        val camera = processCameraProvider.bindToLifecycle(
            lifecycleOwner, DEFAULT_BACK_CAMERA, cameraPreviewUseCase
        )
        cameraControl = camera.cameraControl

        // Cancellation signals we're done with the camera
        try { awaitCancellation() } finally {
            processCameraProvider.unbindAll()
            cameraControl = null
        }
    }

    fun tapToFocus(tapCoords: Offset) {
        val point = surfaceMeteringPointFactory?.createPoint(tapCoords.x, tapCoords.y)
        if (point != null) {
            val meteringAction = FocusMeteringAction.Builder(point).build()
            cameraControl?.startFocusAndMetering(meteringAction)
        }
    }

    fun getPhotoFile(id: Int): File {
        return File()
    }
}