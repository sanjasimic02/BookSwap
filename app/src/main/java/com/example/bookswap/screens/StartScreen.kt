package com.example.bookswap.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookswap.navigation.Routes
import com.example.bookswap.screens.appComponents.StartImage
import com.example.bookswap.viewModel.UserAuthViewModel

@Composable
fun StartScreen(
    viewModel: UserAuthViewModel,
    navController: NavController
) {

    val currentUser = viewModel.currentUserFlow.collectAsState()
    val currentUserId by remember { mutableStateOf(viewModel.getCurrentUserId()) }


    LaunchedEffect(currentUser) {
        Log.d("[DEBUG]", "CurrentUser: ${currentUser.value}")
        if(currentUser.value != null)
        {
            navController.navigate(Routes.userScreen1 + "/$currentUserId")
        }

//        currentUser?.value?.let { user ->
//            val currUserJSON = Gson().toJson(user)
//            val encodedUsr = URLEncoder.encode(currUserJSON, StandardCharsets.UTF_8.toString())
//            Log.d("[DEBUG]", "Navigating to: ${Routes.userScreen + "/$encodedUsr"}")
//            navController.navigate(Routes.userScreen + "/$encodedUsr") {
//                popUpTo(Routes.startScreen) {
//                    inclusive = true
//                }
//            }
//        }
    }

    StartImage {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(30.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x80F5E6CC))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Discover and Share the Joy of Books!",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D4C41),
                        fontFamily = FontFamily.Serif
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Swap books with people nearby and share your reading experiences.",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF3C0B1A),
                        fontFamily = FontFamily.Serif
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate(Routes.loginScreen) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Start",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

