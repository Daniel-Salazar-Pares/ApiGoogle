package com.example.apigoogle.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.apigoogle.R
import com.example.apigoogle.navigation.Routes
import kotlinx.coroutines.delay

@Composable
fun LaunchScreen(navController: NavController) {
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000), label = ""
    )
    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3500)
        navController.popBackStack()
        navController.navigate(Routes.LoginScreen.route)
    }
    Splash(alphaAnim.value)

}

@Composable
fun Splash(alphaAnim: Float) {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Image(painter = painterResource(id = R.drawable.maplogo),
            contentDescription = "Logo", alpha = alphaAnim
        )
        Text(text = "Welcome to Marker App", fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
    }
}