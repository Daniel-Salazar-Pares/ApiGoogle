package com.example.apigoogle.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.provider.Settings
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.apigoogle.navigation.Routes
import com.example.apigoogle.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun CameraScreen(mapViewModel: MapViewModel, navController: NavController) {
    MyDrawer(
        navController = navController,
        mapViewModel = mapViewModel,
        content = {
            val context = LocalContext.current
            val isCameraPermissionGranted by mapViewModel.cameraPermissionGranted.observeAsState(false)
            val shouldShowPermissionRationale by mapViewModel.shouldShowPermissionRationale.observeAsState(false)
            val showPermissionDenied by mapViewModel.showPermissionDenied.observeAsState(false)
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
                        mapViewModel.setCameraPermissionGranted(true)
                    } else {
                        mapViewModel.setShouldShowPermissionRationale(
                            shouldShowRequestPermissionRationale(
                                context as Activity,
                                Manifest.permission.CAMERA
                            )
                        )
                    }
                    if (!shouldShowPermissionRationale) {
                        Log.i("CameraScreen", "No podemos volver a pedir permisos")
                        mapViewModel.setShowPermissionDenied(true)
                    }
                }
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()) {
                Button(onClick = {
                    if (!isCameraPermissionGranted) {
                        launcher.launch(Manifest.permission.CAMERA)
                    } else {
                        navController.navigate(Routes.TakePhotoScreen.route)
                    }
                }) {
                    Text(text = "Take photo")
                }
            }

            if (showPermissionDenied) {
                PermissionDeclinedScreen(mapViewModel)
            }
        }
    )
}

@Composable
fun PermissionDeclinedScreen(mapViewModel: MapViewModel) {
    val context = LocalContext.current
    val isCameraPermissionGranted by mapViewModel.cameraPermissionGranted.observeAsState(false)

    if (!isCameraPermissionGranted) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = "Permission required", fontWeight = FontWeight.Bold)
            Text(text = "This app needs access to the camera to take photos")
            Button(onClick = {
                openAppSettings(context as Activity)
            }) {
                Text(text = "Accept")
            }
        }
    }
}

fun openAppSettings(activity: Activity) {
    val intent = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", activity.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    activity.startActivity(intent)
}


