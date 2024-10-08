package com.example.bookswap.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookswap.R
import com.example.bookswap.models.Book
import com.example.bookswap.navigation.Routes
import com.example.bookswap.repositories.Resource
import com.example.bookswap.viewModel.BookViewModel
import com.example.bookswap.viewModel.UserAuthViewModel


@Composable
fun UserProfileScreen1(
    userId : String,
    navController: NavController,
    viewModel: UserAuthViewModel,
    bookViewModel : BookViewModel,
) {

    val buttonIsEnabled = remember { mutableStateOf(true) }
    val showDialog = remember { mutableStateOf(false) }

    val currentUserState = viewModel.userByIdFlow.collectAsState()
    LaunchedEffect(userId) {
        viewModel.getUserById(userId)
    }

    val currentUser = when (val result = currentUserState.value) {
        is Resource.Success -> result.result
        is Resource.Failure -> {
            Log.e("User data:", result.exception.message ?: "Unknown error")
            null
        }
        else -> null
    }

    if (currentUser != null) {
        bookViewModel.getUsersBooks(currentUser.id)
    }
    else
    {
        //greska
    }

    val bookCollection = bookViewModel.userBooks.collectAsState()
    val listBooks = remember {
        mutableStateListOf<Book>()
    }

    val red = Color(0xFF8B0000) // Osnovna boja
    val lightenedRed = Color(ColorUtils.blendARGB(red.toArgb(), Color.White.toArgb(), 0.2f)) // Posvetljivanje

    bookCollection.value.let {
        when(it){
            is Resource.Success -> {
                //Log.d("Book data:", it.toString())
                listBooks.clear()
                listBooks.addAll(it.result)
            }
            is Resource.Loading -> {

            }
            is Resource.Failure -> {
                Log.e("Book data:", it.toString())
            }
            null -> {}
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF3E0))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF6D4C41))
                .padding(1.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "bookSwap member",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFFEDC9AF)
                    )
                )

                Button(
                    onClick = {
                        showDialog.value = true
                    },
                    enabled = buttonIsEnabled.value,
                    modifier = Modifier
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41),
                        contentColor = Color(0xFF3C0B1A)
                    )
                ) {
                    Text(
                        text = "Sign out",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDC9AF),
                        )
                    )
                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Profile info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFAF3E0))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = currentUser?.profileImg, //URL string pointing to the image stored in Firebase Storage
                    //u pozadini se radi fetch slike sa te lokacije
                    // Coil handles the HTTP request, fetches the image from the provided URL, caches it, and then displays it within the AsyncImage composable
                            contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .background(
                            Color.White,
                            RoundedCornerShape(70.dp)
                        )
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    currentUser?.fullName?.replace('+', ' ')?.let {
                        Text(
                            text = it,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6D4C41)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Phone: ${currentUser?.phoneNumber}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Email: ${currentUser?.email}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically // Poravnanje teksta i bedža po vertikali
                    ) {
                        Text(
                            text = "Total points: ${currentUser?.totalPoints}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color(0xFF6D4C41),
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        var badge_id = R.drawable.bronza2;
                        if(currentUser?.totalPoints == 0)
                        {
                            badge_id = R.drawable.bronza2;
                        }
                        else if(currentUser?.totalPoints in 1..100)
                        {
                            badge_id = R.drawable.srebro;
                        }
                        else
                        {
                            badge_id = R.drawable.zlato;
                        }
                        Spacer(modifier = Modifier.width(8.dp)) // Razmak između teksta i bedža

                        Image(
                            painter = painterResource(id = badge_id), // Zameniti sa odgovarajućim ID-jem resursa za bedž
                            contentDescription = "Badge",
                            modifier = Modifier.size(24.dp) // Veličina bedža
                        )

                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            navController.navigate(Routes.leaderboardScreen)
                        },
                        enabled = buttonIsEnabled.value,
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(0.dp)
                            .border(1.dp, Color(0xFF6D4C41), RoundedCornerShape(12.dp)), // Dodaje tanak border
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF6D4C41)
                        )
                    ) {
                        Text(
                            text = "See ranking list",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6D4C41),
                                fontStyle = FontStyle.Italic
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(16.dp)
                .border(
                    width = 2.dp,
                    color = Color(0xFF6D4C41),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(top = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Library of Available Books",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D4C41)
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .offset(y = (-28).dp)
                        .background(Color(0xFFFAF3E0))
                        .padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Prikaz liste knjiga
                if (listBooks.isEmpty()) {
                    Text(
                        text = "No books available.",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color(0xFF6D4C41)
                        )
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFAF3E0))
                    ) {
                        items(listBooks) { book ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF6D4C41),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(8.dp)
                                    .clickable(
                                        indication = rememberRipple(bounded = true),
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        navController.navigate("bookDetails/${book.id}")
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Title: ${book.title}",
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF6D4C41)
                                            )
                                        )
                                        Text(
                                            text = "Author: ${book.author}",
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                color = Color(0xFF6D4C41)
                                            )
                                        )
                                        Text(
                                            text = "Genre: ${book.genre}",
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                color = Color.Gray
                                            )
                                        )
                                    }

                                    if (book.swapStatus == "unavailable") {
                                        Button(
                                            onClick = { bookViewModel.updateBookStatus(book.id, "available") },
                                            enabled = buttonIsEnabled.value,
                                            modifier = Modifier
                                                .padding(top = 16.dp),
                                            //.size(width = 200.dp, height = 50.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = lightenedRed,//Color(0xFF8B0000),
                                                contentColor = Color(0xFF3C0B1A)
                                            )
                                        ) {
                                            Text(
                                                text = "Available",
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFEDC9AF)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }


        //Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        navController.navigate(Routes.mapScreen)
                    },
                    enabled = buttonIsEnabled.value,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(width = 200.dp, height = 50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41),
                        contentColor = Color(0xFF3C0B1A)
                    )
                ) {
                    Text(
                        text = "View map",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDC9AF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        navController.navigate(Routes.tableScreen)
                    },
                    enabled = buttonIsEnabled.value,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(width = 200.dp, height = 50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41),
                        contentColor = Color(0xFF3C0B1A)
                    )
                ) {
                    Text(
                        text = "All Books",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDC9AF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        navController.navigate(Routes.serviceSettings)
                    },
                    enabled = buttonIsEnabled.value,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(width = 200.dp, height = 50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41),
                        contentColor = Color(0xFF3C0B1A)
                    )
                ) {
                    Text(
                        text = "Service Settings",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDC9AF)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            //title = { Text(text = "Confirm Sign Out") },
            text = { Text(
                text = "Are you sure you want to sign out?",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEDC9AF),
                )
            )},
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                        viewModel.logOut()
                        navController.navigate(Routes.loginScreen)
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text("No")
                }
            },
            modifier = Modifier
                .background(Color(0xFF6D4C41).copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp))
        )
    }
}