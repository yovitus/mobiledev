package dk.itu.moapd.scootersharing.vime.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.utils.getScooters
import kotlinx.coroutines.*

class MapsFragment : Fragment() {

    private val database =
        Firebase.database("https://scooter-sharing-6a9a7-default-rtdb.europe-west1.firebasedatabase.app/").reference

    @OptIn(DelicateCoroutinesApi::class)
    private val callback = OnMapReadyCallback { googleMap ->
        GlobalScope.launch {
            val scooters = database.getScooters()

            withContext(Dispatchers.Main) {
                scooters.forEach { scooter ->
                    val marker = LatLng(scooter.locationLat, scooter.locationLon)
                    googleMap.addMarker(MarkerOptions().position(marker).title("Scooter"))
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}