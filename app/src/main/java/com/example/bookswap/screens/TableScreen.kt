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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.bookswap.R
import com.example.bookswap.models.Book
import com.example.bookswap.navigation.Routes
import com.example.bookswap.repositories.Resource
import com.example.bookswap.screens.bookScreens.FilterDialog
import com.example.bookswap.viewModel.BookViewModel
import com.example.bookswap.viewModel.UserAuthViewModel

@Composable
fun TableScreen(
    navController: NavController,
    userViewModel: UserAuthViewModel,
    bookViewModel : BookViewModel,
) {
    //val context = LocalContext.current
    val bookCollection = bookViewModel.books.collectAsState()
    val booksList = remember { mutableStateListOf<Book>() }

    val isDialogOpen = remember { mutableStateOf(false) }
    val filters = remember { mutableStateOf(mapOf<String, String>()) }
    val filtersApplied = remember { mutableStateOf(false) }
    val filteredBooksList = remember { mutableStateListOf<Book>() }

    val isSearchBarVisible = remember { mutableStateOf(false) }
    val searchApplied = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    LaunchedEffect(bookCollection.value, filtersApplied.value, searchQuery.value, searchApplied.value) {
        Log.d("TableScreen", "LaunchedEffect triggered")
        bookCollection.value.let {
            when (it) {
                is Resource.Success -> {
                    booksList.clear()
                    booksList.addAll(it.result)
                    if(filtersApplied.value && searchApplied.value)
                    {
                        filteredBooksList.clear()
                        filteredBooksList.addAll(
                            it.result.filter { book ->
                                (book.title.contains(searchQuery.value, ignoreCase = true)) && ( //umesto da mi title bude u filters
                                        (filters.value["author"]?.let { book.author.contains(it, ignoreCase = true) } ?: true) &&
                                                (filters.value["genre"]?.let { book.genre.contains(it, ignoreCase = true) } ?: true) &&
                                                (filters.value["language"]?.let { book.language.contains(it, ignoreCase = true) } ?: true))
                            }
                        )
                    }
                    else if (filtersApplied.value) {
                        filteredBooksList.clear()
                        filteredBooksList.addAll(
                            it.result.filter { book ->
                                //(book.title.contains(searchQuery.value, ignoreCase = true)) || ( //umesto da mi title bude u filters
                                        (filters.value["author"]?.let { book.author.contains(it, ignoreCase = true) } ?: true) &&
                                        (filters.value["genre"]?.let { book.genre.contains(it, ignoreCase = true) } ?: true) &&
                                        (filters.value["language"]?.let { book.language.contains(it, ignoreCase = true) } ?: true)
                            }
                        )
                    }
                    else if(searchApplied.value)
                    {
                        filteredBooksList.clear()
                        filteredBooksList.addAll(
                            it.result.filter { book ->
                                book.title.contains(searchQuery.value, ignoreCase = true)
                            }
                        )
                    }
                    else {
                        filteredBooksList.clear()
                        filteredBooksList.addAll(it.result)
                    }
                    Log.d("MapScreen", "Filtered books list: ${filteredBooksList.toList()}")
                }
                is Resource.Failure -> TODO()
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
            Row( modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Text(
//                    text = "bookSwap",
//                    style = TextStyle(
//                        fontSize = 18.sp,
//                        fontStyle = FontStyle.Italic,
//                        color = Color(0xFFEDC9AF)
//                    )
//                )
                IconButton(
                    onClick = {
                        navController.navigate(Routes.mapScreen)
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color(0xFF6D4C41),
                            shape = RoundedCornerShape(12.dp)
                        ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.map),
                        contentDescription = "Map Icon",
                        tint = Color(0xFFEDC9AF),
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    //modifier = Modifier.fillMaxWidth(),
                    //horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Search Icon
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFFEDC9AF),
                        modifier = Modifier
                            .clickable {
                                isSearchBarVisible.value = true
                            }
                    )

                    Button(
                        onClick = {
                            isDialogOpen.value = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6D4C41),
                            contentColor = Color(0xFF3C0B1A)
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "Filters",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEDC9AF),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFFEDC9AF),
                        modifier = Modifier
                            .clickable {
                                searchApplied.value = false
                                filtersApplied.value = false
                                isSearchBarVisible.value = false
                            },
                    )

                    Spacer(modifier = Modifier.width(12.dp))

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
        }

        // Search Bar
        if (isSearchBarVisible.value) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    value = searchQuery.value,
                    onValueChange = { newValue ->
                        searchQuery.value = newValue
                    },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Search book by title...",
                            style = TextStyle(
                                color = Color.Gray
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "",
                            tint = Color(0xFF6D4C41)
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    visualTransformation = VisualTransformation.None,
                    keyboardOptions = KeyboardOptions.Default
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        searchApplied.value = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color(0xFF6D4C41)
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(text = "Apply")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color(0xFF6D4C41),
                    modifier = Modifier
                        .clickable {
                            searchApplied.value = false
                            filtersApplied.value = false
                            isSearchBarVisible.value = false
                        },
                    )
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
        filteredBooksList.forEach { book ->
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

        FilterDialog(
            isDialogOpen = isDialogOpen,
            onDismissRequest = { isDialogOpen.value = false },
            onApplyFilters = { newFilters ->
                filters.value = newFilters
                Log.d("TableScreen", "Filters: ${filters.value}")
                filtersApplied.value = true
                Log.d("TableScreen", "Filters Applied: ${filtersApplied.value}")
            },
            false
        )
    }
}

