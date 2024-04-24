package com.example.apigoogle.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.apigoogle.model.Authenticaion
import com.example.apigoogle.navigation.Routes
import com.example.apigoogle.viewmodel.MapViewModel

@Composable
fun LoginScreen(mapViewModel: MapViewModel, navController: NavController, authentication: Authenticaion) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to the Marker App", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.padding(8.dp))

        val username = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        val nextScreen:Boolean by authentication.goToNext.observeAsState(false)
        val loginError:Boolean by authentication.loginError.observeAsState(false)

        TextField(
            value = username.value,
            onValueChange = { username.value = it },
            singleLine = true,
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth(0.8f)

        )
        Spacer(modifier = Modifier.padding(8.dp))
        Row {
            var showPassword by remember { mutableStateOf(false) }
            TextField(
                value = password.value,
                singleLine = true,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                // Add trailing icon parameter to TextField instead of using separate Icon
                trailingIcon = {
                    val image =
                        if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility")
                    }
                }
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Button(onClick = {
            authentication.login(username.value, password.value)
        }, modifier = Modifier.fillMaxWidth(0.80f),
            enabled = username.value.isNotEmpty() && password.value.isNotEmpty(),
            shape = RectangleShape) {
            Text("Login")
        }


        Row {
            Text("Don't have an account?", fontSize = 14.sp)
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                "Sign up",
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    authentication.changeGoToNext()
                    navController.navigate(Routes.SignUpScreen.route)
                                              },
                color = Color.Magenta
            )
        }

        if (loginError) {
            AlertDialog(
                onDismissRequest = { password.value = ""; authentication.changeLoginError() },
                title = { Text("Error logging in") },
                text = { Text("Invalid credentials, please try again") },
                confirmButton = {
                    Button(
                        onClick = { password.value = ""; authentication.changeLoginError()},

                    ) {
                        Text("Ok")
                    }
                }
            )
        }
        if (nextScreen) {
            authentication.changeGoToNext()
            mapViewModel.setCurrentUser()
            mapViewModel.getMarkers()
            navController.navigate(Routes.MapScreen.route)
        }
    }
}