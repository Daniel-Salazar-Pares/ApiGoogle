package com.example.apigoogle.view

import android.telephony.TelephonyCallback
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.apigoogle.model.Marcador
import com.example.apigoogle.navigation.Routes
import com.example.apigoogle.viewmodel.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.UUID

@Composable
fun MarkerScreen(mapViewModel: MapViewModel, navController: NavController, ubi:LatLng = LatLng(0.0, 0.0)) { //add markers
    MyDrawer(
        navController = navController,
        mapViewModel = mapViewModel,
        content = {
            mapViewModel.getMarkers()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                val name = remember { mutableStateOf("") }
                val descripcio = remember { mutableStateOf("") }
                val latitude = remember { mutableStateOf(ubi.latitude.toString()) }
                val longitude = remember { mutableStateOf(ubi.latitude.toString()) }
                Text(
                    text = "Create Marker",
                )
                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                TextField(
                    value = descripcio.value,
                    onValueChange = { descripcio.value = it },
                    label = { Text("Descripció") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                TextField(
                    value = latitude.value,
                    onValueChange = { latitude.value = it },
                    label = { Text("Latitude") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                TextField(
                    value = longitude.value,
                    onValueChange = { longitude.value = it },
                    label = { Text("Longitude") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )


                var selectedIcon by remember { mutableStateOf("baseline_park_24") }

                var status by remember { mutableStateOf(false) }

                Row (
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

                val showDialog = remember { mutableStateOf(false) }
                if (showDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showDialog.value = false },
                        title = { Text("Error") },
                        text = { Text("Latitude and Longitude must be numbers.") },
                        confirmButton = {
                            Button(
                                onClick = { showDialog.value = false },
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }

                Button(onClick = {
                    try {
                        latitude.value.toDouble()
                        longitude.value.toDouble()
                    } catch (e: NumberFormatException) {
                        showDialog.value = true
                        return@Button
                    }
                    mapViewModel.addMarker(
                        id = UUID.randomUUID().toString(),
                        name.value,
                        descripcio.value,
                        LatLng(latitude.value.toDouble(), longitude.value.toDouble()),
                        selectedIcon
                    )
                    navController.navigate(Routes.MapScreen.route)
                }) {
                    Text("Add new Marker")
                }
            }
        }
    )
}