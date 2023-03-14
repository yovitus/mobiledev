package dk.itu.moapd.scootersharing.vime.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dk.itu.moapd.scootersharing.vime.databinding.ActivityMainBinding

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class MainActivity : AppCompatActivity() {
    companion object {
//        private val TAG = MainActivity::class.qualifiedName
    }

    /**
     * View binding allows easy written code to interact with views.
     */
    private lateinit var binding : ActivityMainBinding

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
    }
}