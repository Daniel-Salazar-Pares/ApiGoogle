package com.example.apigoogle.navigation

sealed class Routes (val route: String) {
    object ListScreen:Routes("ListScreen")
    object LoginScreen:Routes("LoginScreen")
    object SignUpScreen:Routes("SignUpScreen")
    object MapScreen:Routes("MapScreen")
    object MarkerScreen:Routes("MarkerScreen")
    object TakePhotoScreen:Routes("TakePhotoScreen")
    object CameraScreen:Routes("CameraScreen")
    object LaunchScreen:Routes("LaunchScreen")

}

