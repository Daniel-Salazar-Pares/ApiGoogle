package com.example.apigoogle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.apigoogle.model.Authenticaion
import com.example.apigoogle.navigation.Routes
import com.example.apigoogle.ui.theme.ApiGoogleTheme
import com.example.apigoogle.view.*
import com.example.apigoogle.viewmodel.MapViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApiGoogleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navigationController = rememberNavController()
                    val mapViewModel = MapViewModel()
                    val authentication = Authenticaion()
                    NavHost (
                        navController = navigationController,
                        startDestination = Routes.LaunchScreen.route
                    ){
                        composable(Routes.MapScreen.route){
                            MapScreen(navigationController, mapViewModel)
                        }
                        composable(Routes.ListScreen.route){
                            ListScreen(mapViewModel, navigationController)
                        }
                        composable(Routes.MarkerScreen.route){
                            MarkerScreen(mapViewModel, navigationController)
                        }
                        composable(Routes.LoginScreen.route) {
                            LoginScreen(mapViewModel, navigationController, authentication)
                        }
                        composable(Routes.SignUpScreen.route) {
                            SignUpScreen(mapViewModel, navigationController, authentication)
                        }
                        composable(Routes.TakePhotoScreen.route) {
                            TakePhotoScreen(mapViewModel, navigationController)
                        }
                        composable(Routes.CameraScreen.route) {
                            CameraScreen(mapViewModel, navigationController)
                        }
                        composable(Routes.LaunchScreen.route) {
                            LaunchScreen(navigationController)
                        }
                    }
                }
            }
        }
    }
}




//map no genera bien
//list no generada bien
//no elimina bien
