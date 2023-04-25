package dk.itu.moapd.scootersharing.vime.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.databinding.ActivityMainBinding

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.qualifiedName
        //lateinit var database: DatabaseReference
    }

    /**
     * View binding allows easy written code to interact with views.
     */
    private lateinit var binding : ActivityMainBinding
    /**
     * Authentication for logged in user. If auth.currentUser == null, navigate to LoginActivity.
     */
    private lateinit var auth: FirebaseAuth

//    private lateinit var navGraph: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    /**
     * onCreate is called when the activity starts. Initialization such as `setContentView(view)`
     * is called here.
     *
     * @param savedInstanceState If activity is re-initialized, then the bundle contains the most
     * recent data from `onSaveInstanceState()`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
//        navGraph = findNavController(R.id.nav_graph)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        binding.bottomNav.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.home -> {
//                    navGraph.navigate(R.id.home)
//                    true
//                } R.id.maps -> {
//                    navGraph.navigate(R.id.maps)
//                    true
//                } R.id.profile -> {
//                    navGraph.navigate(R.id.profile)
//                    true
//                }
//                else -> false
//            }
//        }
        val view = binding.root
        setContentView(view)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.frament_container) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup the action bar only in the portrait mode.
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            appBarConfiguration = AppBarConfiguration(navController.graph)
            setupActionBarWithNavController(navController, appBarConfiguration)
        }

//        database =
//            Firebase.database("https://scooter-sharing-6a9a7-default-rtdb.europe-west1.firebasedatabase.app/").reference

    }


    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null)
            startLoginActivity()
        Log.println(Log.INFO, TAG, "Signed in as user ${auth.currentUser?.displayName}")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        //add navigation to mainFragment, mapsFragment and startRideFragment using actions
//        when (item.itemId) {
//            R.id.home -> {
//                Navigation.findNavController(binding.root).navigate(R.id.action_mapsFragment_to_mainFragment)
//                true
//            } R.id.maps -> {
//                Navigation.findNavController(binding.root).navigate(R.id.action_mainFragment_to_mapsFragment)
//                true
//            } R.id.profile -> {
//                Navigation.findNavController(binding.root).navigate(R.id.action_mainFragment_to_startRideFragment)
//                true
//            }
//            else -> false
//        }
//        return super.onOptionsItemSelected(item)
//    }


    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}