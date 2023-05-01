package dk.itu.moapd.scootersharing.vime.activities

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textview.MaterialTextView
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.databinding.ActivityCurrentRideBinding
import java.text.SimpleDateFormat
import java.util.*

class CurrentRideActivity : AppCompatActivity() {

    companion object {
        private val TAG = CurrentRideActivity::class.qualifiedName
    }

    private lateinit var binding: ActivityCurrentRideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentRideBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Get a reference to the text view
        val digitalClockTextView: MaterialTextView = findViewById(R.id.duration)

        // Set the custom digital clock style
        digitalClockTextView.setTextAppearance(R.style.DigitalClockTextAppearance)

        // Set the custom background drawable
        val digitalClockBackground: Drawable? =
            ContextCompat.getDrawable(this, R.drawable.digital_clock_background)
        digitalClockTextView.background = digitalClockBackground

        // Create a handler to update the clock every second
        val handler = Handler()
        handler.post(object : Runnable {
            override fun run() {
                // Get the current time and format it as hours, minutes, and seconds
                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val time = dateFormat.format(calendar.time)

                // Set the text view text to the current time
                digitalClockTextView.text = time

                // Toggle the visibility of the colon separator every second
                if (digitalClockTextView.text.toString().contains(":")) {
                    digitalClockTextView.text =
                        digitalClockTextView.text.toString().replace(":", " ")
                } else {
                    digitalClockTextView.text =
                        digitalClockTextView.text.toString().replace(" ", ":")
                }

                // Call this method again after one second
                handler.postDelayed(this, 1000)
            }
        })
    }
}










