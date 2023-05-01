package dk.itu.moapd.scootersharing.vime.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.databinding.ActivityMainBinding

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.qualifiedName
    }

    /**
     * View binding allows easy written code to interact with views.
     */
    private lateinit var binding: ActivityMainBinding

    /**
     * Authentication for logged in user. If auth.currentUser == null, navigate to LoginActivity.
     */
    private lateinit var auth: FirebaseAuth

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
        startCurrentRideActivity()
        if (auth.currentUser == null)
            startLoginActivity()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)
    }


    override fun onStart() {
        super.onStart()
        Log.println(Log.INFO, TAG, "Signed in as user ${auth.currentUser?.displayName}")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startCurrentRideActivity() {
        val intent = Intent(this, CurrentRideActivity::class.java)
        startActivity(intent)
        finish()
    }
}