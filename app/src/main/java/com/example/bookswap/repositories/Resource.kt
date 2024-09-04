package com.example.bookswap.repositories

sealed class Resource <out R>{
    data class Success<out R>(val result: R): Resource<R>()
    data class Failure(val exception: Exception) : Resource<Nothing>()
    data object Loading: Resource<Nothing>() //ne sadrzi nikakve pod, vec samo oznacava da je operacija u toku
}