package com.example.box.navigation

import android.graphics.Bitmap
import androidx.navigation3.runtime.NavKey
import com.example.box.model.BoxViewModel
import kotlinx.serialization.Serializable

@Serializable
data class Home(val viewModel: BoxViewModel) : NavKey

@Serializable
data class ItemInfo(val itemId: Int, val viewModel: BoxViewModel, val actionType: Int) : NavKey

@Serializable
data class ScaledImage(val bitmap: Bitmap, val description: String) : NavKey

@Serializable
data object Screen : NavKey


