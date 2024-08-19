package com.example.bookswap.services

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageService( private val storage: FirebaseStorage)
{
    suspend fun uploadUserPfp( uid: String, image: Uri) : String
    {
        return try
        {
            val storageRef = storage.reference.child("profile_pictures/$uid.jpg")
            val uploadTask = storageRef.putFile(image).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            downloadUrl.toString()
        }
        catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    suspend fun uploadBookCoverImage(image: Uri) : String
    {
        return try
        {
            val fileName = "${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child("book_cover_images/$fileName")
            val uploadTask = storageRef.putFile(image).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            downloadUrl.toString()
        }
        catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    suspend fun uploadBookImages(
        images: List<Uri>
    ): List<String>{
        val downloadUrls = mutableListOf<String>()
        for (image in images) {
            try {
                val fileName = "${System.currentTimeMillis()}.jpg"
                val storageRef = storage.reference.child("book_images/$fileName")
                val uploadTask = storageRef.putFile(image).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await()
                downloadUrls.add(downloadUrl.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return downloadUrls
    }
}