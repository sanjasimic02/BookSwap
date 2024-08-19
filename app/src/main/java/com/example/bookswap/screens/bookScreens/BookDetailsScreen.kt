package com.example.bookswap.screens.bookScreens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.bookswap.viewModel.BookViewModel

@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: String,
    bookViewModel: BookViewModel
) {
    // Fetch book details using the bookId
   // val book = bookViewModel.getBookById(bookId).collectAsState(initial = null).value

    // Display book details here

}
