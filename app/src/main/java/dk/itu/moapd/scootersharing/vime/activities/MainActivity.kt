package dk.itu.moapd.scootersharing.vime.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.databinding.ActivityMainBinding
import dk.itu.moapd.scootersharing.vime.utils.getCurrentRideId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class MainActivity : AppCompatActivity() {
    companion object {
//        private val TAG = MainActivity::class.qualifiedName
    }

    private lateinit var binding: ActivityMainBinding

    /**
     * onCreate is called when the activity starts. Initialization such as `setContentView(view)`
     * is called here.
     *
     * @param savedInstanceState If activity is re-initialized, then the bundle contains the most
     * recent data from `onSaveInstanceState()`.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (FirebaseAuth.getInstance().currentUser == null)
            startLoginActivity()
        CoroutineScope(Dispatchers.Main).launch {
            if (getCurrentRideId() != null)
                startCurrentRideActivity()
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)
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