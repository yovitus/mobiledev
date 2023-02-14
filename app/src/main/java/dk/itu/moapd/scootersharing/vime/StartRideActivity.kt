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

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.core.view.WindowCompat
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.vime.databinding.ActivityStartRideBinding

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class StartRideActivity : AppCompatActivity() {
    companion object {
        private val TAG = StartRideActivity::class.qualifiedName
    }
    /*
    * These are viewbindings that allows easy read
     */
    // GUI variables.
    private lateinit var scooterName: EditText
    private lateinit var location: EditText
    private lateinit var mainBinding: ActivityStartRideBinding

    private val scooter: Scooter = Scooter("", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        mainBinding = ActivityStartRideBinding.inflate(layoutInflater)

        with(mainBinding) {

            // Buttons.
            startride.setOnClickListener {
                if (scooterName.text.toString().isNotEmpty() && location.text.toString().isNotEmpty()) {
                    // Update the object attributes.
                    val name = scooterName.text.toString().trim()
                    scooter.name = name

                    val location = location.text.toString().trim()
                    scooter.location = location

                    // Reset the text fields and update the UI
                    showMessage()
                }
            }
        }
        setContentView(mainBinding.root)
    }

    private fun showMessage () {
        // Print a message in the ‘Logcat‘ system.
        Log.d(TAG, scooter.toString())
        val mySnackbar = Snackbar.make(mainBinding.root, scooter.toString(), LENGTH_SHORT)
        mySnackbar.show()
    }
}