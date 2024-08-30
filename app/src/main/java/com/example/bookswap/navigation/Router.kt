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
import com.example.bookswap.screens.BookOwnerScreen
import com.example.bookswap.screens.LeaderboardScreen
import com.example.bookswap.screens.LoginScreen
import com.example.bookswap.screens.MapScreen
import com.example.bookswap.screens.RegistrationScreen
import com.example.bookswap.screens.ServiceSettings
import com.example.bookswap.screens.StartScreen
import com.example.bookswap.screens.TableScreen
import com.example.bookswap.screens.UserProfileScreen
import com.example.bookswap.screens.UserProfileScreen1
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

    //val currentUserId = viewModel.getCurrentUserId()

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
        composable(Routes.userScreen1 + "/{userId}") {backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            UserProfileScreen1(
                userId = userId,
                navController = navController,
                bookViewModel = bookViewModel,
                viewModel = viewModel
            )
        }

        composable(Routes.mapScreen) {
            MapScreen(viewModel = viewModel, bookViewModel = bookViewModel, navController = navController,
                cameraPositionState = rememberCameraPositionState(),
                myLocation = remember { mutableStateOf(null) })
        }
        composable(Routes.tableScreen) {
            TableScreen(userViewModel = viewModel, bookViewModel = bookViewModel, navController = navController)
        }
        composable(Routes.serviceSettings){
            ServiceSettings(navController = navController)
        }
        composable(Routes.leaderboardScreen){
            LeaderboardScreen(navController = navController, userViewModel = viewModel, bookViewModel = bookViewModel)
        }
        composable(Routes.bookOwnerScreen + "/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            BookOwnerScreen(
                userId = userId,
                navController = navController,
                bookViewModel = bookViewModel,
                userViewModel = viewModel
            )
        }

        composable(
            "bookDetails/{bookId}",
            arguments = listOf(
                navArgument("bookId") { type = NavType.StringType },
                //navArgument("currentUser") { type = NavType.StringType },
                    )
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")
            val booksResource = bookViewModel.books.collectAsState().value

            //val userDataJson = backStackEntry.arguments?.getString("userData")
            //val userData = Gson().fromJson(userDataJson, User::class.java)
            //val currentUserId = FirebaseAuth.getInstance().currentUser?.uid == userData.id

            when (booksResource) {
                is Resource.Success -> {
                    val book = booksResource.result.find { it.id == bookId }
                    book?.let {
                        FirebaseAuth.getInstance().currentUser?.uid?.let { it1 ->
                            BookDetailsScreen(book = it,
                                currentUserId = it1,
                                onBack = { navController.popBackStack() },
                                //mozda treba da prosledim samo neko parce??
                                userAuthViewModel = viewModel,
                                bookViewModel = bookViewModel,
                                navController = navController
                            )
                        }
                            }
                        }

                is Resource.Failure -> TODO()
                Resource.Loading -> TODO()
            }
                }
            }
        }


