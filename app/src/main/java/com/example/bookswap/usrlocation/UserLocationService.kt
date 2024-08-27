package com.example.bookswap.usrlocation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.bookswap.MainActivity
import com.example.bookswap.R
import com.example.bookswap.repositories.AuthRepository
import com.example.bookswap.repositories.Resource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private val booksWithoutDuplicates = mutableSetOf<String>()

    private lateinit var sharedPreferences: SharedPreferences//dodala

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE) //dodala
        createNotificationChannel() //mora pre kreiranja obavestenja
        locationClient = LocationClientImpl(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LocationService", "Service started with action: ${intent?.action}")

        when(intent?.action){
            ACTION_START -> {
                Log.d("LocationService", "Service started")
                val notification = createNotification()
                startForeground(NOTIFICATION_ID, notification)
                start()
            }
            ACTION_STOP -> {
                Log.d("LocationService", "Service stopped")
                stop()
            }
            ACTION_FIND_NEARBY -> {
                Log.d("NearbyService", "Service started")
                val notification = createNotification()
                startForeground(NOTIFICATION_ID, notification)
                start(bookIsNearby = true)
            }
        }
        return START_NOT_STICKY
    }

    private fun start(
        bookIsNearby: Boolean = false
    ) {
        locationClient.getLocationUpdates(1000L) //pokrece da dobija azuriranja lokacije svakih 1000ms tj 3s
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                //Log.d("Lokacija", "${location.latitude} ${location.longitude}")

                // Save the last known location in SharedPreferences
                sharedPreferences.edit()
                    .putString("last_latitude", location.latitude.toString())
                    .putString("last_longitude", location.longitude.toString())
                    .apply()

                val intent = Intent(ACTION_LOCATION_UPDATE).apply { //za emitovanje azurirane lokacije u aplikaciji se koristi Intent
                    putExtra(EXTRA_LOCATION_LATITUDE, location.latitude)
                    putExtra(EXTRA_LOCATION_LONGITUDE, location.longitude)
                }
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
              if(bookIsNearby){
                   checkProximityToBooks(location.latitude, location.longitude)
              }
            }.launchIn(serviceScope) //pokretanje u okviru korutinske oblasti, sto omogucava da se pracenje lokacije odvija u pozadini dok god je servis aktivan
    }

    private fun stop(){
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {

        // Retrieve the last known location
        val lastLatitude = sharedPreferences.getString("last_latitude", null)?.toDoubleOrNull()
        val lastLongitude = sharedPreferences.getString("last_longitude", null)?.toDoubleOrNull()

        if (lastLatitude != null && lastLongitude != null) {
            val lastLocation = LatLng(lastLatitude, lastLongitude)
            // Do something with the last known location, if necessary
        }

        super.onDestroy()
        Log.d("LocationService", "Service stopped")
        serviceScope.cancel()
    }

    private fun createNotificationChannel() {
        val notificationChannelId = "LOCATION_SERVICE_CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Lokacija",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Obaveštavamo vas da se vaša lokacija prati u pozadini"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): android.app.Notification {
        val notificationChannelId = "LOCATION_SERVICE_CHANNEL"

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Praćenje lokacije")
            .setContentText("Servis praćenja lokacije je pokrenut u pozadini")
            .setSmallIcon(R.drawable.book_notif)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

//    private fun saveLastKnownLocation(location: Location) {
//        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
//        with(sharedPreferences.edit()) {
//            putFloat("last_latitude", location.latitude.toFloat())
//            putFloat("last_longitude", location.longitude.toFloat())
//            apply()
//        }
//    }

    private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 //radius zemlje
        val latDistance = Math.toRadians(lat2 - lat1)
        val lonDistance = Math.toRadians(lon2 - lon1)
        val a = sin(latDistance / 2) * sin(latDistance / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(lonDistance / 2) * sin(lonDistance / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R*c;
        //Log.d("BookProximity", "Distance to book: $distance meters")
        return distance
    }

//    private fun checkProximityToBooks(userLatitude: Double, userLongitude: Double) {
//        val firestore = FirebaseFirestore.getInstance()
//        firestore.collection("books").get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    val geoPoint = document.getGeoPoint("location")
//                    val bookUserId = document.getString("userId")
//                    val swapStatus = document.getString("swapStatus")
//                    if (geoPoint != null && swapStatus == "available") { //nema info o korisniku jos, jer se servis aktivira pre logovanja && bookUserId != FirebaseAuth.getInstance().currentUser!!.uid
//                        val distance = calculateHaversineDistance(userLatitude, userLongitude, geoPoint.latitude, geoPoint.longitude)
//                        if (distance <= 1000 && !booksWithoutDuplicates.contains(document.id) ) { //1km udaljenost
//                            alertBookNearby(document.getString("title") ?: "Book")
//                            booksWithoutDuplicates.add(document.id)
//                            Log.d("NearbyBook", document.toString())
//                        }
//                    }
//                }
//            }
//            .addOnFailureListener { e ->
//                Log.e("LocationService", "Error fetching books", e)
//            }
//    }


    private fun checkProximityToBooks(userLatitude: Double, userLongitude: Double) {
        val firestore = FirebaseFirestore.getInstance()
        val authRepository = AuthRepository()

        serviceScope.launch {
            val userResource = authRepository.getUser()

            if (userResource is Resource.Success) {
                val currentUser = userResource.result

                firestore.collection("books").get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val geoPoint = document.getGeoPoint("location")
                            val bookUserId = document.getString("userId")
                            val swapStatus = document.getString("swapStatus")

                            // Proveri da li je knjiga dostupna i da li pripada nekom drugom korisniku
                            if (geoPoint != null && swapStatus == "available" && (currentUser == null || bookUserId != currentUser.id)) {
                                val distance = calculateHaversineDistance(userLatitude, userLongitude, geoPoint.latitude, geoPoint.longitude)

                                if (distance <= 1000 && !booksWithoutDuplicates.contains(document.id)) {
                                    alertBookNearby(document.getString("title") ?: "Book")
                                    booksWithoutDuplicates.add(document.id)
                                    Log.d("NearbyBook", document.toString())
                                }
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("LocationService", "Error fetching books", e)
                    }
            } else {
                Log.e("LocationService", "Failed to fetch current user: ${(userResource as Resource.Failure).exception.message}")
            }
        }
    }

    private fun alertBookNearby(bookTitle: String) {
        val notificationChannelId = "LOCATION_SERVICE_CHANNEL"

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Book Nearby!")
            .setContentText("You're near the book \"$bookTitle\"!")
            .setSmallIcon(R.drawable.book_notif)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NEARBY_BOOK_NOTIFICATION_ID, notification)
    }


    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_FIND_NEARBY = "ACTION_FIND_NEARBY"
        const val ACTION_LOCATION_UPDATE = "ACTION_LOCATION_UPDATE"
        const val EXTRA_LOCATION_LATITUDE = "EXTRA_LOCATION_LATITUDE"
        const val EXTRA_LOCATION_LONGITUDE = "EXTRA_LOCATION_LONGITUDE"
        private const val NOTIFICATION_ID = 1
        private const val NEARBY_BOOK_NOTIFICATION_ID = 25
        private const val RENT_NOTIFICATION_ID = 4
    }
}