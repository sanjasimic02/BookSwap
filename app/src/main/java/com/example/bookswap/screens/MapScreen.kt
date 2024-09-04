package com.example.bookswap.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TableRows
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.example.bookswap.R
import com.example.bookswap.models.Book
import com.example.bookswap.navigation.Routes
import com.example.bookswap.repositories.Resource
import com.example.bookswap.screens.appComponents.bitmapDescriptorFromVector2
import com.example.bookswap.screens.bookScreens.AddBookScreen
import com.example.bookswap.screens.bookScreens.FilterDialog
import com.example.bookswap.usrlocation.LocationService
import com.example.bookswap.viewModel.BookViewModel
import com.example.bookswap.viewModel.UserAuthViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.GeoPoint
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

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

    //sa prez, za GoogleMap composable
    val uiSettings by remember { mutableStateOf(MapUiSettings()) }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    val context = LocalContext.current
    val currentUserId by remember { mutableStateOf(viewModel.getCurrentUserId()) }

    val bookCollection = bookViewModel.books.collectAsState()
    val booksList = remember { mutableListOf<Book>() }

    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val isTrackingServiceEnabled = sharedPreferences.getBoolean("tracking_location", true)
    val lastLatitude = sharedPreferences.getString("last_latitude", null)?.toDoubleOrNull()
    val lastLongitude = sharedPreferences.getString("last_longitude", null)?.toDoubleOrNull()

    val isDialogOpen = remember { mutableStateOf(false) }
    val filters = remember { mutableStateOf(mapOf<String, String>()) }
    val filtersApplied = remember { mutableStateOf(false) }
    val filteredBooksList = remember { mutableStateListOf<Book>() }
    val radius = filters.value["radius"]?.toFloatOrNull() ?: 10f

    val isSearchBarVisible = remember { mutableStateOf(false) }
    val searchApplied = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    val showNoResultsDialog = remember { mutableStateOf(false) }


    //znaci samo u slucaju da postoji samo jedna takva pronadjena, fokusiraj tamo
    val selectedBook = filteredBooksList.firstOrNull()
    LaunchedEffect(filteredBooksList.size) {
        if(filteredBooksList.size == 1 ) {
            selectedBook?.let { book ->
                if (book.swapStatus == "available" && book.userId != currentUserId) {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                        LatLng(book.location.latitude, book.location.longitude),
                        17f // Zoom nivo
                    )
                }
            }
        }
        else if((filtersApplied.value || searchApplied.value) && filteredBooksList.size == 0)
        {
            showNoResultsDialog.value = true
        }
    }

    //ne obuhvata podrucje kako hocu
//    LaunchedEffect(radius) {
//        // Kada je primenjen radius, postavi kameru da prikazuje celo područje
//        if(filteredBooksList.size > 1 ) {
//            val centerLatLng = myLocation.value
//            val radiusInMeters = radius * 1000
//            val boundsBuilder = LatLngBounds.Builder()
//            boundsBuilder.include(centerLatLng!!)
//            // Izračunajte ivice granica na osnovu radiusa
//            val northEast = LatLng(
//                centerLatLng.latitude + (radiusInMeters / 111320), // 111320 metara po stepenu latituda
//                centerLatLng.longitude + (radiusInMeters / (111320 * Math.cos(Math.toRadians(centerLatLng.latitude))))
//            )
//
//            val southWest = LatLng(
//                centerLatLng.latitude - (radiusInMeters / 111320),
//                centerLatLng.longitude - (radiusInMeters / (111320 * Math.cos(Math.toRadians(centerLatLng.latitude))))
//            )
//
//            boundsBuilder.include(northEast)
//            boundsBuilder.include(southWest)
//
//            // Kreirajte LatLngBounds objekat i postavite kameru
//            val bounds = boundsBuilder.build()
//            cameraPositionState.position = CameraPosition.fromLatLngZoom(
//                bounds.center,
//                17f // Prilagodite zoom nivo da odgovara radiusu
//            )
//        }
//    }

    if (!isTrackingServiceEnabled && lastLatitude != null && lastLongitude != null) {
        val lastLocation = LatLng(lastLatitude, lastLongitude)
        cameraPositionState.position = CameraPosition.fromLatLngZoom(lastLocation, 17f)
    }

    val showSheet = remember { mutableStateOf(true) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    fun calculateDistance(start: MutableState<LatLng?>, end: GeoPoint): Float {
        val startLatLng = start.value ?: return Float.MAX_VALUE
        val endLatLng = LatLng(end.latitude, end.longitude)
        val earthRadius = 6371 // Earth radius km

        //prevodjenje koordinata u radijane
        val lat1 = Math.toRadians(startLatLng.latitude)
        val lon1 = Math.toRadians(startLatLng.longitude)
        val lat2 = Math.toRadians(endLatLng.latitude)
        val lon2 = Math.toRadians(endLatLng.longitude)

        //razlika u koordinatama
        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        //Haversine formula
        val a = sin(dLat / 2).pow(2) +
                cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return (earthRadius * c).toFloat() //km
    }

    LaunchedEffect(bookCollection.value, filtersApplied.value, searchQuery.value, searchApplied.value, filters.value) {
        Log.d("TableScreen", "LaunchedEffect triggered")
        bookCollection.value.let {
            when (it) {
                is Resource.Success -> {
                    booksList.clear()
                    booksList.addAll(it.result)
                    if(filtersApplied.value && searchApplied.value)
                    {
                        filteredBooksList.clear()
                        filteredBooksList.addAll(
                            it.result.filter { book ->
                                book.userId != currentUserId &&
                                book.swapStatus == "available" && book.title.contains(searchQuery.value, ignoreCase = true) && //case insensitive
                                        ((filters.value["author"]?.let { book.author.contains(it, ignoreCase = true) } ?: true) &&
                                        (filters.value["genre"]?.let { book.genre.contains(it, ignoreCase = true) } ?: true) &&
                                                (filters.value["language"]?.let { book.language.contains(it, ignoreCase = true) } ?: true)) &&
                                        (calculateDistance(myLocation, book.location) <= radius) // Filter by radius
                            }
                        )

                    }
                    else if (filtersApplied.value) {
                        filteredBooksList.clear()
                        filteredBooksList.addAll(
                            it.result.filter { book ->
                                book.userId != currentUserId && book.swapStatus == "available" &&
                                //(book.title.contains(searchQuery.value, ignoreCase = true)) || ( //umesto da mi title bude u filters
                                (filters.value["author"]?.let { book.author.contains(it, ignoreCase = true) } ?: true) &&
                                        (filters.value["genre"]?.let { book.genre.contains(it, ignoreCase = true) } ?: true) &&
                                        (filters.value["language"]?.let { book.language.contains(it, ignoreCase = true) } ?: true) &&
                                        (calculateDistance(myLocation, book.location) <= radius)// Filter by radius
                            }
                        )

                    }
                    else if(searchApplied.value)
                    {
                        filteredBooksList.clear()
                        filteredBooksList.addAll(
                            it.result.filter { book ->
                                book.userId != currentUserId &&
                                        book.swapStatus == "available" &&
                                        book.title.contains(searchQuery.value, ignoreCase = true)
                            }
                        )
                    }
                    else {
                        filteredBooksList.clear()
                        filteredBooksList.addAll(it.result) //regularna lista svih knjiga iz baze
                    }
                    Log.d("MapScreen", "Filtered books list: ${filteredBooksList.toList()}")
                    Log.d("MapScreen", "Number of books: ${filteredBooksList.size}")
                }
                is Resource.Failure -> TODO()
                Resource.Loading -> TODO()
            }
        }
    }

    LaunchedEffect(filteredBooksList) {
        Log.d("MapScreen", "Filtered books list updated: $filteredBooksList")
    }


    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

                if (intent?.action == LocationService.ACTION_LOCATION_UPDATE) { //osluskuje promene
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
                location = myLocation, //na korisnikovu trenutnu lokaciju
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
                Row( modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
//                    Text(
//                        text = "bookSwap",
//                        style = TextStyle(
//                            fontSize = 18.sp,
//                            fontStyle = FontStyle.Italic,
//                            color = Color(0xFFEDC9AF)
//                        )
//                    )
                    Icon(
                        imageVector = Icons.Default.TableRows,
                        contentDescription = "Table View",
                        tint = Color(0xFFEDC9AF),
                        modifier = Modifier
                            .clickable {
                                navController.navigate(Routes.tableScreen)
                            }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFEDC9AF),
                            modifier = Modifier
                                .clickable {
                                    isSearchBarVisible.value = true
                                }
                        )
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

                        Spacer(modifier = Modifier.width(2.dp))

                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color(0xFFEDC9AF),
                            modifier = Modifier
                                .clickable {
                                    searchApplied.value = false
                                    filtersApplied.value = false
                                    isSearchBarVisible.value = false
                                },
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                navController.navigate("${Routes.userScreen1}/${currentUserId}")
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

            if (isSearchBarVisible.value) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        value = searchQuery.value,
                        onValueChange = { newValue ->
                            searchQuery.value = newValue
                        },
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Search book by title...",
                                style = TextStyle(
                                    color = Color.Gray
                                )
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "",
                                tint = Color(0xFF6D4C41)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                        ),
                        visualTransformation = VisualTransformation.None,
                        keyboardOptions = KeyboardOptions.Default
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            searchApplied.value = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color(0xFF6D4C41)
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        Text(text = "Apply")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color(0xFF6D4C41),
                        modifier = Modifier
                            .clickable {
                                searchApplied.value = false
                                filtersApplied.value = false
                                isSearchBarVisible.value = false
                            },
                    )
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
                    modifier = Modifier.fillMaxSize(),
                    properties = properties,
                    uiSettings = uiSettings
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
                        //val bookLocation = LatLng(book.location.latitude, book.location.longitude)
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
                onApplyFilters = { newFilters -> //callback fja
                    filters.value = newFilters
                    Log.d("MapScreen", "Filters: ${filters.value}")
                    filtersApplied.value = true
                    Log.d("MapScreen", "Filters Applied: ${filtersApplied.value}")
                },
                true
            )

            // Dijalog za prikaz poruke
            if (showNoResultsDialog.value) {
                AlertDialog(
                    onDismissRequest = {
                        showNoResultsDialog.value = false
                    },
                    title = {
                        Text(text = "No results!",
                            color = Color(0xFF6D4C41))
                    },
                    text = {
                        Text(text = "There are no books that match your search or filter criteria.",
                            color = Color(0xFF6D4C41))
                    },
                    confirmButton = {
                        Button(onClick = { showNoResultsDialog.value = false },
                            colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF6D4C41),
                                    contentColor = Color(0xFFEDC9AF)
                        )) {
                            Text("OK", color = Color(0xFFEDC9AF))
                        }
                    }
                )
            }
        }
        }
    }










