package com.example.bookswap.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bookswap.models.User
import com.example.bookswap.repositories.Resource
import com.example.bookswap.screens.LoginScreen
import com.example.bookswap.screens.MapScreen
import com.example.bookswap.screens.RegistrationScreen
import com.example.bookswap.screens.ServiceSettings
import com.example.bookswap.screens.StartScreen
import com.example.bookswap.screens.UserProfileScreen
import com.example.bookswap.screens.bookScreens.BookDetailsScreen
import com.example.bookswap.viewModel.BookViewModel
import com.example.bookswap.viewModel.UserAuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Router(
    viewModel : UserAuthViewModel,
    bookViewModel : BookViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.startScreen) {
        composable(Routes.registrationScreen) {
            RegistrationScreen(viewModel = viewModel, navController = navController)
        }
        composable(Routes.loginScreen) {
            LoginScreen(viewModel = viewModel, navController = navController)
        }
        composable(Routes.startScreen) {
            StartScreen(viewModel = viewModel, navController = navController)
        }
//        composable(Routes.addBook){
//            AddBookScreen(viewModel = viewModel, navController = navController, bookViewModel = bookViewModel)
//        } //treba da prosledim lokaciju!!!!
        composable(
            route = Routes.userScreen + "/{currentUser}",
            arguments = listOf(navArgument("currentUser")
            {
                type = NavType.StringType
            })
        ) {currBackStack ->
            val currentUserJSON = currBackStack.arguments?.getString("currentUser")
            val currentUser = Gson().fromJson(currentUserJSON, User::class.java)
            val isCorrect = FirebaseAuth.getInstance().currentUser?.uid == currentUser.id
            UserProfileScreen(viewModel = viewModel, navController = navController, bookViewModel = bookViewModel, currentUser = currentUser, isCorrect = isCorrect)
        }
        composable(Routes.mapScreen) {
            MapScreen(viewModel = viewModel, bookViewModel = bookViewModel, navController = navController,
                cameraPositionState = rememberCameraPositionState(),
                myLocation = remember { mutableStateOf(null) })
        }
        composable(Routes.serviceSettings){
            ServiceSettings(navController = navController)
        }

        composable(
            "bookDetails/{bookId}",
            arguments = listOf(navArgument("bookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")
            val booksResource = bookViewModel.books.collectAsState().value

            when (booksResource) {
                is Resource.Success -> {
                    val book = booksResource.result.find { it.id == bookId }
                    book?.let {
                        BookDetailsScreen(book = it, onBack = { navController.popBackStack() })
                    }
                }
                is Resource.Loading -> {
                    // Handle loading state
                }
                is Resource.Failure -> {
                    // Handle failure state
                }
            }
        }

    }
}

