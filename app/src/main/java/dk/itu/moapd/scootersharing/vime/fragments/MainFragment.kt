package dk.itu.moapd.scootersharing.vime.fragments

import android.Manifest
import android.os.Build
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
import dk.itu.moapd.scootersharing.vime.utils.getCard
import dk.itu.moapd.scootersharing.vime.utils.getRequestUserPermissions
import dk.itu.moapd.scootersharing.vime.utils.getRidesQuery
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class MainFragment : Fragment() {
    companion object {
        //        private val TAG = MainFragment::class.qualifiedName
    }

    /*
    * These are viewbindings that allows easy read
     */
    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private lateinit var adapter: CustomAdapter

    private var requestCameraPermission: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val query = getRidesQuery()
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

            // For 'Reporting the Device’s Android Version' challenge
            textViewAndroidversion.text = resources.getString(
                R.string.android_version_text,
                Build.VERSION.SDK_INT
            )

            startRideButton.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    if (getCard() != null) {
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