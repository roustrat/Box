package com.example.box.camerax

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.takePicture
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Refresh
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.geometry.takeOrElse
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.box.model.BoxViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.delay
import java.io.File
import java.util.UUID


// https://medium.com/androiddevelopers/getting-started-with-camerax-in-jetpack-compose-781c722ca0c4
// Also can use https://proandroiddev.com/lets-build-an-android-camera-app-camerax-compose-9ea47356aa80
@Suppress("ParamsComparedByRef")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    cameraPreviewViewModel: CameraPreviewViewModel,
    viewModel: BoxViewModel,
    onBack: () -> Unit
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val imageCaptureUseCase = remember { ImageCapture.Builder().build() }
    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    val context = LocalContext.current
    if (cameraPermissionState.status.isGranted) {
        Box{
            CameraPreviewContent(
                modifier = modifier,
                cameraPreviewViewModel = cameraPreviewViewModel
            )
            IconButton(
                onClick = {
                    lensFacing =
                        if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else CameraSelector.LENS_FACING_BACK
                },
                modifier = Modifier
                    .offset(16.dp, 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Sharp.Refresh,
                    contentDescription = null
                )
            }
            Column(
                modifier
                    .align(Alignment.BottomCenter)) {
                Button(
                    onClick = {
                        cameraPreviewViewModel.takePhoto(
                            viewModel = viewModel,
                            context = context
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

    } else {

        Column(
            modifier = modifier
                .fillMaxSize()
                .wrapContentSize()
                .widthIn(max = 480.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "Whoops! Looks like we need your camera to work our magic!" +
                        "Don't worry, we just wanna see your pretty face (and maybe some cats). " +
                        "Grant us permission and let's get this party started!"
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "Hi there! We need your camera to work our magic! ✨\n" +
                        "Grant us permission and let's get this party started! \uD83C\uDF89"
            }
            Text(textToShow, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Unleash the Camera!")
            }
        }
    }
}

@Suppress("ParamsComparedByRef")
@Composable
fun CameraPreviewContent(
    modifier: Modifier = Modifier,
    cameraPreviewViewModel: CameraPreviewViewModel,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val surfaceRequest by cameraPreviewViewModel.surfaceRequest.collectAsStateWithLifecycle()
    val context = LocalContext.current
    LaunchedEffect(lifecycleOwner) {
        cameraPreviewViewModel.bindToCamera(context.applicationContext, lifecycleOwner)
    }

    var autofocusRequest by remember { mutableStateOf(UUID.randomUUID() to Offset.Unspecified) }

    val autofocusRequestId = autofocusRequest.first
    // Show the autofocus indicator if the offset is specified
    val showAutofocusIndicator = autofocusRequest.second.isSpecified
    // Cache the initial coords for each autofocus request
    val autofocusCoords = remember(autofocusRequestId) { autofocusRequest.second }

    // Queue hiding the request for each unique autofocus tap
    if (showAutofocusIndicator) {
        LaunchedEffect(autofocusRequestId) {
            delay(1000)
            // Clear the offset to finish the request and hide the indicator
            autofocusRequest = autofocusRequestId to Offset.Unspecified
        }
    }

    surfaceRequest?.let { request ->
        val coordinateTransformer = remember { MutableCoordinateTransformer() }
        CameraXViewfinder(
            surfaceRequest = request,
            coordinateTransformer = coordinateTransformer,
            modifier = modifier.pointerInput(cameraPreviewViewModel, coordinateTransformer) {
                detectTapGestures { tapCoords ->
                    with(coordinateTransformer) {
                        cameraPreviewViewModel.tapToFocus(tapCoords.transform())
                    }
                    autofocusRequest = UUID.randomUUID() to tapCoords
                }
            }
        )

        AnimatedVisibility(
            visible = showAutofocusIndicator,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .offset { autofocusCoords.takeOrElse { Offset.Zero }.round() }
                .offset((-24).dp, (-24).dp)
        ) {
            Spacer(Modifier
                .border(2.dp, Color.White, CircleShape)
                .size(48.dp))
        }
    }
}