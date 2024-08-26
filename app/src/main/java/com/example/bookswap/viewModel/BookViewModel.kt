package com.example.bookswap.viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookswap.models.Book
import com.example.bookswap.models.Comment
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

//    fun getBookById(bookId: String): Book? {
//        return books.value.data?.find { it.id == bookId }
//    }



    fun updateUserPoints(userId: String, pointsToAdd: Int) {
        viewModelScope.launch {
            val result = repository.updateUserPoints(userId, pointsToAdd)
            if (result is Resource.Failure) {
                // Handle failure case
            }
        }
    }

    fun addCommentToBook(bookId: String, comment: Comment) = viewModelScope.launch {
        try {
            repository.addCommentToBook(bookId, comment)
        } catch (e: Exception) {
            Log.e("BookViewModel", "Error adding comment to book", e)
        }
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

    // Ova funkcija menja status knjige u 'unavailable'
    fun updateBookStatus(bookId: String, newStatus: String) = viewModelScope.launch {
        try {

            repository.updateBookStatus(bookId, newStatus)

        } catch (e: Exception) {
            Log.e("BookViewModel", "Error updating book status", e)
        }
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