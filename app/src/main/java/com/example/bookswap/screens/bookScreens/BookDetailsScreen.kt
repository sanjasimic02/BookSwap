package com.example.bookswap.screens.bookScreens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.bookswap.models.Book
import com.example.bookswap.navigation.Routes
import com.example.bookswap.viewModel.BookViewModel
import com.example.bookswap.viewModel.UserAuthViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BookDetailsScreen(
    book: Book,
    currentUserId : String,
    onBack: () -> Unit,
    bookViewModel: BookViewModel,
    userAuthViewModel: UserAuthViewModel,
    navController: NavController
) {

    val context = LocalContext.current
    val isBookOwner = book.userId == currentUserId
    val scope = rememberCoroutineScope() // Kreira CoroutineScope

    val showSheet = remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val (isButtonEnabled, setButtonEnabled) = remember { mutableStateOf(true) }

    // Control the sheet
    LaunchedEffect(showSheet.value) {
        if (showSheet.value) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    fun handleRentBookClick() {

        bookViewModel.updateUserPoints(book.userId, 10)
        bookViewModel.updateBookStatus(book.id, "unavailable")
        Toast.makeText(context, "Book is successfully rented!", Toast.LENGTH_SHORT).show()
        setButtonEnabled(false) // Disable the button after clicking
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            AddCommentScreen(
                bookViewModel = bookViewModel,
                userAuthViewModel = userAuthViewModel,
                book = book,
                isBookOwner = book.userId == currentUserId,
                onDismiss = { showSheet.value = false } // skriva sheet
            )
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAF3E0))
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .background(Color(0xFF6D4C41))
                    .padding(1.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f)
                    ) {
                        androidx.compose.material3.Text(
                            text = "bookSwap",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color(0xFFEDC9AF)
                            ),
                            modifier = Modifier
                                .padding(6.dp)
                                .align(Alignment.CenterStart) // Centrira vertikalno
                        )
                    }

                    if (!isBookOwner) {
                        androidx.compose.material3.Button(
                            onClick = {
                                navController.navigate("${Routes.bookOwnerScreen}/${book.userId}")
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
                                text = "Visit Book Owner",
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


            Spacer(modifier = Modifier.height(16.dp))
            if(book.swapStatus == "unavailable") {
                Text(
                    text = "*This book is currently rented!",
                    style = TextStyle(
                        fontSize = 16.sp,
                        //fontWeight = FontWeight.Bold,
                        color = Color.Red,
                    ),
                    modifier = Modifier.padding(4.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF5F5DC))
                    .padding(16.dp)
            ) {

                BasicText(
                    text = book.title,
                    style = TextStyle(
                        fontSize = 32.sp,
                        color = Color(0xFF3E2723),
                        fontFamily = MaterialTheme.typography.h4.fontFamily
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Divider(color = Color(0xFF8D6E63), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                BasicText(
                    text = "By ${book.author}",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color(0xFF3E2723),
                        fontFamily = MaterialTheme.typography.subtitle1.fontFamily,
                        fontStyle = FontStyle.Italic
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Divider(color = Color(0xFF8D6E63), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                BasicText(
                    text = "Genre: ${book.genre}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFF3E2723),
                        fontFamily = MaterialTheme.typography.body1.fontFamily
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))

                Divider(color = Color(0xFF8D6E63), thickness = 1.dp)
                Spacer(modifier = Modifier.height(8.dp))

                BasicText(
                    text = "Language: ${book.language}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFF3E2723),
                        fontFamily = MaterialTheme.typography.body1.fontFamily
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color(0xFF8D6E63), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                BasicText(
                    text = book.description,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color(0xFF3E2723),
                        fontFamily = MaterialTheme.typography.body1.fontFamily,
                        fontStyle = FontStyle.Italic
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Placeholder for book images
                if (book.bookImages.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(vertical = 16.dp)
                    ) {
                        items(book.bookImages) { imageUrl ->
                            Image(
                                painter = rememberImagePainter(data = imageUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(150.dp)
                                    .padding(horizontal = 8.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                if (!isBookOwner && book.swapStatus == "available") {
                    Button(
                        onClick = {
                            scope.launch {
                                // Koristi `userAuthViewModel.contactOwner` unutar korutine
                                if (book.userId.isNotEmpty()) {
                                    userAuthViewModel.contactOwner(context, book.userId)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Phone number is not available",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF6D4C41))
                    ) {
                        Text(
                            text = "Contact Owner", style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEDC9AF),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (!isBookOwner && book.swapStatus == "available") {
                    Button(
                        onClick = { handleRentBookClick() },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF6D4C41)),
                        enabled = isButtonEnabled
                    ) {
                        Text(
                            text = "Rent Book", style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFEDC9AF),
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick =
                    {
                        navController.navigate(Routes.mapScreen)
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF6D4C41))
                ) {
                    Text(
                        text = "Back to Map", style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDC9AF),
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                //KOMENTARI
                Button(
                    onClick = {
                        showSheet.value = true
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF6D4C41))
                ) {
                    Text(
                        text = "Open Comments", style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDC9AF),
                        )
                    )
                }
            }
        }
    }
}
