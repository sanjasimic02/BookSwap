package com.example.bookswap.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.bookswap.R
import com.example.bookswap.models.Book
import com.example.bookswap.navigation.Routes
import com.example.bookswap.repositories.Resource
import com.example.bookswap.viewModel.BookViewModel
import com.example.bookswap.viewModel.UserAuthViewModel

@Composable
fun BookOwnerScreen(
    userId: String,
    navController: NavController,
    bookViewModel : BookViewModel,
    userViewModel : UserAuthViewModel
) {

    val buttonIsEnabled = remember { mutableStateOf(true) }
    val isLoading = remember { mutableStateOf(false) }

    val currentUserState = userViewModel.userByIdFlow.collectAsState()
    LaunchedEffect(userId) {
        userViewModel.getUserById(userId)
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

    bookCollection.value.let {
        when(it){
            is Resource.Success -> {
                Log.d("Book data:", it.toString())
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

                androidx.compose.material3.Button(
                    onClick = {
                        navController.navigateUp()
                    },
                    modifier = Modifier
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41),
                        contentColor = Color(0xFF3C0B1A)
                    )
                ) {
                    androidx.compose.material3.Text(
                        text = "Back",
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
                if (currentUser != null) {
                    AsyncImage(
                        model = currentUser.profileImg,
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
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    if (currentUser != null) {
                        Text(
                            text = currentUser.fullName.replace('+', ' '),
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6D4C41)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (currentUser != null) {
                        Text(
                            text = "Phone: ${currentUser.phoneNumber}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (currentUser != null) {
                        Text(
                            text = "Email: ${currentUser.email}",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    if (currentUser != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically // Poravnanje teksta i bedža po vertikali
                        ) {
                            Text(
                                text = "Total points: ${currentUser.totalPoints}",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = Color(0xFF6D4C41),
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            var badge_id = R.drawable.bronza2;
                            if(currentUser.totalPoints == 0)
                            {
                                badge_id = R.drawable.bronza2;
                            }
                            else if(currentUser.totalPoints in 1..100)
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
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
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
            }
        }

    }
}