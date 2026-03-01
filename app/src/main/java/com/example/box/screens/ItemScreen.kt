package com.example.box.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.example.box.R
import com.example.box.model.BoxViewModel
import com.example.box.model.entities.Item
import com.example.box.model.entities.Origin
import com.example.box.model.entities.PlaceItem
import com.example.box.navigation.CameraX
import com.example.box.navigation.ScaledImage
import com.example.box.utils.getScaledBitmap
import java.io.File

@Composable
fun SetupItemScreen(
    onNavigation: (NavKey) -> Unit,
    onBack: () -> Unit,
    itemId: Int,
    viewModel: BoxViewModel,
    actionType: Int
) {
    ItemScreen(
        onNavigation = onNavigation,
        itemId = itemId,
        viewModel = viewModel,
        actionType = actionType,
        onBack = onBack
    )
}
@Composable
fun ItemScreen(
    onNavigation: (NavKey) -> Unit,
    onBack: () -> Unit,
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
            onNavigation = onNavigation,
            onBack = onBack
        )
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
    val origin = viewModel.giveOriginFromId(place.originId)

    val photoFile = File(LocalContext.current.applicationContext.filesDir, item.photoFileName)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.name,
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )
        itemImage(
            viewModel = viewModel,
            onNavigation = onNavigation,
            item = item,
            photoFile = photoFile
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = place.name,
                fontSize = 12.sp
            )

            Text(
                text = origin.name,
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
    onBack: () -> Unit,
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
    onBack: () -> Unit,
    viewModel: BoxViewModel,
    onNavigation: (NavKey) -> Unit
    ) {
    val origins: List<Origin> = viewModel.OriginList.value ?: emptyList()
    var places: List<PlaceItem> = viewModel.PlaceList.value ?: emptyList()

    val (nameValue, setName) = remember { mutableStateOf("") }
    val onNameChange = {text : String ->
        setName(text)
        viewModel.insertItemName(text)
    }

    val (propertiesValue, setProperties) = remember { mutableStateOf("") }
    val onPropertiesChange = {text : String ->
        setProperties(text)
        viewModel.insertItemProperties(text)
    }

    var originExpanded by remember { mutableStateOf(false) }
    var placeExpanded by remember { mutableStateOf(false) }

    val itemId: Int = viewModel.getItemId()
    val photoFile: File by remember { mutableStateOf(viewModel.getPhotoFile(id = itemId)) }

    val myContext = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nameValue,
            onValueChange = onNameChange,
            label = {
                Text(text = stringResource(id = R.string.item_name))
            },
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = primaryColor,
//                focusedLabelColor = primaryColor,
//                cursorColor = primaryColor
//            ),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
        )

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Button(onClick = {originExpanded = !originExpanded}) {
                Text(stringResource(R.string.Origin))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            }
        }
        DropdownMenu(
            expanded = originExpanded,
            onDismissRequest = {originExpanded = false}
        ) {
            if (origins.isNotEmpty()){
                origins.forEach { item ->
                    DropdownMenuItem(
                        text = {Text(item.name)},
                        onClick = {
                            places = viewModel.givePlacesFromOrigin(item.id)
                            originExpanded = true
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Button(onClick = {placeExpanded = !placeExpanded}) {
                Text(stringResource(R.string.ItemPlace))
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = null
                )
            }
        }
        DropdownMenu(
            expanded = placeExpanded,
            onDismissRequest = {placeExpanded = false}
        ) {
            if (places.isNotEmpty()){
                places.forEach { item ->
                    DropdownMenuItem(
                        text = {Text(item.name)},
                        onClick = {
                            viewModel.insertItemPlaceID(item.id)
                            placeExpanded = true
                        }
                    )
                }
            }
        }
        itemNewImage(
            viewModel = viewModel,
            onNavigation = onNavigation,
            photoFile =photoFile
        )
        TextField(
            value = propertiesValue,
            onValueChange = onPropertiesChange,
            label = {
                Text(text = stringResource(id = R.string.properties))
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        Button(
            onClick = {
                val numberOfError = viewModel.checkCurrentItem()
                when(numberOfError) {
                    1 -> {
                        val text = "Укажите наименование"
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(myContext, text, duration)
                        toast.show()
                    }
                    2 -> {
                        val text = "Укажите хранилище"
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(myContext, text, duration)
                        toast.show()
                    }
                    else -> {
                        viewModel.insertCurrentItemIntoBD()
                        onBack()
                    }
                }
            },

        ) {
            Text(
                text = stringResource(id = R.string.add_button)
            )
        }

    }
}

@Composable
fun EditItem(
    itemId: Int,
    viewModel: BoxViewModel
) {

}
@Composable
fun itemImage(
    onNavigation: (NavKey) -> Unit,
    viewModel: BoxViewModel,
    item: Item,
    photoFile: File
) {

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
                                CameraX(
                                    viewModel = viewModel
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
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}

@Composable
fun itemNewImage(
    onNavigation: (NavKey) -> Unit,
    viewModel: BoxViewModel,
    photoFile: File
) {
    val itemName = viewModel.getItemName()
    if (photoFile.exists()) {
        val bitmap = getScaledBitmap(photoFile.path, LocalContext.current as Activity)
        // Размер
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = itemName,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onNavigation(
                                ScaledImage(
                                    bitmap = bitmap,
                                    description = itemName
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
                .fillMaxWidth()
                .padding(20.dp)
        )
    }
}




