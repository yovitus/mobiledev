package dk.itu.moapd.scootersharing.vime.fragments

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.data.Scooter
import dk.itu.moapd.scootersharing.vime.livedata.ScootersLiveData
import dk.itu.moapd.scootersharing.vime.services.LocationUpdatesService
import dk.itu.moapd.scootersharing.vime.utils.getBitmapFromVectorDrawable

class MapsFragment : Fragment() {

    companion object {
        //        private val TAG = MapsFragment::class.qualifiedName
        private const val DEFAULT_ZOOM = 15
    }

    private var locationUpdatesService: LocationUpdatesService? = null
    private var serviceBound = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            val binder = service as LocationUpdatesService.LocalBinder
            locationUpdatesService = binder.getService()
            serviceBound = true

            locationUpdatesService?.subscribeToLocationUpdates(
                ::addUserMarker,
                ::updateUserPosAndAddr
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
            locationUpdatesService?.unsubscribeToLocationUpdates()
        }
    }

    private var idsToScooters: Map<String, Scooter>? = null
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

        map?.setOnMarkerClickListener { marker ->
            moveCamera(marker.position)
            if (marker != userMarker) {
                val args: ScooterDialogFragmentArgs by navArgs()
                val scooter = idsToScooters?.get(marker.tag)
                if (scooter != null) {

                    val bundle = bundleOf(
                        "scooterTitle" to scooter.name,
                        "scooterAddress" to scooter.address,
                        "scooterImageUrl" to scooter.imageUrl
                    )
                    findNavController().navigate(
                        R.id.action_maps_to_scooterDialog,
                        bundle
                    )
                }
            }
            return@setOnMarkerClickListener true
        }

        scootersLiveData = ScootersLiveData(database)
        observer = Observer { idsToScooters ->
            val oldKeys = scooterMarkers.keys
            val newKeys = idsToScooters.keys

            val removedKeys = oldKeys.toMutableSet().apply { removeAll(newKeys) }

            if (removedKeys.size > 0) {
                removedKeys.forEach { id ->
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
                    marker?.tag = id
                    if (marker != null)
                        scooterMarkers[id] = marker
                }
            }
            this.idsToScooters = idsToScooters
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
                    locationUpdatesService?.subscribeToLocationUpdates(
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
        val bitmap = requireContext().getBitmapFromVectorDrawable(R.drawable.baseline_accessibility_new_24)
        userMarker = map?.addMarker(MarkerOptions()
            .position(markerPos)
            .icon(BitmapDescriptorFactory.fromBitmap(bitmap)))
        moveCamera(LatLng(location.latitude, location.longitude))
    }

    private fun moveCamera(latLng: LatLng) {
        map?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng, DEFAULT_ZOOM.toFloat()
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
            )
                .title("Scooter: ${scooter.name}")
        )
    }
}