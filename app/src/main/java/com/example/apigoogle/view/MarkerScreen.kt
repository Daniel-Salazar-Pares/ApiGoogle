package com.example.apigoogle.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.BorderColor
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.apigoogle.model.Authentication
import com.example.apigoogle.model.Marcador
import com.example.apigoogle.navigation.Routes
import com.example.apigoogle.viewmodel.MapViewModel
import com.google.android.gms.maps.model.LatLng
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkerScreen(
    mapViewModel: MapViewModel,
    navController: NavController,
    authentication: Authentication,
    markerId: String
) {
    val showBottomSheet = remember { mutableStateOf(false) }
    val marker = remember { mapViewModel.getMarkerById(markerId) }
    val vectorName = marker?.tipus // replace with your vector name
    val context = LocalContext.current
    val vectorId = context.resources.getIdentifier(
        vectorName,
        "drawable",
        context.packageName
    )
    val vectorResource = painterResource(id = vectorId)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            marker?.let {
                Spacer(modifier = Modifier.fillMaxSize(0.3f))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "Marker: ${it.nom} ", fontSize = 36.sp)
                    Icon(
                        painter = vectorResource,
                        contentDescription = "Icon",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(34.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = "Description: ${it.descripcio}", fontSize = 24.sp)
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = "Latitude: ${it.latitut}", fontSize = 18.sp)
                Spacer(modifier = Modifier.padding(8.dp))
                Text(text = "Longitude: ${it.longitud}")
                Spacer(modifier = Modifier.padding(8.dp))
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Back",
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .clickable { navController.navigate(Routes.ListScreen.route) }
                            .weight(1f)  // False fill parameter to align start
                    )

                    Box(modifier = Modifier.weight(1f)) // This spacer takes up remaining space

                    Icon(
                        imageVector = Icons.Outlined.BorderColor,
                        contentDescription = "Edit Marker",
                        modifier = Modifier
                            .align(Alignment.Bottom)
                            .clickable {
                                showBottomSheet.value = true
                            }
                            .weight(1f)  // False fill parameter to align end
                    )
                }
                if (showBottomSheet.value) {
                    var nom by remember { mutableStateOf(marker.nom) }
                    var descripcio by remember { mutableStateOf(marker.descripcio) }
                    var selectedIcon by remember { mutableStateOf(marker.tipus) }
                    ModalBottomSheet(onDismissRequest = {  }) {
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
                            Button(
                                enabled = nom.isNotEmpty() && descripcio.isNotEmpty(),
                                onClick = {
                                    mapViewModel.editMarker(Marcador(marker.id, nom, descripcio, marker.latitut, marker.longitud, selectedIcon))
                                    showBottomSheet.value = false
                                    navController.navigate(Routes.ListScreen.route)
                                }) {
                                Text("Modify Marker")
                            }
                        }
                    }
                }
            } ?: Text(text = "No marker available")
        }
    }
}
