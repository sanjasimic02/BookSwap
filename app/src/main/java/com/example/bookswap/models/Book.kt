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
    //val coverImage: String = "",
    val bookImages: List<String> = emptyList(),
    //val rating: Float = 0.0f,
    val swapStatus: String = "" //da li je vec iznajmljena, jer ako jeste onda valjda ne treba da se vidi ili da bude onemoguceno neko dugme
)