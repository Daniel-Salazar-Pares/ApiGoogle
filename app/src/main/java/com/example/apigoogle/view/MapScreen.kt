package com.example.apigoogle.view

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apigoogle.MainActivity
import com.example.apigoogle.viewmodel.MapViewModel
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.UUID

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(navController: NavController, mapViewModel: MapViewModel) {
    MyDrawer(
        navController = navController,
        mapViewModel = mapViewModel,
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val context = LocalContext.current
                val fusedLocationProviderClient =
                    remember { LocationServices.getFusedLocationProviderClient(context) }
                var lastKnownLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }
                var cameraPositionState =
                    rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(lastKnownLocation, 18f)
                    }
                val locationResult = fusedLocationProviderClient.getCurrentLocation(100, null)

                locationResult.addOnCompleteListener(context as MainActivity) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = LatLng(task.result.latitude, task.result.longitude)
                        cameraPositionState.position =
                            CameraPosition.fromLatLngZoom(lastKnownLocation, 18f)
                    } else {
                        Log.e("Error", "Exception: ${task.exception}")
                    }
                }

                val permissionState =
                    rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
                LaunchedEffect(Unit) {
                    permissionState.launchPermissionRequest()
                }
                if (permissionState.status.isGranted) {
                    Map(mapViewModel, cameraPositionState, navController)
                } else {
                    Text("Permission denied, need permission to access location.")
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Map(
    mapViewModel: MapViewModel,
    cameraPositionState: CameraPositionState,
    navController: NavController
) {
    val showBottomSheet = remember { mutableStateOf(false) }
    val ubi = remember { mutableStateOf(LatLng(0.0, 0.0)) }
    val markerList by mapViewModel.markers.observeAsState()
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { },
        onMapLongClick = {
            showBottomSheet.value = true
            ubi.value = it
        },
        properties = MapProperties(
            isMyLocationEnabled = true,
            isTrafficEnabled = true
        )
    ) {
        //Shows markerson the map
        if (markerList != null) {
            for(marker in markerList!!) {
                Marker(
                    state = MarkerState(position = LatLng(marker.latitut, marker.longitud)),
                    title = marker.nom,
                    snippet = marker.descripcio,
                )
            }
        }
        //Bottom sheet to add a new marker
        var nom by remember { mutableStateOf("") }
        var descripcio by remember { mutableStateOf("") }
        var selectedIcon by remember { mutableStateOf("baseline_park_24") }
        if (showBottomSheet.value) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet.value = false }) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        value = nom,
                        onValueChange = { nom = it },
                        label = { Text("Name") },
                        singleLine = true,
                        placeholder = { Text(text = "Nom ubicació") }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    OutlinedTextField(
                        value = descripcio,
                        onValueChange = { descripcio = it },
                        label = { Text("Descripció") },
                        singleLine = true,
                        placeholder = { Text(text = "Descripció") }
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Icon:")
                        mapViewModel.icons.forEach { icon ->
                            val vectorName = icon // replace with your vector name
                            val context = LocalContext.current
                            val vectorId = context.resources.getIdentifier(vectorName, "drawable", context.packageName)
                            val vectorResource = painterResource(id = vectorId)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = vectorResource,
                                contentDescription = "Icon"
                            )
                            RadioButton(
                                selected = (icon == selectedIcon),
                                onClick = { selectedIcon = icon },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    var id = UUID.randomUUID().toString()
                    Button(
                        enabled = nom.isNotEmpty() && descripcio.isNotEmpty(),
                        onClick = {
                        mapViewModel.addMarker(id, nom, descripcio, ubi.value, selectedIcon)
                        nom = ""
                        descripcio = ""
                        selectedIcon = "baseline_park_24"
                        mapViewModel.getMarkers()
                        showBottomSheet.value = false
                    }) {
                        Text("Add marker")
                    }
                }
            }
        }
    }
}
