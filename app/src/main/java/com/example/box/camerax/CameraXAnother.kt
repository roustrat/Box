package com.example.box.camerax

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.lifecycle.LifecycleOwner
import com.example.box.model.BoxViewModel
import java.io.File

@Suppress("ParamsComparedByRef")
@Composable
fun CameraScreen(
    viewModel: BoxViewModel,
    onBack: () -> Unit
) {
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val imageCaptureUseCase = remember { ImageCapture.Builder().build() }

    val localContext = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CameraPreview(
            lensFacing = lensFacing,
            imageCaptureUseCase = imageCaptureUseCase,
            modifier = Modifier
                .fillMaxSize()
        )

        IconButton(
            onClick = {
                lensFacing =
                    if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                        CameraSelector.LENS_FACING_BACK
                    } else CameraSelector.LENS_FACING_FRONT
            },
            modifier = Modifier
                .offset(16.dp, 32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Cameraswitch,
                contentDescription = "Switch camera"
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)) {
            Button(
                onClick = {
                    takePhoto(
                        context = localContext,
                        viewModel = viewModel,
                        imageCaptureUseCase = imageCaptureUseCase,
                    )
                    onBack()
                },
                modifier = Modifier
                    .padding(bottom = 40.dp)
            ) {
                Text("Take photo")
            }
        }
    }
}

@Suppress("ParamsComparedByRef")
@Composable
fun CameraPreview(
    lensFacing: Int,
    modifier: Modifier,
    imageCaptureUseCase: ImageCapture
) {
    val previewUseCase = remember { Preview.Builder().build() }

    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }

    val localContext = LocalContext.current

    fun rebindCameraProvider() {
        cameraProvider?.let { cameraProvider ->
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                localContext as LifecycleOwner,
                cameraSelector,
                previewUseCase, imageCaptureUseCase
            )
            cameraControl = camera.cameraControl
        }
    }

    LaunchedEffect(Unit) {
        cameraProvider = ProcessCameraProvider.awaitInstance(localContext)
        rebindCameraProvider()
    }

    LaunchedEffect(lensFacing) {
        rebindCameraProvider()
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            PreviewView(context).also {
                previewUseCase.surfaceProvider = it.surfaceProvider
                rebindCameraProvider()
            }
        }
    )
}

private fun takePhoto(
    context: Context,
    viewModel: BoxViewModel,
    imageCaptureUseCase: ImageCapture
) {
    val photoFile = File(context.filesDir, viewModel.getPhotoFileName())
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
        .build()
    val callback = object : ImageCapture.OnImageSavedCallback{
        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            viewModel.setPhotoFile(outputFileResults.savedUri?.toFile())
        }

        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraX", "Photo capture failed: ${exception.message}", exception)
        }
    }
    imageCaptureUseCase.takePicture(outputOptions, ContextCompat.getMainExecutor(context), callback)
}