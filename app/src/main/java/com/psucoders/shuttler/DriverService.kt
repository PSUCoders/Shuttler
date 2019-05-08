package com.psucoders.shuttler

import android.content.Intent
import androidx.core.app.NotificationCompat
import com.psucoders.shuttler.App.Companion.CHANNEL_ID
import android.location.Location
import android.util.Log
 import android.app.*
import android.content.pm.PackageManager
 import android.os.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint


class DriverService : Service() {

    private lateinit var geofencingClient: GeofencingClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mCurrentLocation: Location
    private lateinit var db: FirebaseFirestore
    private lateinit var driver: String

    override fun onCreate() {
        super.onCreate()
        db = FirebaseFirestore.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        buildLocationRequest()
        buildLocationCallback()
        geofencingClient = LocationServices.getGeofencingClient(this)
        // get user id here
        driver = "cdKOppgDPxGjj0roFfUG"
        db.collection("drivers").document(driver).update("active", true)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val message = "Tracking Shuttle location"

        val notificationIntent = Intent(this, DriverActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Driver Service")
                .setContentText(message)
                .setSmallIcon(R.drawable.shuttler_logo)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
        if (ContextCompat.checkSelfPermission
                (applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient
                    .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        db.collection("drivers").document("cdKOppgDPxGjj0roFfUG").update("active", false)
        Toast.makeText(applicationContext, "destroyed", Toast.LENGTH_LONG).show()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun buildLocationCallback() {
        Log.d("BUILD LOCATION CALLBACK", "LOCATION CALLED")
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                mCurrentLocation = locationResult!!.lastLocation
                val geoPoint = GeoPoint(mCurrentLocation.latitude, mCurrentLocation.longitude)
                db.collection("drivers").document(driver).update("location", geoPoint)
                Toast.makeText(applicationContext, "LATITUDE: ${mCurrentLocation.latitude}  LONGITUDE: ${mCurrentLocation.longitude}", Toast.LENGTH_LONG).show()
            }
        }
    }
}

