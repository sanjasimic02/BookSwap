package com.example.bookswap.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookswap.models.Book
import com.example.bookswap.repositories.Resource
import com.example.bookswap.viewModel.BookViewModel
import com.example.bookswap.viewModel.UserAuthViewModel

@Composable
fun TableScreen(
    navController: NavController,
    userViewModel: UserAuthViewModel,
    bookViewModel : BookViewModel,
) {
    val context = LocalContext.current
    val bookCollection = bookViewModel.books.collectAsState()
    val booksList = remember { mutableStateListOf<Book>() }

    LaunchedEffect(bookCollection) {
        bookCollection.value.let {
            when (it) {
                is Resource.Success -> {
                    booksList.clear()
                    booksList.addAll(it.result)
                }
                is Resource.Loading -> {
                    Log.d("MapScreen", "Loading books...")
                }
                is Resource.Failure -> {
                    Log.e("MapScreen", "Failed to load books:")
                }
                null -> {
                    Log.d("MapScreen", "No books available")
                }
                Resource.Loading -> TODO()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAF3E0))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp) // Postavljanje fiksne visine za header
                .background(Color(0xFF6D4C41))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "bookSwap",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color(0xFFEDC9AF)
                    )
                )

                Spacer(modifier = Modifier.width(4.dp)) // Razmak između dugmića
                Button(
                    onClick = {
                        navController.navigateUp()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6D4C41),
                        contentColor = Color(0xFF3C0B1A)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(
                        text = "User Profile",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDC9AF),
                        )
                    )
                }
            }
        }

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF8D6E63))
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Title",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "Author",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "Genre",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Text(
                text = "Language",
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        booksList.forEach { book ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            navController.navigate("bookDetails/${book.id}")
                        }
                    )
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 8.dp)
            ) {
                // Status indicator
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            color = when (book.swapStatus) {
                                "available" -> Color.Green
                                "unavailable" -> Color.Red
                                else -> Color.Gray
                            },
                            shape = RoundedCornerShape(50)
                        )
                        .padding(end = 8.dp)
                )
                Text(
                    text = book.title,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF6D4C41)
                    )
                )
                Text(
                    text = book.author,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF6D4C41)
                    )
                )
                Text(
                    text = book.genre,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF6D4C41)
                    )
                )
                Text(
                    text = book.language,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF6D4C41)
                    )
                )
            }
            Divider(color = Color(0xFF6D4C41), thickness = 0.5.dp)
        }
    }
}
