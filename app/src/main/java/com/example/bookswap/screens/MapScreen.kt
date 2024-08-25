package com.example.bookswap.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.example.bookswap.R
import com.example.bookswap.models.Book
import com.example.bookswap.repositories.Resource
import com.example.bookswap.screens.appComponents.bitmapDescriptorFromVector2
import com.example.bookswap.screens.bookScreens.AddBookScreen
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

    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val isTrackingServiceEnabled = sharedPreferences.getBoolean("tracking_location", true)
    val lastLatitude = sharedPreferences.getString("last_latitude", null)?.toDoubleOrNull()
    val lastLongitude = sharedPreferences.getString("last_longitude", null)?.toDoubleOrNull()


    if (!isTrackingServiceEnabled && lastLatitude != null && lastLongitude != null) {
        val lastLocation = LatLng(lastLatitude, lastLongitude)
        // Use lastLocation as the position of the map
        cameraPositionState.position = CameraPosition.fromLatLngZoom(lastLocation, 17f)
    }

    // val showDialog = remember { mutableStateOf(false) }

    val showSheet = remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val bookCollection = bookViewModel.books.collectAsState()
    val booksList = remember { mutableListOf<Book>() }



    // Handle book loading states
    bookCollection.value.let {
        when (it) {
            is Resource.Success -> {
                booksList.clear()
                booksList.addAll(it.result)
            }
            is Resource.Loading -> {
                Log.d("MapScreen", "Loading books...")
            }
            is Resource.Failure -> {
                Log.e("MapScreen", "Failed to load books:")
            }
            null -> {
                Log.d("MapScreen", "No books available")
            }
        }
    }


    Log.d("MapScreen", "MapScreen Composable Started")

    // Register BroadcastReceiver to receive location updates
    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                if (intent?.action == LocationService.ACTION_LOCATION_UPDATE) {
                    val latitude = intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LATITUDE, 0.0)
                    val longitude = intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LONGITUDE, 0.0)
                    myLocation.value = LatLng(latitude, longitude)
                }
            }
        }
    }

    // Register the receiver
    DisposableEffect(context) {
        //Log.d("MapScreen", "Registering BroadcastReceiver")

        LocalBroadcastManager.getInstance(context)
            .registerReceiver(receiver, IntentFilter(LocationService.ACTION_LOCATION_UPDATE))
        onDispose {
            //Log.d("MapScreen", "Unregistering BroadcastReceiver")

            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(myLocation.value) {//dodala launched effect
        myLocation.value?.let { location ->
            cameraPositionState.position = CameraPosition.fromLatLngZoom(location, 17f)
        }
    }

    // Control the sheet
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
        Box(modifier = Modifier.fillMaxSize()) {
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
                            //showDialog.value = true
                            true
                        }
                    )
                }

                // Display markers for all books
                booksList.forEach { book ->
                    Log.d("MapScreen", "Book: ${book.title}, Location: ${book.location.latitude}, ${book.location.longitude}")
                    val bookLocation = LatLng(book.location.latitude, book.location.longitude)
                    Marker(
                        state = MarkerState(position = bookLocation),
                        title = book.title,
                        snippet = "By ${book.author}",
                        icon = bitmapDescriptorFromVector2(context, R.drawable.book_marker),
                        onClick = {

                            // Navigate to the book details screen

                            Log.d("MapScreen", "Clicked on book: ${book.title}")
                            //false

                            navController.navigate("bookDetails/${book.id}")
                            true
                        },

                    )
                }

            }
            // Dialog for adding a book
//    if (showDialog.value) {
//        CustomDialog(
//            showDialog = showDialog,
//            onAddBookClick = {
//                Log.d("MapScreen", "Add new book button clicked")
//
//            }
//        )
//
//    }
        }
    }
}





