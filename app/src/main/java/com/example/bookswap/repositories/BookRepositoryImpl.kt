package com.example.bookswap.repositories

import android.net.Uri
import com.example.bookswap.models.Book
import com.example.bookswap.services.DbService
import com.example.bookswap.services.StorageService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class BookRepositoryImpl : BookRepository{
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val storageInstance = FirebaseStorage.getInstance()

    private val databaseService = DbService(firestoreInstance)
    private val storageService = StorageService(storageInstance)

    override suspend fun saveBook(
        location: LatLng,
        title: String,
        author: String,
        description: String,
        genre: String,
        language: String,
        bookImages: List<Uri>
    ): Resource<String> {
        return try{
            val currentUser = firebaseAuth.currentUser
            if(currentUser!=null){
                val bookImagesUrls = storageService.uploadBookImages(bookImages)
                val geoLocation = GeoPoint(
                    location.latitude,
                    location.longitude
                )
                //val coverImageUrl = storageService.uploadBookCoverImage(coverImage)

                val newBook = Book(
                    userId = currentUser.uid,
                    location = geoLocation,
                    title = title,
                    author = author,
                    description = description,
                    genre = genre,
                    language = language,
                    bookImages = bookImagesUrls,
                    swapStatus = "available"
                    //coverImage = coverImageUrl
                )
               databaseService.saveBook(newBook)
                //ovde mogu da dodam poene nakon sto postavi knjigu??
            }
            Resource.Success("Uspesno su sačuvani svi podaci o knjizi")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getAllBooks(): Resource<List<Book>> {
        return try{
            val snapshot = firestoreInstance.collection("books").get().await()
            val beaches = snapshot.toObjects(Book::class.java)
            Resource.Success(beaches)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUsersBooks(uid: String): Resource<List<Book>> {
        return try {
            val snapshot = firestoreInstance.collection("books")
                .whereEqualTo("userId", uid)
                .get()
                .await()
            val beaches = snapshot.toObjects(Book::class.java)
            Resource.Success(beaches)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}