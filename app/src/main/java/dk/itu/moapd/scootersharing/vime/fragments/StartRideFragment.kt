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
package dk.itu.moapd.scootersharing.vime.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.activities.MainActivity
import dk.itu.moapd.scootersharing.vime.utils.createDialog
import dk.itu.moapd.scootersharing.vime.databinding.FragmentStartRideBinding
import dk.itu.moapd.scootersharing.vime.utils.hideKeyboard

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class StartRideFragment : Fragment() {
    companion object {
        private val TAG = StartRideFragment::class.qualifiedName
    }

    /*
    * These are viewbindings that allows easy read
     */

    private var _binding: FragmentStartRideBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.database.child("")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentStartRideBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            // Buttons.
            startRideButton.setOnClickListener {
                if (binding.editTextName.text.toString().isEmpty() ||
                        binding.editTextLocation.text.toString().isEmpty()) {
                    Snackbar.make(
                        root,
                        "Please fill out name and location",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    requireContext().createDialog(
                        "Start Ride",
                        "Are you sure you want to start a ride?",
                        (fun () {
                            // Update the object attributes.
                            val name = binding.editTextName.text.toString().trim()
                            val location = binding.editTextLocation.text.toString().trim()

                            //ridesDB.addScooter(name, location)

                            // Reset the text fields and update the UI
                            binding.editTextName.text?.clear()
                            binding.editTextLocation.text?.clear()

                            //ridesDB.showMessage(binding.root, ridesDB.getCurrentScooterInfo(), TAG)

                            findNavController().navigate(
                                R.id.show_mainFragment_from_startRideFragment
                            )
                            requireContext().hideKeyboard(binding.root)
                        })
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
            _binding = null
    }
}