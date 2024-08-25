package com.example.bookswap

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.bookswap.ui.theme.BookSwapTheme
import com.example.bookswap.viewModel.BookViewModel
import com.example.bookswap.viewModel.BookViewModelFactory
import com.example.bookswap.viewModel.UserAuthViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: UserAuthViewModel by viewModels()
    private val bookViewModel: BookViewModel by viewModels{
        BookViewModelFactory()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookSwapTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookSwapApp(viewModel, bookViewModel)
                }

            }
        }
    }
}

