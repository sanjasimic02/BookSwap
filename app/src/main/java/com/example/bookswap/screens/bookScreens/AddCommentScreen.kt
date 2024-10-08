package com.example.bookswap.screens.bookScreens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookswap.models.Book
import com.example.bookswap.models.Comment
import com.example.bookswap.repositories.Resource
import com.example.bookswap.viewModel.BookViewModel
import com.example.bookswap.viewModel.UserAuthViewModel


@Composable
fun AddCommentScreen(
    bookViewModel : BookViewModel,
    userAuthViewModel: UserAuthViewModel,
    book: Book,
    isBookOwner: Boolean,
    onDismiss: () -> Unit
) {
    val (newComment, setNewComment) = remember { mutableStateOf("") }
    val commentState = bookViewModel.specificBookComments.collectAsState()

    val currentUserFlow = userAuthViewModel.currentUserFlow.collectAsState()
    val currentUser = when (val result = currentUserFlow.value) {
        is Resource.Success -> result.result
        is Resource.Failure -> {
            Log.e("User data:", result.exception.message ?: "Unknown error")
            null
        }
        else -> null
    }

    LaunchedEffect(book.id) {
        bookViewModel.loadCommentsForBook(book.id)
    }

    fun handleAddComment() {
        //if (newComment.isNotBlank()) {
            val comment = Comment(
                userName = currentUser?.fullName ?: "Anonymous",
                comment = newComment
            )
        //}
        bookViewModel.addCommentToBook(book.userId, book.id, comment)
        setNewComment("") //ocisti input
        //bookViewModel.loadCommentsForBook(book.id) // Refresh
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Comments Section
            Text(
                text = "Comments",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6D4C41)
                ),
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(commentState.value) { comment ->
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = comment.userName.ifBlank { "Anonymous" },
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3E2723)
                            )
                        )
                        Text(
                            text = comment.comment,
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = Color(0xFF3E2723)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Add Comment Input
            if (!isBookOwner) {
                TextField(
                    value = newComment,
                    onValueChange = setNewComment,
                    label = { Text("Add your comment") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { handleAddComment() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF6D4C41))
                ) {
                    Text(
                        text = "Submit Comment",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEDC9AF),
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // "X" button in the top-right corner
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
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
                Text("x")
            }
        }
    }
}