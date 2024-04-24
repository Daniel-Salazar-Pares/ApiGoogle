package com.example.apigoogle.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.apigoogle.viewmodel.MapViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.apigoogle.model.Authenticaion
import com.example.apigoogle.navigation.Routes
import kotlinx.coroutines.launch

@Composable
fun MyScaffold(mapViewModel: MapViewModel, state: DrawerState, navController: NavController ,content: @Composable () -> Unit) {
    Scaffold (
        topBar = {
            MyTopAppBar(mapViewModel, state)
        },
        content = {
            Box(modifier = Modifier.padding(it)) {
                content()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(mapViewModel: MapViewModel, state: DrawerState) {
    val scope = rememberCoroutineScope()
    TopAppBar(
        title = { Text(text = mapViewModel.screen.value) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    state.open()
                }
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    )
}


@Composable
fun MyDrawer(mapViewModel: MapViewModel, navController: NavController, content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentDestination?.route
    val drawerName = getScreenNameFromRoute(currentRoute ?: "")
    mapViewModel.setScreen(drawerName)
    val state = rememberDrawerState(initialValue = DrawerValue.Closed)

    //ni idea como funciona pero cambia el titulo del appbar
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val route = destination.route ?: return@OnDestinationChangedListener
            mapViewModel.setScreen(getScreenNameFromRoute(route))
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }
    ModalNavigationDrawer(
        drawerState = state,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    IconButton(onClick = {
                        scope.launch {
                            state.close()
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                    }
                    Text(text = "Menú", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold )
                }
                Divider()
                NavigationDrawerItem(
                    label = { Text("Map") },
                    icon = { Icon(imageVector = Icons.Outlined.Place, contentDescription = "Search") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            state.close()
                            navController.navigate(Routes.MapScreen.route)
                        }
                    }
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("Markers") },
                    icon = { Icon(imageVector = Icons.Outlined.List, contentDescription = "Search") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            state.close()
                            navController.navigate(Routes.ListScreen.route)
                        }
                    }
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("New Markers") },
                    icon = { Icon(imageVector = Icons.Outlined.Add, contentDescription = "Search") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            state.close()
                            navController.navigate(Routes.MarkerScreen.route) // Replace with the route for "New Markers"
                        }
                    }
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("Camera") },
                    icon = { Icon(imageVector = Icons.Outlined.AddAPhoto, contentDescription = "Camera") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            state.close()
                            navController.navigate(Routes.CameraScreen.route) // Replace with the route for "New Markers"
                        }
                    }
                )
                Divider()
                Column (modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Logout",
                        color = Color.Red,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Routes.LoginScreen.route)
                                Authenticaion().logOut()
                            }
                            .padding(8.dp)
                    )
                }

            }
        }) {
        MyScaffold(mapViewModel, state, navController, content)
    }
}


fun getScreenNameFromRoute(route: String): String {
    return when (route) {
        Routes.MapScreen.route -> "Map"
        Routes.ListScreen.route -> "Markers"
        Routes.MarkerScreen.route -> "New Markers"
        Routes.CameraScreen.route -> "Camera"
        else -> "Unknown"
    }
}