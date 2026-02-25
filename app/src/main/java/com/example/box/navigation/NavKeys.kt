package com.example.box.navigation

import androidx.navigation3.runtime.NavKey
import com.example.box.model.BoxViewModel
import kotlinx.serialization.Serializable

@Serializable
data class Home(val viewModel: BoxViewModel) : NavKey

@Serializable
data class ItemInfo(val itemId: Int, val viewModel: BoxViewModel) : NavKey

@Serializable
data object Screen : NavKey


