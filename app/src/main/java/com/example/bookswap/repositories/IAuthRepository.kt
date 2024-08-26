package com.example.bookswap.repositories

import android.content.Context
import android.net.Uri
import com.example.bookswap.models.User
import com.google.firebase.auth.FirebaseUser

interface IAuthRepository
{
    val user: FirebaseUser?

    suspend fun logIn(email : String, password : String) : Resource<FirebaseUser>
    suspend fun register(email : String, password: String, fullName : String, phoneNumber : String, profileImg : Uri) : Resource<FirebaseUser>
    fun logOut()

    suspend fun getUser(): Resource<User>
    suspend fun getAllUsers(): Resource<List<User>>
    suspend fun contactOwner(context: Context, userId: String)
}