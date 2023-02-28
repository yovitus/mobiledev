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
import android.widget.EditText
import androidx.core.view.WindowCompat
import dk.itu.moapd.scootersharing.vime.databinding.ActivityMainBinding

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var ridesDB: RidesDB
        private lateinit var adapter: CustomArrayAdapter
    }
    /*
    * These are viewbindings that allows easy read
     */
    // GUI variables.
    private lateinit var scooterName: EditText
    private lateinit var location: EditText
    private lateinit var mainBinding: ActivityMainBinding

    private val scooter: Scooter = Scooter("", "")
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        ridesDB = RidesDB.get(this)


        adapter = CustomArrayAdapter(this, R.layout.list_rides, ridesDB.getRidesList())
        mainBinding = ActivityMainBinding.inflate(layoutInflater)

        val fragment = MainFragment()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, fragment)
            .commit()

    }

        override fun onRestart() {
            super.onRestart()
            adapter.notifyDataSetChanged()
        }
}