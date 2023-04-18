package dk.itu.moapd.scootersharing.vime.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.RidesDB
import dk.itu.moapd.scootersharing.vime.adapters.CustomAdapter
import dk.itu.moapd.scootersharing.vime.utils.createDialog
import dk.itu.moapd.scootersharing.vime.databinding.FragmentMainBinding

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class MainFragment : Fragment() {
    companion object {
        private val TAG = MainFragment::class.qualifiedName
        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomAdapter
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

        ridesDB = RidesDB.get(requireContext())
        adapter = CustomAdapter(
            ridesDB.getRidesList(),
            (fun(scooter) { ridesDB.showMessage(binding.root, scooter.toString(), TAG) }),
            (fun(scooter) {
                requireContext().createDialog(
                    "Delete Ride",
                    "Are you sure you want to delete ride ${scooter.name}?",
                    (fun() {
                        val index = ridesDB.getRidesList().indexOf(scooter)
                        ridesDB.deleteScooter(scooter)
                        adapter.notifyItemRemoved(index)
                    })
                )
            })
        )
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

            mapButton?.setOnClickListener {
                findNavController().navigate(
                    R.id.action_mainFragment_to_mapsFragment
                )
            }

            updateRideButton.setOnClickListener {
                if (ridesDB.getRidesList().isEmpty()) {
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