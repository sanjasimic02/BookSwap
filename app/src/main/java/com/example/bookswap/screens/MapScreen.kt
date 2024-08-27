package com.example.bookswap.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.example.bookswap.R
import com.example.bookswap.models.Book
import com.example.bookswap.repositories.Resource
import com.example.bookswap.screens.appComponents.bitmapDescriptorFromVector2
import com.example.bookswap.screens.bookScreens.AddBookScreen
import com.example.bookswap.screens.bookScreens.FilterDialog
import com.example.bookswap.usrlocation.LocationService
import com.example.bookswap.viewModel.BookViewModel
import com.example.bookswap.viewModel.UserAuthViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterialApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: UserAuthViewModel,
    bookViewModel : BookViewModel,
    cameraPositionState: CameraPositionState = rememberCameraPositionState(),
    myLocation: MutableState<LatLng?> = remember { mutableStateOf(null) }
) {

    val context = LocalContext.current
    val currentUserId by remember { mutableStateOf(viewModel.getCurrentUserId()) }

    val currUserData = viewModel.currentUserFlow.collectAsState()

    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val isTrackingServiceEnabled = sharedPreferences.getBoolean("tracking_location", true)
    val lastLatitude = sharedPreferences.getString("last_latitude", null)?.toDoubleOrNull()
    val lastLongitude = sharedPreferences.getString("last_longitude", null)?.toDoubleOrNull()


    val isDialogOpen = remember { mutableStateOf(false) }
    val filters = remember { mutableStateOf(mapOf<String, String>()) }
    val filtersApplied = remember { mutableStateOf(false) }
    val filteredBooksList = remember { mutableStateListOf<Book>() }

    // Ako postoji filtrirana knjiga, postavljam poziciju kamere na nju
    val selectedBook = filteredBooksList.firstOrNull()

    LaunchedEffect(selectedBook) {
        selectedBook?.let { book ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(book.location.latitude, book.location.longitude),
                12f // Zoom nivo
            )
        }
    }

    val bookCollection = bookViewModel.books.collectAsState()
    val booksList = remember { mutableListOf<Book>() }

    if (!isTrackingServiceEnabled && lastLatitude != null && lastLongitude != null) {
        val lastLocation = LatLng(lastLatitude, lastLongitude)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(lastLocation, 17f)
    }

    val showSheet = remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)


    LaunchedEffect(bookCollection.value, filtersApplied.value) {
        bookCollection.value.let {
            when (it) {
                is Resource.Success -> {
                    booksList.clear()
                    booksList.addAll(it.result)
                    if (filtersApplied.value) {
                        filteredBooksList.clear()
                        filteredBooksList.addAll(
                            it.result.filter { book ->
                                (filters.value["title"]?.let { book.title.contains(it, ignoreCase = true) } ?: true) &&
                                        (filters.value["author"]?.let { book.author.contains(it, ignoreCase = true) } ?: true) &&
                                        (filters.value["genre"]?.let { book.genre.contains(it, ignoreCase = true) } ?: true) &&
                                        (filters.value["language"]?.let { book.language.contains(it, ignoreCase = true) } ?: true)
                            }
                        )
                    } else {
                        filteredBooksList.clear()
                        filteredBooksList.addAll(it.result)
                    }
                    Log.d("MapScreen", "Filtered books list: ${filteredBooksList.toList()}")
                }
                is Resource.Failure -> TODO()
                Resource.Loading -> TODO()
            }
        }
    }

    LaunchedEffect(filteredBooksList) {
        Log.d("MapScreen", "Filtered books list updated: $filteredBooksList")
    }


//    // Handle book loading states
//    LaunchedEffect(bookCollection) { //dodato da se na svaku promenu ovo desava
//        bookCollection.value.let {
//            when (it) {
//                is Resource.Success -> {
//                    booksList.clear()
//                    booksList.addAll(it.result)
//                }
//
//                is Resource.Loading -> {
//                    Log.d("MapScreen", "Loading books...")
//                }
//
//                is Resource.Failure -> {
//                    Log.e("MapScreen", "Failed to load books:")
//                }
//
//                null -> {
//                    Log.d("MapScreen", "No books available")
//                }
//            }
//        }
//    }


    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                if (intent?.action == LocationService.ACTION_LOCATION_UPDATE) {
                    val latitude =
                        intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LATITUDE, 0.0)
                    val longitude =
                        intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LONGITUDE, 0.0)
                    myLocation.value = LatLng(latitude, longitude)
                }
            }
        }
    }

    DisposableEffect(context) {
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(receiver, IntentFilter(LocationService.ACTION_LOCATION_UPDATE))
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(myLocation.value) {
        myLocation.value?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 17f)
        }
    }

    LaunchedEffect(showSheet.value) {
        if (showSheet.value) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            AddBookScreen(
                bookViewModel = bookViewModel,
                navController = navController,
                location = myLocation,
                onDismiss = { showSheet.value = false } // skriva sheet
            )
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFAF3E0))
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color(0xFF6D4C41))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "bookSwap",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFFEDC9AF)
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row {
                        Button(
                            onClick = {
                                isDialogOpen.value = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6D4C41),
                                contentColor = Color(0xFF3C0B1A)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Filters",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEDC9AF),
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Button(
                            onClick = {
                                      navController.navigateUp()
//                                Log.d("MapScreen", "Current User Data: $currUserData")
//                                currUserData.value?.let { user ->
//                                    Log.d("MapScreen", "User: $user")
//                                    val currUserJSON = Gson().toJson(user)
//                                    val encodedUsr = URLEncoder.encode(currUserJSON, StandardCharsets.UTF_8.toString())
//                                    Log.d("[DEBUG]", "Navigating to: ${Routes.userScreen + "/$encodedUsr"}")
//                                    navController.navigate(Routes.userScreen + "/$encodedUsr") {
////                                        popUpTo(Routes.mapScreen) {
////                                            inclusive = false
////                                        }
//                                    }
                            //}
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF6D4C41),
                                contentColor = Color(0xFF3C0B1A)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                text = "User Profile",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEDC9AF),
                                )
                            )
                        }
                    }
                }
            }
            // Map
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    myLocation.value?.let { location ->
                        Marker(
                            state = MarkerState(position = location),
                            title = "You are here",
                            snippet = "Current location of the user",
                            onClick = {
                                showSheet.value = true
                                true
                            }
                        )
                    }

                    filteredBooksList.forEach { book ->
                        val bookLocation = LatLng(book.location.latitude, book.location.longitude)
                        val markerIcon = if (book.userId == currentUserId) {
                            bitmapDescriptorFromVector2(context, R.drawable.book_marker)
                        } else {
                            bitmapDescriptorFromVector2(context, R.drawable.knjiga)
                        }

                        if (book.swapStatus == "available") {
                            Marker(
                                //position = LatLng(book.location.latitude, book.location.longitude),
                                state = MarkerState(position = LatLng(book.location.latitude, book.location.longitude)),
                                title = book.title,
                                snippet = "By ${book.author}",
                                icon = markerIcon,
                                onClick = {
                                    Log.d("MapScreen", "Clicked on book: ${book.title}")
                                    navController.navigate("bookDetails/${book.id}")
                                    true
                                }
                            )
                        }
                    }
                }
            }

            FilterDialog(
                isDialogOpen = isDialogOpen,
                onDismissRequest = { isDialogOpen.value = false },
                onApplyFilters = { newFilters ->
                    filters.value = newFilters
                    Log.d("MapScreen", "Filters: ${filters.value}")
                    filtersApplied.value = true
                    Log.d("MapScreen", "Filters Applied: ${filtersApplied.value}")
                }
            )
        }
        }
    }









