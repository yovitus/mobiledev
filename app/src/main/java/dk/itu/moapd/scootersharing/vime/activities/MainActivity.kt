package dk.itu.moapd.scootersharing.vime.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.databinding.ActivityMainBinding

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.qualifiedName
        lateinit var database: DatabaseReference
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
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        database =
            Firebase.database("https://scooter-sharing-6a9a7-default-rtdb.europe-west1.firebasedatabase.app/").reference

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sign_out_button -> {
                auth.signOut()
                Log.println(Log.INFO, TAG, "Signing out...")
                startLoginActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}