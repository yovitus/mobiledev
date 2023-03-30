package dk.itu.moapd.scootersharing.vime.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.RidesDB
import dk.itu.moapd.scootersharing.vime.adapters.CustomAdapter
import dk.itu.moapd.scootersharing.vime.data.Scooter
import dk.itu.moapd.scootersharing.vime.utils.createDialog
import dk.itu.moapd.scootersharing.vime.databinding.FragmentMainBinding

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class MainFragment : Fragment() {
    companion object {
        private val TAG = MainFragment::class.qualifiedName
        private lateinit var adapter: CustomAdapter
        private lateinit var auth: FirebaseAuth
        private lateinit var database: DatabaseReference
    }
    /*
    * These are viewbindings that allows easy read
     */
    private var _binding: FragmentMainBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = Firebase.database("https://scooter-sharing-6a9a7-default-rtdb.europe-west1.firebasedatabase.app/").reference
        auth.currentUser?.let{
            val query = database.child("users")
                                .child(it.uid)
                                .child("rides")
            val options = FirebaseRecyclerOptions.Builder<Scooter>()
                .setQuery(query, Scooter::class.java)
                .setLifecycleOwner(this)
                .build()
            adapter = CustomAdapter(options)
        }
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

            // Buttons.
            startRideButton.setOnClickListener {
                findNavController().navigate(
                    R.id.show_startRideFragment
                )
            }

            updateRideButton.setOnClickListener {
                if (database.) {
                    Snackbar.make(
                        root,
                        "Rides list is empty, start a ride before updating.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    findNavController().navigate(
                        R.id.show_updateRideFragment
                    )
                }
            }

            showRidelistButton.setOnClickListener {
                if (recyclerView.visibility == View.VISIBLE) {
                    recyclerView.visibility = View.INVISIBLE
                } else
                    recyclerView.visibility = View.VISIBLE
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}