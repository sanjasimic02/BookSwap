package com.example.bookswap.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookswap.models.User
import com.example.bookswap.navigation.Routes
import com.example.bookswap.repositories.Resource
import com.example.bookswap.screens.appComponents.Alternative
import com.example.bookswap.screens.appComponents.BSBackground
import com.example.bookswap.screens.appComponents.CustomInput
import com.example.bookswap.screens.appComponents.CustomLabel
import com.example.bookswap.screens.appComponents.Heading
import com.example.bookswap.screens.appComponents.Heading2
import com.example.bookswap.screens.appComponents.Password
import com.example.bookswap.viewModel.UserAuthViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun LoginScreen(
    viewModel: UserAuthViewModel,
    navController: NavController
) {
    val email = remember { mutableStateOf("") }
    val isEmailError = remember { mutableStateOf(false) }
    val emailErrorText = remember { mutableStateOf("") }

    val password = remember { mutableStateOf("") }
    val isPasswordError = remember { mutableStateOf(false) }
    val passwordErrorText = remember { mutableStateOf("") }

    val isLoading = remember { mutableStateOf(false) }
    val signInFlow = viewModel.signInFlow.collectAsState()

    val currentUser = remember {
        mutableStateOf<User?>(null)
    }
    val currUserData = viewModel.currentUserFlow.collectAsState()



    BSBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 30.dp, top = 14.dp, bottom = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Heading(text = "Enter the World of Books")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Heading2(secondary_text = "Enter your credentials to access your account...")

                Spacer(modifier = Modifier.height(24.dp))
                CustomLabel(label = "Email Address")
                CustomInput(
                    hint = "example@domain.com",
                    value = email,
                    isEmail = true,
                    isError = isEmailError,
                    errorText = emailErrorText
                )

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Password")
                Password(
                    inputValue = password,
                    hint = "fde1234KL!",
                    isError = isPasswordError,
                    errorText = passwordErrorText
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        isEmailError.value = false
                        isPasswordError.value = false
                        isLoading.value = true

                        if (email.value.isEmpty()) {
                            isEmailError.value = true
                            isLoading.value = false
                        } else if (password.value.isEmpty()) {
                            isPasswordError.value = true
                            isLoading.value = false
                        } else {
                            viewModel.logIn(
                                email = email.value,
                                password = password.value
                            )

                        }
                    },
                    enabled = !isLoading.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41),
                        contentColor = Color(0xFF3C0B1A)
                    )
                ) {
                    Text(
                        text = "Log In",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDC9AF),
                        )
                    )
                }

                Spacer(modifier = Modifier.height(17.dp))
                Alternative(
                    text = "Don't have an account?  ",
                    link = "Register",
                    onClick = {
                        navController.navigate(Routes.registrationScreen)
                    }
                )

            }

            LaunchedEffect(signInFlow.value) {
                when (signInFlow.value) {
                    is Resource.Failure -> {
                        isLoading.value = false
                    }
                    is Resource.Success -> {
                        Log.d("[DEBUG]", "Login successful, fetching user data.")
                        viewModel.getUserData()
                    }
                    is Resource.Loading -> {
                        isLoading.value = true
                        Log.d("[DEBUG]", "Loading...")
                    }
                    null -> {
                        Log.d("[DEBUG]", "signInFlow is null")
                    }
                }
            }

            LaunchedEffect(currUserData.value) {
                when (val userResource = currUserData.value) {
                    is Resource.Success -> {
                        Log.d("[DEBUG]", "User data fetched successfully.")
                        val user = userResource.result
                        isLoading.value = false

                        //currentUser.value?.let { user ->
                            val currUserJSON = Gson().toJson(user)
                            val encodedUsr = URLEncoder.encode(currUserJSON, StandardCharsets.UTF_8.toString())
                            Log.d("[DEBUG]", "Navigating to: ${Routes.userScreen + "/$encodedUsr"}")
                            navController.navigate(Routes.userScreen + "/$encodedUsr") {
                                popUpTo(Routes.loginScreen) { inclusive = true }
                            }
                        //}
                    }
                    is Resource.Failure -> {
                        Log.d("[DEBUG]", "Failed to fetch user data.")
                        currentUser.value = null
                        isLoading.value = false
                    }
                    is Resource.Loading -> {
                        isLoading.value = true
                        Log.d("[DEBUG]", "Loading user data...")
                    }
                    null -> {
                        Log.d("[DEBUG]", "currUserData is null")
                    }
                }
            }
        }
    }
}

