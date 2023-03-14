/*
 * MIT License
 *
 * Copyright (c) 2023 Vitus Girelli Meiner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.scootersharing.vime

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
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
        adapter = CustomAdapter(ridesDB.getRidesList(),
            (fun (scooter) { ridesDB.showMessage(binding.root, scooter.toString(), TAG) }),
            (fun (scooter) {
                createDialog(
                    requireContext(),
                    "Delete Ride",
                    "Are you sure you want to delete ride ${scooter.name}?",
                    (fun () {
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
            textViewAndroidversion.text = resources.getString(R.string.android_version_text, Build.VERSION.SDK_INT)

            // Buttons.
            startRideButton.setOnClickListener {
                findNavController().navigate(
                    R.id.show_startRideFragment
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