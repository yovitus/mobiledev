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

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.vime.databinding.FragmentUpdateRideBinding

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class UpdateRideFragment : Fragment() {
    companion object {
        private val TAG = UpdateRideFragment::class.qualifiedName
        lateinit var ridesDB: RidesDB

    }
    /*
    * These are viewbindings that allows easy read
     */

    private var _binding: FragmentUpdateRideBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ridesDB = RidesDB.get(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentUpdateRideBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            editTextName.setText(ridesDB.getCurrentScooter().name)
            editTextLocation.setText(ridesDB.getCurrentScooter().location)

            // Buttons.
            updateRideButton.setOnClickListener {
                if (editTextLocation.text.toString().isNotEmpty()) {
                    // Update the object attributes.
                    val location = editTextLocation.text.toString().trim()
                    ridesDB.updateCurrentScooter(location)
                    editTextLocation.text?.clear()

                    showMessage()
                }
            }
        }
    }

    /**
     * Shows a message containing information about the scooter.
     */
    private fun showMessage() {
        // Print a message in the 'Logcat' system
        Log.d(TAG, ridesDB.getCurrentScooterInfo())
        // And print at the bottom of phone
        Snackbar.make(
            binding.root,
            ridesDB.getCurrentScooterInfo(),
            Snackbar.LENGTH_SHORT
        ).show()
    }
}

