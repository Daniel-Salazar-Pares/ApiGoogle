package com.example.apigoogle.navigation

sealed class Routes (val route: String) {
    object ListScreen:Routes("ListScreen")
    object LoginScreen:Routes("LoginScreen")
    object SignUpScreen:Routes("SignUpScreen")
    object MapScreen:Routes("MapScreen")
    object MarkerScreen:Routes("MarkerScreen/{markerId}")
    object LaunchScreen:Routes("LaunchScreen")

}

