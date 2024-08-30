package com.example.bookswap.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId var id: String = "",
    val email: String = "", //?
    //val password: String = "", //?
    val fullName: String = "",
    val phoneNumber: String = "",
    val profileImg: String = "",
    val totalPoints: Int = 0 //kad doda knjigu dobija +5p, kad nekom iznajmi +10p, kad mu neko ostavi komentar na knjigu +7p npr
)
