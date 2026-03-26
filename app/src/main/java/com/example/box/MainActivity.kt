package com.example.box

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.box.camerax.CameraPreviewContent
import com.example.box.camerax.CameraPreviewScreen
import com.example.box.camerax.CameraPreviewViewModel
import com.example.box.camerax.CameraScreen
import com.example.box.model.BoxViewModel
import com.example.box.navigation.CameraX
import com.example.box.navigation.CameraXAnother
import com.example.box.navigation.Home
import com.example.box.navigation.Image
import com.example.box.navigation.ItemInfo
import com.example.box.screens.HomeScreenSetup
import com.example.box.screens.ScaledImage
import com.example.box.screens.SetupItemScreen
import com.example.box.ui.theme.AppTheme
import com.example.box.utils.getScaledBitmap
import java.io.File

class MainActivity : FragmentActivity() {
    private lateinit var viewModel: BoxViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }
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
            val cameraViewModel = remember { CameraPreviewViewModel() }
            AppTheme {
                BoxApp(viewModel = viewModel)
            }
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private val CAMERAX_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}
@Suppress("ParamsComparedByRef")
@Composable
fun BoxApp(viewModel: BoxViewModel) {

    // That code creates the back stack instance initialized with the home
    //screen navigation key and assigns it to the backStack parameter of the
    //NavDisplay. The onBack handler defines what happens when the user
    //navigates backward within the app. In this case, we call the back stack’s
    //removeLastOrNull() method to pop the last entry off the stack if one exists
    //or, otherwise, return a null value.
    // Проблема с тем, что HomeScreen не объект, и не может быть в стеке первым!!!
    val backStack = rememberNavBackStack(Home)

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
    val onBack: () -> Unit = {
        backStack.removeLastOrNull()
    }

    val cameraViewModel = remember { CameraPreviewViewModel() }

    NavDisplay(
        backStack = backStack,
        onBack = onBack,
        entryProvider = entryProvider {
//            entry<Logo>(
//                metadata = mapOf("extraDataKey" to "Home")
//            ) {
//                LogoScreen(
//                    onNavigation = onNavigation
//                )
//            }
            entry<Home>(
                metadata = mapOf("extraDataKey" to "modelValue")
            ) { _ ->
                HomeScreenSetup(
                    onNavigation = onNavigation,
                    viewModel = viewModel)
            }
            entry<ItemInfo>(
                metadata = mapOf("extraDataKey" to "ItemValue")
            ) { key ->
                SetupItemScreen(
                    onNavigation = onNavigation,
                    onBack = onBack,
                    viewModel = viewModel,
                    actionType = key.actionType
                )
            }
            entry<Image>(
                metadata = mapOf("Look" to "ScaledImage")
            ) { key ->
                ScaledImage(
                    viewModel = viewModel,
                    onBack = onBack
                )
            }
            entry<CameraX>(
                metadata = mapOf("takePhoto" to "CameraX")
            ) {
                CameraPreviewScreen(
                    viewModel = viewModel,
                    onBack = onBack,
                    cameraPreviewViewModel = cameraViewModel
                )
            }
            entry<CameraXAnother>(
                metadata = mapOf("takePhoto" to "CameraX")
            ) {
                CameraScreen(
                    viewModel = viewModel,
                    onBack = onBack
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

//enum class AppDestinations(
//    val label: String,
//    val icon: ImageVector,
//) {
//    HOME("Home", Icons.Default.Home),
//    FAVORITES("Favorites", Icons.Default.Favorite),
//    PROFILE("Profile", Icons.Default.AccountBox),
//
//}

class MainViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BoxViewModel(application) as T
    }
}