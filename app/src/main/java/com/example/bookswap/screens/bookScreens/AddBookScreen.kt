package com.example.bookswap.screens.bookScreens

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookswap.screens.appComponents.BSBackground
import com.example.bookswap.screens.appComponents.BookDataInput
import com.example.bookswap.screens.appComponents.CustomLabel
import com.example.bookswap.screens.appComponents.Heading
import com.example.bookswap.screens.appComponents.Heading2
import com.example.bookswap.screens.appComponents.UploadBookImages
import com.example.bookswap.viewModel.BookViewModel
import com.google.android.gms.maps.model.LatLng

@Composable
fun AddBookScreen(
    bookViewModel: BookViewModel,
    navController: NavController,
    location: MutableState<LatLng?>, //da se zna gde se dodaje
    onDismiss: () -> Unit

) {
    val bookFlow = bookViewModel.bookFlow.collectAsState()

    val title = remember { mutableStateOf("") }
    val author = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val genre = remember { mutableStateOf("") }
    val language = remember { mutableStateOf("") }

    val coverImage = remember { mutableStateOf(Uri.EMPTY) }
    val isCoverImageError = remember { mutableStateOf(false) }


    val bookImages = remember { mutableStateOf("") }
    //var swapStatus by remember { mutableStateOf(TextFieldValue("")) } ja da ga postavim inicijalno na dostupan

    val selectedMoreImages = remember {
        mutableStateOf<List<Uri>>(emptyList())
    }

    BSBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, end = 30.dp, top = 14.dp, bottom = 14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Heading(text = "Add a new book!")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Heading2(secondary_text = "Expand Your Exchange Library...")

                Spacer(modifier = Modifier.height(8.dp))

                CustomLabel(label = "Title")
                Spacer(modifier = Modifier.height(2.dp))
                BookDataInput(
                    hint = "Call me by your name",
                    value = title
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomLabel(label = "Author")
                Spacer(modifier = Modifier.height(2.dp))
                BookDataInput(
                    hint = "Andre Aciman",
                    value = author
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomLabel(label = "Description")
                Spacer(modifier = Modifier.height(2.dp))
                BookDataInput(
                    hint = "Elio, a teenager, develops feelings for Oliver...",
                    value = description
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomLabel(label = "Genre")
                Spacer(modifier = Modifier.height(2.dp))
                BookDataInput(
                    hint = "Romance/Drama",
                    value = genre
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomLabel(label = "Language")
                Spacer(modifier = Modifier.height(2.dp))
                BookDataInput(
                    hint = "English",
                    value = language
                )
                Spacer(modifier = Modifier.height(16.dp))

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
                        CustomLabel(label = "Choose book images to make it look better.")
                        Spacer(modifier = Modifier.height(8.dp))
                        UploadBookImages(selectedMoreImages)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        bookViewModel.saveBook(
                            location = location,
                            description = description.value,
                            title = title.value,
                            author = author.value,
                            genre = genre.value,
                            language = language.value,
                            bookImages = selectedMoreImages.value
                        )
                        //dodaje knjigu!!!
                        //sad na profilu korisnika zelim da prikazem dostupne knjige, ali i na mapi??
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .size(width = 200.dp, height = 50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF6D4C41),
                        contentColor = Color(0xFFEDC9AF)
                    )
                ) {
                    Text("Add Book")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // "x" dugme u gornjem desnom uglu
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp, end = 20.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF6D4C41),
                        contentColor = Color(0xFFEDC9AF)
                    )
                ) {
                    Text("X")
                }
            }
        }
    }
}


