package com.majid.androidassignment.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
) : LocationClient {

    /**
     * Get location updates as a Flow of Location objects.
     * Throws LocationClient.LocationException if location permission is missing or GPS is disabled.
     */
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> = callbackFlow {
        // Check for location permissions
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw LocationClient.LocationException("Missing location permission")
        }

        // Check if GPS is enabled
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            throw LocationClient.LocationException("GPS is disabled")
        }

        // Create location request
        val request = LocationRequest.create()
            .setInterval(interval)
            .setFastestInterval(interval)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        // Define location callback
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                // Send the latest location to the Flow
                result.lastLocation?.let { location ->
                    launch { send(location) }
                }
            }
        }

        // Request location updates
        client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

        // Remove location updates when the Flow is closed
        awaitClose {
            client.removeLocationUpdates(locationCallback)
        }
    }
}
