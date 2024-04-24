package com.example.apigoogle.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apigoogle.model.Authentication
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.VisualTransformation
import androidx.navigation.NavController
import com.example.apigoogle.navigation.Routes
import com.example.apigoogle.viewmodel.MapViewModel

@Composable
fun SignUpScreen(
    mapViewModel: MapViewModel,
    navController: NavController,
    authentication: Authentication
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordR = remember { mutableStateOf("") }

    val accountCreated:Boolean by authentication.accountCreated.observeAsState(false)
    val signupError:Boolean by authentication.singupError.observeAsState(false)
    Box(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp, start = 8.dp),
        contentAlignment = Alignment.TopStart
    ) {
        IconButton(onClick = {
            navController.navigate(Routes.LoginScreen.route)
        }) {
            Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "Back")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text("Create an account", fontSize = 24.sp)

        Spacer(modifier = Modifier.padding(8.dp))
        TextField(
            value = username.value,
            singleLine = true,
            onValueChange = { username.value = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth(0.8f)

        )
        Spacer(modifier = Modifier.padding(8.dp))
        Row {
            var showPassword by remember { mutableStateOf(false) }
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                singleLine = true,
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
        Row {
            var showPasswordR by remember { mutableStateOf(false) }
            TextField(
                value = passwordR.value,
                onValueChange = { passwordR.value = it },
                label = { Text("Repeat Password") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth(0.8f),
                visualTransformation = if (showPasswordR) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (showPasswordR) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { showPasswordR = !showPasswordR }) {
                        Icon(imageVector = image, contentDescription = "Toggle password visibility")
                    }
                }
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))

        Button(
            onClick = {
                authentication.register(username.value, password.value)
            },
            modifier = Modifier.fillMaxWidth(0.80f),
            enabled = username.value.isNotEmpty() && password.value.isNotEmpty() && passwordR.value.isNotEmpty() && password.value == passwordR.value
        ) {
            Text("Sign Up")
            Spacer(modifier = Modifier.padding(4.dp))
        }
        if (signupError) {
            AlertDialog(
                onDismissRequest = {
                    authentication.changeSingupError()
                    password.value = ""
                    passwordR.value = ""
                },
                title = {
                    Text("Error")
                },
                text = {
                    Text("Error creating account, try again.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            authentication.changeSingupError()
                            password.value = ""
                            passwordR.value = ""
                        }
                    ) {
                        Text("Ok")
                    }
                }
            )
        }
        if (accountCreated) {
            AlertDialog(
                onDismissRequest = {
                    authentication.changeAccountCreated()
                    navController.navigate(Routes.LoginScreen.route)
                },
                title = {
                    Text("Account created")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            authentication.changeAccountCreated()
                            navController.navigate(Routes.LoginScreen.route)
                        }
                    ) {
                        Text("Ok")
                    }
                }
            )
        }

    }
}