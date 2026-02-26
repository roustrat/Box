package com.example.box.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.box.R
import com.example.box.model.BoxViewModel
import com.example.box.model.entities.Item
import com.example.box.navigation.ScaledImage
import com.example.box.utils.getScaledBitmap
import java.io.File

@Composable
fun SetupItemScreen(
    onNavigation: (NavKey) -> Unit,
    itemId: Int,
    viewModel: BoxViewModel,
    actionType: Int
) {
    ItemScreen(
        onNavigation = onNavigation,
        itemId = itemId,
        viewModel = viewModel,
        actionType = actionType
    )
}
@Composable
fun ItemScreen(
    onNavigation: (NavKey) -> Unit,
    itemId: Int,
    viewModel: BoxViewModel,
    actionType: Int
) {
    when(actionType) {
        0 -> InfoItem(
            onNavigation = onNavigation,
            itemId = itemId,
            viewModel = viewModel
        )
        1 -> NewItem(
            viewModel = viewModel,
            onNavigation = onNavigation)
        2 -> EditItem(
            itemId = itemId,
            viewModel = viewModel)
    }
}

@Suppress("ParamsComparedByRef")
@SuppressLint("ContextCastToActivity")
@Composable
fun InfoItem(
    onNavigation: (NavKey) -> Unit,
    itemId: Int,
    viewModel: BoxViewModel
) {
    val item: Item = viewModel.giveItemFromId(itemId)
    val place = viewModel.givePlaceFromId(item.placeID)
    val photoFile = File(LocalContext.current.applicationContext.filesDir, item.photoFileName)
    Column(
        modifier = Modifier
            .fillMaxSize()
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.name,
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, LocalContext.current as Activity)
            // Размер
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = item.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onNavigation(
                                    ScaledImage(
                                        bitmap = bitmap,
                                        description = item.name
                                    )
                                )
                            }
                        )
                    }
            )
        } else {
            Image(
                painter = painterResource(R.drawable.empty_photo),
                contentDescription = "empty",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = place.name,
                fontSize = 12.sp
            )

            Text(
                text = place.origin,
                fontSize = 12.sp
            )
        }

        Text(
            text = item.properties,
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Suppress("ParamsComparedByRef")
@Composable
fun ViewImage(
    bitmap: Bitmap,
    description: String
) {
    // Масштаб
    var scale by remember { mutableFloatStateOf(1f) }

    val state = rememberTransformableState {
            scaleChange, offsetChange, rotationChange ->
        scale *= scaleChange
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = description,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .transformable(state = state)
    )
}

@Composable
fun NewItem(
    viewModel: BoxViewModel,
    onNavigation: (NavKey) -> Unit
    ) {

}

@Composable
fun EditItem(
    itemId: Int,
    viewModel: BoxViewModel
) {

}




