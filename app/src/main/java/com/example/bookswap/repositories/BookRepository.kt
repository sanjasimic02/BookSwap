package com.example.bookswap.repositories

import android.net.Uri
import com.example.bookswap.models.Book
import com.example.bookswap.models.Comment
import com.google.android.gms.maps.model.LatLng

interface BookRepository {

    suspend fun saveBook(
        location: LatLng,
        title: String,
        author: String,
        description: String,
        genre: String,
        language: String,
        bookImages : List<Uri>
        //coverImage: Uri,
       // rating: String, //mozda ne ovde
    ): Resource<String>

    suspend fun getAllBooks(): Resource<List<Book>>
    suspend fun getUsersBooks(
        uid: String //kog korisnika
    ): Resource<List<Book>>

    suspend fun updateUserPoints(uid : String, points : Int): Resource<String>
    suspend fun updateBookStatus(bookId: String, newStatus: String)
    suspend fun getCommentsForBook(bookId: String): List<Comment>
    suspend fun addCommentToBook(uid: String, bookId: String, comment: Comment)
}