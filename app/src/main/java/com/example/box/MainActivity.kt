package com.example.box

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.box.model.BoxViewModel
import com.example.box.navigation.Home
import com.example.box.navigation.ItemInfo
import com.example.box.navigation.ScaledImage
import com.example.box.navigation.Screen
import com.example.box.screens.HomeScreen
import com.example.box.screens.HomeScreenSetup
import com.example.box.screens.SetupItemScreen
import com.example.box.screens.ViewImage
import com.example.box.ui.theme.AppTheme
import kotlin.compareTo

class MainActivity : FragmentActivity() {
    private lateinit var viewModel: BoxViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Получение ссылки на текущего владельца локального хранилища моделей представления
            val owner = LocalViewModelStoreOwner.current
            // Проверка что владелец не null и при положительном исходе вызывается
            // функция viewModel(), которой передаются владелец, идентификационная строка
            // и фабрика моделей представления (которой передается ссылка на Application)
            owner?.let {
                viewModel = viewModel(
                    it,
                    "RoomViewModel",
                    MainViewModelFactory(
                        LocalContext.current.applicationContext as Application
                    )
                )
            }
            AppTheme {
                BoxApp(viewModel = viewModel)
            }
        }
    }
}
@Composable
fun BoxApp(viewModel: BoxViewModel) {

    // That code creates the back stack instance initialized with the home
    //screen navigation key and assigns it to the backStack parameter of the
    //NavDisplay. The onBack handler defines what happens when the user
    //navigates backward within the app. In this case, we call the back stack’s
    //removeLastOrNull() method to pop the last entry off the stack if one exists
    //or, otherwise, return a null value.
    // Проблема с тем, что HomeScreen не объект, и не может быть в стеке первым!!!
    val backStack = rememberNavBackStack(Screen)

    //When the user clicks a button to navigate to the next screen, the current
    //screen must call the add() method of the back stack, passing the navigation
    //key of the destination screen as an argument. Instead of calling this method
    //directly within each composable, we will hoist this code into an event
    //handler in NavigationDemo and pass it to the screen composables, where it can
    //be called
    val onNavigation: (NavKey) -> Unit = {
        backStack.add(it)
    }

    //We also need to pass an event handler to the profile screen to clear the back
    //stack and return directly to the home screen. The NavBackStack class does
    //not include a method to clear all but the last entry, but we can achieve the
    //same result using a while statement and the removeLastOrNull() method
    //based on the number of stack entries
    val onClearBackStack: () -> Unit = {
        while (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryProvider = entryProvider {
            entry<Home>(
                metadata = mapOf("extraDataKey" to "modelValue")
            ) { key ->
                HomeScreenSetup(
                    onNavigation = onNavigation,
                    viewModel = key.viewModel)
            }
            entry<ItemInfo>(
                metadata = mapOf("extraDataKey" to "ItemValue")
            ) { key ->
                SetupItemScreen(
                    onNavigation = onNavigation,
                    itemId = key.itemId,
                    viewModel = key.viewModel,
                    actionType = key.actionType
                )
            }
            entry<ScaledImage>(
                metadata = mapOf("Look" to "ScaledImage")
            ) { key ->
                ViewImage(
                    bitmap = key.bitmap,
                    description = key.description
                )
            }
        }
    )
//    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//        HomeScreen(
//            modifier = Modifier.padding(innerPadding),
//            viewModel = viewModel
//        )
//    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),

}

class MainViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BoxViewModel(application) as T
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {

    }
}