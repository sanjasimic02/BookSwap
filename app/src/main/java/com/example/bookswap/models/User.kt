package com.example.bookswap.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId var id: String = "",
    val email: String = "", //?
    val password: String = "", //?
    val fullName: String = "",
    val phoneNumber: String = "",
    val profileImg: String = ""
)
