package com.example.bookswap.viewModel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookswap.models.Book
import com.example.bookswap.repositories.BookRepositoryImpl
import com.example.bookswap.repositories.Resource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {

    val repository = BookRepositoryImpl()
    private val _bookFlow = MutableStateFlow<Resource<String>?>(null)
    val bookFlow: StateFlow<Resource<String>?> = _bookFlow
    private val _books = MutableStateFlow<Resource<List<Book>>>(Resource.Success(emptyList()))
    val books: StateFlow<Resource<List<Book>>> get() = _books
    private val _userBooks = MutableStateFlow<Resource<List<Book>>>(Resource.Success(emptyList()))
    val userBooks: StateFlow<Resource<List<Book>>> get() = _userBooks

    init {
        getAllBooks()
    }
    fun getAllBooks() = viewModelScope.launch {
        _books.value = repository.getAllBooks()
    }

    fun saveBook(
        location: MutableState<LatLng?>,
        description: String,
        title: String,
        author: String,
        genre: String,
        language: String,
        bookImages : List<Uri>
    ) = viewModelScope.launch{
        _bookFlow.value = Resource.Loading
        repository.saveBook(
            location = location.value!!,
            description = description,
            title = title,
            author = author,
            genre = genre,
            language = language,
            bookImages = bookImages
        )
        _bookFlow.value = Resource.Success("Knjiga je uspesno dodata!")
    }

    fun getUsersBooks(
        uid: String
    ) = viewModelScope.launch {
        _userBooks.value = repository.getUsersBooks(uid)
    }
}

class BookViewModelFactory: ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BookViewModel::class.java)){
            return BookViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}