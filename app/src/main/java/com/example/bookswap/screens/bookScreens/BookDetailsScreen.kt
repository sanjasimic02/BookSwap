package com.example.bookswap.screens.bookScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.bookswap.models.Book

@Composable
fun BookDetailsScreen(
    book: Book,
    onBack: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5DC)
    ) {
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
                    fontStyle =  FontStyle.Italic
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

            Button(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFF6D4C41))
            ) {
                Text(text = "Back", style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEDC9AF),
                ))
            }
        }
    }
}
