package com.example.box.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import com.example.box.R
import com.example.box.model.BoxViewModel
import com.example.box.data.entities.Item
import com.example.box.data.entities.PlaceItem
import com.example.box.navigation.ItemInfo
import com.example.box.utils.getScaledBitmap
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

//@Parcelize
//class PlaceItemP(val id: Int) : Parcelable
//@Parcelize
//class ItemP(val id: Int) : Parcelable

private enum class ActionType(
    val label: String,
    val id: Int
) {
    NEW("New Item", 1),
    EDIT("Edit Item", 2),
    INFO("For info", 0)
}

@Suppress("ParamsComparedByRef")
@Composable
fun HomeScreenSetup(
    onNavigation: (NavKey) -> Unit,
    viewModel: BoxViewModel) {

    val myModifier = Modifier

    Scaffold(
        modifier = myModifier.fillMaxSize()) { innerPadding ->
        HomeScreen(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel,
            onNavigation = onNavigation
        )
    }
}

@Suppress("ParamsComparedByRef")
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigation: (NavKey) -> Unit,
    viewModel: BoxViewModel) {

    Box(modifier = modifier
        .fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            FloatingActionButton(
                onClick = {
                    Log.d("HomeScreen", "${ActionType.NEW.id}")
                    onNavigation(
                        ItemInfo(
                            ActionType.NEW.id),
                    )
                },
                contentColor = Color.White,
                content = {
                    Icon(imageVector = Icons.Filled.AddCircle, contentDescription = "Add new item")
                },
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(20.dp)
            )
        }

        Column(
            Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Места",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                        .padding(0.dp, 40.dp, 0.dp, 20.dp)
            )
            PlaceList(
                modifier,
                viewModel = viewModel
            )
            Spacer(
                modifier
                    .height(20.dp))
            Text(
                text = "Элементы",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(10.dp)
                )
            ItemList(
                onNavigation = onNavigation,
                viewModel = viewModel
            )
        }
    }
}

@Suppress("ParamsComparedByRef")
@Composable
fun PlaceList(
    modifier: Modifier = Modifier,
    viewModel: BoxViewModel) {
    val placeList by viewModel.placeList.observeAsState(emptyList())

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxWidth()
    ) {
        if (placeList.isEmpty()) {
            val item = PlaceItem(
                name = "",
                originId = UUID.randomUUID()
            )
           PlaceListItem(
               item = item,
               onItemClick = {}
               )
        } else {
            LazyRow(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                placeList.forEachIndexed { _, item ->
                    item {
                        PlaceListItem(
                            item = item,
                            onItemClick = { item ->
                                scope.launch {
                                    viewModel.setCurrentItem(id = item.id)
                                }
                            }
                        )
                    }
                }
            }
        }

    }
}

@Suppress("ParamsComparedByRef")
@Composable
fun PlaceListItem(
    item: PlaceItem,
    onItemClick: (PlaceItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.onPrimary
    ),
        modifier = modifier
            .padding(15.dp)
            .fillMaxWidth()
            .clickable{onItemClick(item)},
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(10.dp),
                fontSize = 30.sp
            )
        }
    }
}

@Suppress("ParamsComparedByRef")
@Composable
fun ItemList(
    modifier: Modifier = Modifier,
    onNavigation: (NavKey) -> Unit,
    viewModel: BoxViewModel
) {
    val itemList by viewModel.itemList.observeAsState(emptyList())

    var filtredItemList: List<Item> by remember { mutableStateOf(emptyList()) }
    filtredItemList = itemList.filter { item ->
        item.placeID == viewModel.currentPlaceIdState
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val placeName = viewModel.givePlaceNameFromId(viewModel.currentPlaceIdState)

    Column(
        modifier = modifier.fillMaxHeight()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            filtredItemList.forEachIndexed { index, item ->
                item {
                    ItemListItem(
                        item = item,
                        onItemClick = { item ->
                            scope.launch {
                                viewModel.setCurrentItem(item.id)
                                onNavigation(
                                    ItemInfo(
                                        ActionType.INFO.id
                                    ),
                                )
                            }
                        },
                        placeName = placeName
                    )
                }
            }
        }
    }
}

@Suppress("ParamsComparedByRef")
@Composable
fun ItemListItem(
    item: Item,
    placeName: String,
    onItemClick: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
            .padding(3.dp)
            .fillMaxWidth()
            .clickable{onItemClick(item)},
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column() {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = placeName,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            ImageLoader(item.photoFileName, item.name, modifier = Modifier.size(75.dp))
        }
    }
}

// Понять как убрать
@SuppressLint("ContextCastToActivity")
@Composable
fun ImageLoader(photoFileName: String, itemName: String, modifier: Modifier = Modifier) {
    val photoFile = File(LocalContext.current.applicationContext.filesDir, photoFileName)
    if (photoFile.exists()) {
        val bitmap = getScaledBitmap(photoFile.path, LocalContext.current as Activity)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = itemName,
            contentScale = ContentScale.Fit,
            modifier = modifier
        )
    } else {
        Image(
            painter = painterResource(R.drawable.empty_photo),
            contentDescription = "empty",
            contentScale = ContentScale.Fit,
            modifier = modifier
        )
    }
}

