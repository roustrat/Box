package com.example.box.screens

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.box.R
import com.example.box.model.BoxViewModel
import com.example.box.model.entities.Item
import com.example.box.model.entities.PlaceItem
import com.example.box.navigation.ItemInfo
import com.example.box.utils.getScaledBitmap
import kotlinx.coroutines.launch
import java.io.File

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

@Composable
fun HomeScreenSetup(
    onNavigation: (NavKey) -> Unit,
    viewModel: BoxViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize()) { innerPadding ->
        HomeScreen(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel,
            onNavigation = onNavigation
        )
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onNavigation: (NavKey) -> Unit,
    viewModel: BoxViewModel) {
    val placeList by viewModel.PlaceList.observeAsState(listOf())
    val itemList by viewModel.ItemList.observeAsState(listOf())
    var currentPlaceIdState: Int by remember { mutableIntStateOf(0) }
    val currentPlaceIdChange = { value: Int ->
        currentPlaceIdState = value
    }

    Column() {
        PlaceList(
            modifier
                .fillMaxWidth(),
            placeList = placeList,
            currentPlaceIdChange = currentPlaceIdChange
            )
        Spacer(
            modifier
                .height(20.dp))
        ItemList(
            modifier
                .fillMaxWidth(),
            onNavigation = onNavigation,
            PlaceID = currentPlaceIdState,
            viewModel = viewModel
        )
    }
}

@Composable
fun PlaceList(
    modifier: Modifier = Modifier,
    placeList: List<PlaceItem>,
    currentPlaceIdChange: (Int) -> Unit) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier
        .horizontalScroll(scrollState)) {
           placeList.forEachIndexed { index, item ->
               item {
                   PlaceListItem(
                       item = item,
                       onItemClick = { item ->
                           scope.launch {
                               currentPlaceIdChange(item.id)
                           }
                       }
                   )
               }
           }
    }
}

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
            .padding(3.dp)
            .fillMaxWidth()
            .clickable{onItemClick(item)},
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun ItemList(
    modifier: Modifier = Modifier,
    onNavigation: (NavKey) -> Unit,
    PlaceID: Int,
    viewModel: BoxViewModel
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val itemList = viewModel.currentItemsInPlace(PlaceID)
    val placeName = viewModel.givePlaceNameFromId(PlaceID)

    LazyColumn(
        modifier
            .verticalScroll(scrollState)) {
        itemList.forEachIndexed { index, item ->
            item {
                ItemListItem(
                    item = item,
                    onItemClick = { item ->
                        scope.launch {
                            onNavigation(
                                ItemInfo(
                                    itemId = item.id,
                                    viewModel = viewModel,
                                    ActionType.INFO.id),
                                )
                        }
                    },
                    placeName = placeName
                )
            }
        }
    }
}

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

