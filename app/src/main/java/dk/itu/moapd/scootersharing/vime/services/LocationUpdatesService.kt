package dk.itu.moapd.scootersharing.vime.services

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.*

class LocationUpdatesService : Service() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var updateFunc: (Location, String) -> Unit = { _: Location, _: String -> }

    override fun onBind(intent: Intent?): IBinder {
        startLocationAware()
        return LocalBinder()
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): LocationUpdatesService = this@LocationUpdatesService
    }

    private fun startLocationAware() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                p0.lastLocation?.let { location ->
                    setAddress(location)
                }
            }
        }
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED

    fun subscribeToLocationUpdates(
        initFunc: (Location) -> Unit,
        upFunc: (Location, String) -> Unit
    ) {
        if (checkPermission())
            return

        updateFunc = upFunc

        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5)
            .build()

        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val lastKnownLocation = task.result
                if (lastKnownLocation != null) {
                    initFunc(lastKnownLocation)
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    fun unsubscribeToLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun setAddress(loc: Location) {
        if (!Geocoder.isPresent())
            return

        val geocoder = Geocoder(this, Locale.getDefault())

        if (Build.VERSION.SDK_INT >= 33) {
            val geocodeListener = Geocoder.GeocodeListener { addresses ->
                addresses.firstOrNull()?.toAddressString()?.let { addr ->
                    updateFunc(loc, addr)
                }
            }
            geocoder.getFromLocation(loc.latitude, loc.longitude, 1, geocodeListener)
        } else {
            geocoder.getFromLocation(loc.latitude, loc.longitude, 1)?.let { addresses ->
                addresses.firstOrNull()?.toAddressString()?.let { addr ->
                    updateFunc(loc, addr)
                }
            }
        }
    }

    private fun Address.toAddressString(): String {
        return this.getAddressLine(0).toString()
    }
}