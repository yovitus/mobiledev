package dk.itu.moapd.scootersharing.vime.fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.adapters.CustomAdapter
import dk.itu.moapd.scootersharing.vime.data.Ride
import dk.itu.moapd.scootersharing.vime.databinding.FragmentMainBinding
import dk.itu.moapd.scootersharing.vime.singletons.FirebaseManager
import dk.itu.moapd.scootersharing.vime.utils.getRequestUserPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val firebaseManager = FirebaseManager.getInstance()

    private lateinit var adapter: CustomAdapter

    private var requestCameraPermission: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val query = firebaseManager.getRidesQuery()
        val options = FirebaseRecyclerOptions.Builder<Ride>()
            .setQuery(query, Ride::class.java)
            .setLifecycleOwner(this)
            .build()
        adapter = CustomAdapter(options)

        val permissions = arrayOf(Manifest.permission.CAMERA)
        val onGranted: () -> Unit = {
            findNavController().navigate(
                R.id.action_home_to_qrScannerFragment
            )
        }
        requestCameraPermission = getRequestUserPermissions(permissions, onGranted)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter

            startRideButton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    if (firebaseManager.getCard() != null) {
                        requestCameraPermission!!()
                    } else
                        Toast.makeText(
                            context,
                            "Please add card under profile before starting ride",
                            Toast.LENGTH_SHORT
                        ).show()
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}