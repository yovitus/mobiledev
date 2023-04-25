package dk.itu.moapd.scootersharing.vime.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.utils.getScooters
import kotlinx.coroutines.*
import java.util.*

class MapsFragment : Fragment() {

    companion object {
        private val TAG = MapsFragment::class.qualifiedName
        private const val ALL_PERMISSION_RESULTS = 1011
        private const val DEFAULT_ZOOM = 15
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var address: String
    private var userMarker: Marker? = null

    private var map: GoogleMap? = null

    private val database =
        Firebase.database("https://scooter-sharing-6a9a7-default-rtdb.europe-west1.firebasedatabase.app/").reference

    @OptIn(DelicateCoroutinesApi::class)
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        GlobalScope.launch {
            val scooters = database.getScooters()

            withContext(Dispatchers.Main) {
                scooters.forEach { scooter ->
                    val markerPos = LatLng(scooter.locationLat, scooter.locationLon)
                    // Should be updated with onclick, for modal popup
                    googleMap.addMarker(MarkerOptions().position(markerPos).title("Scooter"))
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        startLocationAware()
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onResume() {
        super.onResume()
        subscribeToLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        unsubscribeToLocationUpdates()
    }

    private fun startLocationAware() {
        requestUserPermissions()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                p0.lastLocation?.let { location ->
                    setAddress(location)
                }
            }
        }
    }

    private fun requestUserPermissions() {
        val permissions: ArrayList<String> = ArrayList()

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        val permissionsToRequest = permissionsToRequest(permissions)

        if (permissionsToRequest.size > 0) {
            requestPermissions(
                permissionsToRequest.toTypedArray(),
                ALL_PERMISSION_RESULTS
            )
        }
    }

    private fun permissionsToRequest(permissions: ArrayList<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()

        for (permission in permissions)
            if (checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                result.add(permission)
        return result
    }

    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED

    private fun subscribeToLocationUpdates() {
        if (checkPermission())
            return

        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 5)
            .build()

        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val lastKnownLocation = task.result
                if (lastKnownLocation != null) {
                    map?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lastKnownLocation.latitude,
                                lastKnownLocation.longitude
                            ), DEFAULT_ZOOM.toFloat()
                        )
                    )
                    val markerPos = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                    // Should be updated to other than marker, shows user location
                    userMarker = map?.addMarker(MarkerOptions().position(markerPos))
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper()
        )
    }

    private fun unsubscribeToLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun setAddress(loc: Location) {
        if (!Geocoder.isPresent())
            return

        val geocoder = Geocoder(requireActivity(), Locale.getDefault())

        if (Build.VERSION.SDK_INT >= 33) {
            val geocodeListener = Geocoder.GeocodeListener { addresses ->
                addresses.firstOrNull()?.toAddressString()?.let {
                    userMarker?.position = LatLng(loc.latitude, loc.longitude)
                    address = it
                }
            }
            geocoder.getFromLocation(loc.latitude, loc.longitude, 1, geocodeListener)
        } else {
            geocoder.getFromLocation(loc.latitude, loc.longitude, 1)?.let { addresses ->
                addresses.firstOrNull()?.toAddressString()?.let {
                    userMarker?.position = LatLng(loc.latitude, loc.longitude)
                    address = it
                }
            }
        }
    }

    private fun Address.toAddressString(): String {
        val address = this

        // Create a `String` with multiple lines.
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            append(address.getAddressLine(0)).append("\n")
            append(address.postalCode).append(" ")
            append(address.locality).append("\n")
            append(address.countryName)
        }

        return stringBuilder.toString()
    }

}