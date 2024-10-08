package com.example.bookswap.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookswap.navigation.Routes
import com.example.bookswap.repositories.Resource
import com.example.bookswap.screens.appComponents.Alternative
import com.example.bookswap.screens.appComponents.BSBackground
import com.example.bookswap.screens.appComponents.CustomInput
import com.example.bookswap.screens.appComponents.CustomLabel
import com.example.bookswap.screens.appComponents.Heading
import com.example.bookswap.screens.appComponents.Heading2
import com.example.bookswap.screens.appComponents.Password
import com.example.bookswap.screens.appComponents.RegisterButton
import com.example.bookswap.screens.appComponents.UploadProfileImg
import com.example.bookswap.viewModel.UserAuthViewModel

@Composable
fun RegistrationScreen(
    viewModel: UserAuthViewModel?,
    navController: NavController?
) {
    val signUpFlow = viewModel?.signUpFlow?.collectAsState()

    val email = remember { mutableStateOf("") }
    val isEmailError = remember { mutableStateOf(false) }
    val emailErrorText = remember { mutableStateOf("") }

    val password = remember { mutableStateOf("") }
    val isPasswordError = remember { mutableStateOf(false) }
    val passwordErrorText = remember { mutableStateOf("") }

    val fullName = remember { mutableStateOf("") }
    val isFullNameError = remember { mutableStateOf(false) }

    val phoneNumber = remember { mutableStateOf("") }
    val isPhoneNumberError = remember { mutableStateOf(false) }

    val profileImg = remember { mutableStateOf(Uri.EMPTY) }
    val isProfileImgError = remember { mutableStateOf(false) }
    //val profileImgErrorText = remember { mutableStateOf("Profile image is required.") }


    val showPassword = remember { mutableStateOf(false) }

    val buttonEnabled = remember { mutableStateOf(true) }
    val isLoading = remember { mutableStateOf(false) }
    val isError = remember { mutableStateOf(false) }
    val errorText = remember { mutableStateOf("") }


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
                Spacer(modifier = Modifier.height(32.dp))

                // centriram naslov
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Heading(text = "Only for book lovers!")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Heading2(secondary_text = "Create your account to explore and share beautiful stories...")

                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = Color(0xFFBCAAA4),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(2.dp))
                        CustomLabel(label = "Choose your profile picture by clicking on the icon below:")
                        Spacer(modifier = Modifier.height(8.dp))
                        UploadProfileImg(profileImg, isProfileImgError)
//                        if (isProfileImgError.value) {
//                            Text(
//                                text = profileImgErrorText.value,
//                                color = Color.Red,
//                                modifier = Modifier.padding(top = 4.dp)
//                            )
//                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Full name")

                Spacer(modifier = Modifier.height(2.dp))
                CustomInput(
                    hint = "Sanja Simic",
                    value = fullName,
                    isEmail = false,
                    isError = isFullNameError,
                    errorText = emailErrorText
                )

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Email address")

                Spacer(modifier = Modifier.height(2.dp))
                CustomInput(
                    hint = "sanja.simic@gmail.com",
                    value = email,
                    isEmail = true,
                    isError = isEmailError,
                    errorText = emailErrorText
                )

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Phone number")

                Spacer(modifier = Modifier.height(2.dp))
                CustomInput(
                    hint = "0600220423",
                    value = phoneNumber,
                    isEmail = false,
                    isNumber = true,
                    isError = isPhoneNumberError,
                    errorText = emailErrorText
                )

                Spacer(modifier = Modifier.height(16.dp))
                CustomLabel(label = "Password")

                Spacer(modifier = Modifier.height(2.dp))
                Password(
                    inputValue = password,
                    hint = "fde1234KL!",
                    isError = isPasswordError,
                    errorText = passwordErrorText
                )

                Spacer(modifier = Modifier.height(2.dp))
                    RegisterButton(
                    onClick = {
                        isProfileImgError.value = false
                        isEmailError.value = false
                        isPasswordError.value = false
                        isFullNameError.value = false
                        isPhoneNumberError.value = false
                        isError.value = false
                        isLoading.value = true

                        if (profileImg.value == Uri.EMPTY) {
                            isProfileImgError.value = true
                            isLoading.value = false
                        } else if (fullName.value.isEmpty()) {
                            isFullNameError.value = true
                            isLoading.value = false
                        } else if (email.value.isEmpty()) {
                            isEmailError.value = true
                            isLoading.value = false
                        } else if (phoneNumber.value.isEmpty()) {
                            isPhoneNumberError.value = true
                            isLoading.value = false
                        } else if (password.value.isEmpty()) {
                            isPasswordError.value = true
                            isLoading.value = false
                        } else {
                            viewModel?.register(
                                profileImg = profileImg.value,
                                fullName = fullName.value,
                                email = email.value,
                                phoneNumber = phoneNumber.value,
                                password = password.value
                            )
                        }
                    },
                    buttonText = "Sign Up",
                    isEnabled = buttonEnabled,
                    isLoading = isLoading,
                )

               Spacer(modifier = Modifier.height(17.dp))
                Alternative(
                    text = "Already have an account? ",
                    link = "Sign In",
                    onClick = {
                        navController?.navigate(Routes.loginScreen)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                    signUpFlow?.value?.let {
                        when (it) {
                            is Resource.Failure -> {
                                isLoading.value = false
                                isError.value = true
                                errorText.value = it.exception.message.toString()
                                Log.e("[ERROR]", it.exception.message.toString())
                            }

                            is Resource.Success -> {
                                isLoading.value = false
                                LaunchedEffect(Unit) {
                                    navController?.navigate(Routes.loginScreen) {
                                        popUpTo(Routes.startScreen) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }

                            is Resource.Loading -> {}

                            null -> Log.d("SignUpScreen", "SignUp flow doesn't exist!")
                        }
                    }
                if (isError.value) {
                    Text(
                        text = errorText.value,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF6D4C41).copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFF5E6CC))
            }
        }
    }
}
