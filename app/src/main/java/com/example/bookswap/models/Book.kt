package com.example.bookswap.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Book (
    @DocumentId var id: String = "",
    val userId: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val genre: String = "",
    val language: String = "",
    val bookImages: List<String> = emptyList(),
    val swapStatus: String = "", //da li je vec iznajmljena
    val comments: List<Comment> = emptyList() // list of comments
)

data class Comment(
    //val userId: String = "",
    val userName: String = "",
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)