package com.example.box.camerax

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.example.box.model.BoxViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class CameraPreviewViewModel : ViewModel() {
    // used to set up a link between the Camera and your UI.
    private val _surfaceRequest = MutableStateFlow<SurfaceRequest?>(null)
    val surfaceRequest: StateFlow<SurfaceRequest?> = _surfaceRequest
    private var surfaceMeteringPointFactory: SurfaceOrientedMeteringPointFactory? = null
    private var cameraControl: CameraControl? = null

    private var lensFacing = mutableIntStateOf(CameraSelector.LENS_FACING_FRONT)
    val imageCaptureUseCase = ImageCapture.Builder().build()

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
            lifecycleOwner, DEFAULT_BACK_CAMERA, cameraPreviewUseCase, imageCaptureUseCase
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

    fun takePhoto(
        context: Context,
        viewModel: BoxViewModel
    ) {
        val photoFile = File(context.filesDir, viewModel.getPhotoFileName())
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .build()
        val callback = object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                viewModel.setPhotoFile(outputFileResults.savedUri?.toFile())
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
            }
        }
        imageCaptureUseCase.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            callback
        )
    }
}