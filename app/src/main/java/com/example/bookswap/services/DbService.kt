package com.example.bookswap.services

import com.example.bookswap.models.Book
import com.example.bookswap.models.User
import com.example.bookswap.repositories.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DbService(
    private val firestore: FirebaseFirestore
) {
    suspend fun saveUserData( id: String, user: User) : Resource<String>
    {
        return try
        {
            firestore.collection("users").document(id).set(user).await()
            Resource.Success("[INFO] User data saved successfully. (User ID: ${id})")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun getUserData( id: String ) : Resource<String>
    {
        return try
        {
            val userDocRef = firestore.collection("users").document(id)
            val userSnapshot = userDocRef.get().await()

            if(userSnapshot.exists())
            {
                val user = userSnapshot.toObject(User::class.java)
                if(user != null) {
                    Resource.Success(user)
                }
                else {
                    Resource.Failure(Exception("[ERROR] User not found! (User ID: ${id})"))
                }
            }
            else {
                Resource.Failure(Exception("[ERROR] User snapshot not found (User ID: ${id})"))
            }

            Resource.Success("[INFO] Successfully retrieved user data. (User ID: ${id})")

        }
        catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun saveBook(
        book: Book
    ): Resource<String>{
        return try{
            firestore.collection("books").add(book).await()
            Resource.Success("Podaci o knjizi su uspesno sacuvani")
        }catch(e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}