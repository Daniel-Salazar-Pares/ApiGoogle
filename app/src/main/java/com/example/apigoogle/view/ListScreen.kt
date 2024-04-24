package com.example.apigoogle.view

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.Snapshot.Companion.observe
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apigoogle.model.Marcador
import com.example.apigoogle.viewmodel.MapViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import androidx.navigation.NavController
import com.example.apigoogle.model.Authentication
import com.example.apigoogle.navigation.Routes
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun ListScreen(mapViewModel: MapViewModel, navController: NavController, authentication: Authentication) {
    val markerList by mapViewModel.markers.observeAsState()
    MyDrawer(
        navController = navController,
        mapViewModel = mapViewModel,
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                if (markerList!!.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            text = "No markers stored yet!"
                        )
                    }
                } else {
                    LazyColumn {
                        items(markerList!!) { marker ->
                            Card {
                                val vectorName = marker.tipus // replace with your vector name
                                val context = LocalContext.current
                                val vectorId = context.resources.getIdentifier(
                                    vectorName,
                                    "drawable",
                                    context.packageName
                                )
                                val vectorResource = painterResource(id = vectorId)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .clickable {
                                            navController.navigate(Routes.MarkerScreen.route.replace("{markerId}", marker.id))
                                        }
                                ) {
                                    Icon(
                                        painter = vectorResource,
                                        contentDescription = "Icon",
                                        modifier = Modifier.padding(8.dp)
                                    )
                                    Column(
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    ) {
                                        Text(text = "Name: ${marker.nom}")
                                        Text(text = "Description: ${marker.descripcio}")
                                        Text(text = "Latitude: ${marker.latitut}")
                                        Text(text = "Longitude: ${marker.longitud}")
                                    }
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = "Delete",
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .clickable(onClick = {
                                                mapViewModel.deleteMarker(marker)
                                            })
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        },
        authentication = authentication
    )
}
