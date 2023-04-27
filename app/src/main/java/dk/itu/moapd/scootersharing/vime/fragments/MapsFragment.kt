package dk.itu.moapd.scootersharing.vime.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
import dk.itu.moapd.scootersharing.vime.data.Scooter
import dk.itu.moapd.scootersharing.vime.livedata.ScootersLiveData
import dk.itu.moapd.scootersharing.vime.services.LocationUpdatesService

class MapsFragment : Fragment() {

    companion object {
        //        private val TAG = MapsFragment::class.qualifiedName
        private const val DEFAULT_ZOOM = 15
    }

    private lateinit var locationUpdatesService: LocationUpdatesService
    private var serviceBound = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as LocationUpdatesService.LocalBinder
            locationUpdatesService = binder.getService()
            serviceBound = true

            locationUpdatesService.subscribeToLocationUpdates(
                ::addUserMarker,
                ::updateUserPosAndAddr
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            locationUpdatesService.unsubscribeToLocationUpdates()
        }
    }

    private var scootersLiveData: ScootersLiveData? = null
    private var observer: Observer<Map<String, Scooter>>? = null

    private lateinit var address: String
    private var userMarker: Marker? = null


    private val scooterMarkers: MutableMap<String, Marker> =
        emptyMap<String, Marker>().toMutableMap()

    private var map: GoogleMap? = null

    private val database =
        Firebase.database("https://scooter-sharing-6a9a7-default-rtdb.europe-west1.firebasedatabase.app/").reference

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        scootersLiveData = ScootersLiveData(database)
        observer = Observer { idsToScooters ->
            val oldKeys = scooterMarkers.keys
            val newKeys = idsToScooters.keys

            val removedKeys = oldKeys.toMutableSet().apply { removeAll(newKeys) }

            if (removedKeys.size > 0) {
                removedKeys.forEach { id ->
                    scooterMarkers[id]?.remove()
                    scooterMarkers.remove(id)
                }
            }

            idsToScooters.forEach { (id, scooter) ->
                var marker = scooterMarkers[id]
                if (marker != null) {
                    if (marker.position.latitude != scooter.locationLat ||
                        marker.position.longitude != scooter.locationLon
                    )
                        marker.position = LatLng(scooter.locationLat, scooter.locationLon)
                } else {
                    marker = addScooterMarker(scooter)
                    if (marker != null)
                        scooterMarkers[id] = marker
                }
            }
        }
        scootersLiveData!!.observe(viewLifecycleOwner, observer!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requestUserPermissions()

        Intent(requireContext(), LocationUpdatesService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unbindService(connection)
        serviceBound = false
        observer?.let { scootersLiveData?.removeObserver(it) }
    }

    private fun requestUserPermissions() {
        val permissions: ArrayList<String> = ArrayList()

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        val permissionsToRequest = permissionsToRequest(permissions)

        if (permissionsToRequest.size > 0) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { perms ->
                val allGranted = perms.all { it.value }
                if (allGranted) {
                    locationUpdatesService.subscribeToLocationUpdates(
                        ::addUserMarker,
                        ::updateUserPosAndAddr
                    )
                }
            }
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private fun permissionsToRequest(permissions: ArrayList<String>): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()

        for (permission in permissions)
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                result.add(permission)

        return result
    }

    private fun addUserMarker(location: Location) {
        val markerPos = LatLng(location.latitude, location.longitude)
        // Should be updated to other than marker, shows user location
        userMarker = map?.addMarker(MarkerOptions().position(markerPos).title("Me :)"))
        moveCamera(location)
    }

    private fun moveCamera(location: Location) {
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    location.latitude,
                    location.longitude
                ), DEFAULT_ZOOM.toFloat()
            )
        )
    }

    private fun updateUserPosAndAddr(location: Location, addr: String) {
        userMarker?.position = LatLng(location.latitude, location.longitude)
        address = addr
    }

    private fun addScooterMarker(scooter: Scooter): Marker? {
        return map?.addMarker(
            MarkerOptions().position(
                LatLng(
                    scooter.locationLat,
                    scooter.locationLon
                )
            ).title("Scooter: ${scooter.name}")
        )
    }
}