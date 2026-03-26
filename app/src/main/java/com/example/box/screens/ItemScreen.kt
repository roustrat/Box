package com.example.box.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
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
import androidx.navigation3.runtime.NavKey
import com.example.box.R
import com.example.box.model.BoxViewModel
import com.example.box.data.entities.Origin
import com.example.box.data.entities.PlaceItem
import com.example.box.navigation.CameraXAnother
import com.example.box.navigation.Image
import com.example.box.utils.getScaledBitmap
import java.io.File

@Suppress("ParamsComparedByRef")
@Composable
fun SetupItemScreen(
    onNavigation: (NavKey) -> Unit,
    onBack: () -> Unit,
    viewModel: BoxViewModel,
    actionType: Int
) {
    val myModifier = Modifier
    ItemScreen(
        onNavigation = onNavigation,
        viewModel = viewModel,
        actionType = actionType,
        onBack = onBack,
        myModifier = myModifier
    )
}
@Suppress("ParamsComparedByRef")
@Composable
fun ItemScreen(
    onNavigation: (NavKey) -> Unit,
    onBack: () -> Unit,
    viewModel: BoxViewModel,
    actionType: Int,
    myModifier: Modifier
) {
    when(actionType) {
        0 -> {
            if (viewModel.getItemName() != "") {
                viewModel.resetCurrentItem()
            }
            InfoItem(
                onNavigation = onNavigation,
                viewModel = viewModel,
                onBack = onBack,
                modifier = myModifier
            )
        }
        1 -> {
            Toast.makeText(LocalContext.current, "$actionType", Toast.LENGTH_SHORT)
            viewModel.resetCurrentItem()
            NewItem(
                viewModel = viewModel,
                onNavigation = onNavigation,
                onBack = onBack,
                modifier = myModifier
            )
        }
        2 -> {
            // Сработает ли?
            viewModel.takeItemForEdit(id = viewModel.getItemId())
            EditItem(
            viewModel = viewModel,
            onBack = onBack,
            onNavigation = onNavigation,
                modifier = myModifier
            )
        }
    }
}

@Suppress("ParamsComparedByRef")
@SuppressLint("ContextCastToActivity")
@Composable
fun InfoItem(
    onNavigation: (NavKey) -> Unit,
    viewModel: BoxViewModel,
    onBack: () -> Unit,
    modifier: Modifier
) {
    val place = viewModel.givePlaceFromId(viewModel.getItemId())
    val origin = viewModel.giveOriginFromId(place.originId)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = viewModel.getItemName(),
            fontSize = 30.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 40.dp, 0.dp, 20.dp)
        )
        ViewImage(
            onNavigation = onNavigation,
            viewModel = viewModel,
            onBack = onBack
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
            text = viewModel.getItemProperties(),
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@SuppressLint("ContextCastToActivity")
@Suppress("ParamsComparedByRef")
@Composable
fun ViewImage(
    onNavigation: (NavKey) -> Unit,
    viewModel: BoxViewModel,
    onBack: () -> Unit
) {
    val photoFile = File(LocalContext.current.applicationContext.filesDir, viewModel.getPhotoFileName())

    if (photoFile.exists()) {
        val bitmap = getScaledBitmap(photoFile.path, LocalContext.current as Activity)
        // Размер
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = viewModel.getItemName(),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(300.dp)
                .padding(20.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onNavigation(
                                Image
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
                .size(300.dp)
                .padding(20.dp)
        )
    }
}

@SuppressLint("ContextCastToActivity")
@Suppress("ParamsComparedByRef")
@Composable
fun ScaledImage(
    viewModel: BoxViewModel,
    onBack: () -> Unit
) {
    val photoFile = File(LocalContext.current.applicationContext.filesDir, viewModel.getPhotoFileName())
    val bitmap = getScaledBitmap(photoFile.path, LocalContext.current as Activity)
    val itemName = viewModel.getItemName()

    // Масштаб
    var scale by remember { mutableFloatStateOf(1f) }

    val state = rememberTransformableState {
            scaleChange, _, _ ->
        scale *= scaleChange
    }

    // Размер
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = itemName,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            // Gettins access to graphic layer to get information about composable changes
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .transformable(state = state)
    )
}

@Suppress("ParamsComparedByRef")
@Composable
fun NewItem(
    onBack: () -> Unit,
    viewModel: BoxViewModel,
    onNavigation: (NavKey) -> Unit,
    modifier: Modifier
    ) {
    val origins: List<Origin> = viewModel.originList.value ?: emptyList()
    var places: List<PlaceItem> = viewModel.placeList.value ?: emptyList()

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

    val itemId = viewModel.getItemId()

    val myContext = LocalContext.current

    Column(
        modifier = modifier
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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .padding(0.dp, 40.dp, 0.dp, 20.dp)
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
        ItemNewImage(
            viewModel = viewModel,
            onNavigation = onNavigation,
            photoFile = viewModel.currentPhotoFile
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
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 30.dp)
        ) {
            Text(
                text = stringResource(id = R.string.add_button)
            )
        }

    }
}

@Suppress("ParamsComparedByRef")
@Composable
fun EditItem(
    viewModel: BoxViewModel,
    onBack: () -> Unit,
    onNavigation: (NavKey) -> Unit,
    modifier: Modifier
) {
    val origins: List<Origin> = viewModel.originList.value ?: emptyList()
    var places: List<PlaceItem> = viewModel.placeList.value ?: emptyList()

    val (nameValue, setName) = remember { mutableStateOf(viewModel.getItemName()) }
    val onNameChange = {text : String ->
        setName(text)
        viewModel.insertItemName(text)
    }

    val (propertiesValue, setProperties) = remember { mutableStateOf(viewModel.getItemProperties()) }
    val onPropertiesChange = {text : String ->
        setProperties(text)
        viewModel.insertItemProperties(text)
    }

    var originExpanded by remember { mutableStateOf(false) }
    var placeExpanded by remember { mutableStateOf(false) }

    val myContext = LocalContext.current

    Column(
        modifier = modifier
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
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .padding(0.dp, 40.dp, 0.dp, 20.dp)
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
        ItemNewImage(
            viewModel = viewModel,
            onNavigation = onNavigation,
            photoFile = viewModel.currentPhotoFile
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
                        viewModel.resetCurrentItem()
                        onBack()
                    }
                }
            },
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 30.dp)
            ) {
            Text(
                text = stringResource(id = R.string.add_button)
            )
        }

    }
}
@Suppress("ParamsComparedByRef")
@SuppressLint("ContextCastToActivity")
@Composable
fun ItemNewImage(
    onNavigation: (NavKey) -> Unit,
    viewModel: BoxViewModel,
    photoFile: File?
) {
    val itemName = viewModel.getItemName()
    if (photoFile != null) {
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
            )
        } else {
            Image(
                painter = painterResource(R.drawable.empty_photo),
                contentDescription = "empty",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            onNavigation(
                                CameraXAnother
                            )
                        }
                    )
                }
            )
        }
    } else {
        Image(
            painter = painterResource(R.drawable.empty_photo),
            contentDescription = "empty",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onNavigation(
                            CameraXAnother
                        )
                    }
                )
            }
        )
    }
}

//@Suppress("ParamsComparedByRef")
//@SuppressLint("ContextCastToActivity")
//@Composable
//fun ItemNewImage(
//    onNavigation: (NavKey) -> Unit,
//    viewModel: BoxViewModel
//) {
//    val itemName = viewModel.getItemName()
//    val currentBitmap = viewModel.currentBitmap
//    if (currentBitmap!= null) {
//        Image(
//            bitmap = currentBitmap.asImageBitmap(),
//            contentDescription = itemName,
//            contentScale = ContentScale.Fit,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp)
//        )
//    } else {
//        Image(
//            painter = painterResource(R.drawable.empty_photo),
//            contentDescription = "empty",
//            contentScale = ContentScale.Fit,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp)
//                .pointerInput(Unit) {
//                    detectTapGestures(
//                        onTap = {
//                            onNavigation(
//                                CameraXAnother
//                            )
//                        }
//                    )
//                }
//        )
//    }
//}




