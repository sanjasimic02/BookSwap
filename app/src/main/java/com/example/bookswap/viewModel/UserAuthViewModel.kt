package com.example.bookswap.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bookswap.models.User
import com.example.bookswap.repositories.AuthRepository
import com.example.bookswap.repositories.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserAuthViewModel() : ViewModel()
{
    val repo = AuthRepository()
    private val _signInFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signInFlow: StateFlow<Resource<FirebaseUser>?> = _signInFlow

    private val _signUpFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val signUpFlow: StateFlow<Resource<FirebaseUser>?> = _signUpFlow

    private val _currentUserFlow = MutableStateFlow<Resource<User>?>(null)
    @SuppressLint("RestrictedApi")
    val currentUserFlow: StateFlow<Resource<User>?> = _currentUserFlow

    private val _allUsers = MutableStateFlow<Resource<List<User>>?>(null)
    @SuppressLint("RestrictedApi")
    val allUsers: StateFlow<Resource<List<User>>?> = _allUsers

    private val _userByIdFlow = MutableStateFlow<Resource<User>?>(null)
    val userByIdFlow: StateFlow<Resource<User>?> = _userByIdFlow

    val currentUser: FirebaseUser?
        get() = repo.user

    fun getUserData() = viewModelScope.launch {
        val result = repo.getUser()
        _currentUserFlow.value = result
    }

    fun getCurrentUserId(): String? {
        val currentUserResource = _currentUserFlow.value
        return if (currentUserResource is Resource.Success) {
            currentUserResource.result.id
        } else {
            null
        }
    }

    fun getUserById(userId: String) = viewModelScope.launch {
        val result = repo.getUserById(userId)
        _userByIdFlow.value = result
    }

    suspend fun contactOwner(context: Context, userId: String) {
        repo.contactOwner(context, userId)
    }

    fun getAllUsersData() = viewModelScope.launch {
        val result = repo.getAllUsers()
        _allUsers.value = result
    }

    init {
        if(repo.user != null){
            _signInFlow.value = Resource.Success(repo.user!!)
        }
    }



    fun logIn(email: String, password: String) = viewModelScope.launch{
        _signInFlow.value = Resource.Loading
        val result = repo.logIn(email, password)
        if (result is Resource.Success) {
            getUserData() // Pozovite getUserData nakon uspešnog logovanja
        }
        _signInFlow.value = result
    }

    fun register(fullName: String, phoneNumber: String, profileImg: Uri, email: String, password: String) = viewModelScope.launch {
        _signUpFlow.value = Resource.Loading
        val result = repo.register(email, password, fullName, phoneNumber, profileImg)
        _signUpFlow.value = result
    }

    fun logOut()
    {
        repo.logOut()
        _signInFlow.value = null
        _signUpFlow.value = null
        _currentUserFlow.value = null
    }
}

class UserAuthViewModelFactory : ViewModelProvider.Factory
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T
    {
        if(modelClass.isAssignableFrom(UserAuthViewModel::class.java))
        {
            return UserAuthViewModel() as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}