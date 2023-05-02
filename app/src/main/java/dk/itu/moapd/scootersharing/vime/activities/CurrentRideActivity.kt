package dk.itu.moapd.scootersharing.vime.activities

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.data.Ride
import dk.itu.moapd.scootersharing.vime.data.Scooter
import dk.itu.moapd.scootersharing.vime.databinding.ActivityCurrentRideBinding
import dk.itu.moapd.scootersharing.vime.services.LinearAccelerationUpdatesService
import dk.itu.moapd.scootersharing.vime.services.LocationUpdatesService
import dk.itu.moapd.scootersharing.vime.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import kotlin.math.ceil

class CurrentRideActivity : AppCompatActivity() {

    companion object {
//        private val TAG = CurrentRideActivity::class.qualifiedName
    }

    private lateinit var binding: ActivityCurrentRideBinding

    private lateinit var scooter: Scooter
    private lateinit var ride: Ride

    private var locationUpdatesService: LocationUpdatesService? = null
    private var accelerationUpdatesService: LinearAccelerationUpdatesService? = null

    private var curLat: Double? = null
    private var curLon: Double? = null
    private var curAddr: String? = null

    private var topAcceleration: Int? = null

    private val locationConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as LocationUpdatesService.LocalBinder
            locationUpdatesService = binder.getService()

            locationUpdatesService!!.subscribeToLocationUpdates(
                upFunc = { loc, addr ->
                    curLat = loc.latitude
                    curLon = loc.longitude
                    curAddr = addr
                }
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationUpdatesService?.unsubscribeToLocationUpdates()
        }
    }

    private val accelerationConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as LinearAccelerationUpdatesService.LocalBinder
            accelerationUpdatesService = binder.getService()

            accelerationUpdatesService!!.subscribeToAccelerationUpdates { acceleration ->
                val accelerationInt = acceleration.toInt()

                // Update speed text
                binding.speed.text =
                    resources.getString(R.string.speed_m_ss, accelerationInt.toString())

                // Updates topAcceleration if topAcceleration is null or if it is lower than current
                if (topAcceleration == null || topAcceleration!! < accelerationInt)
                    topAcceleration = accelerationInt
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            accelerationUpdatesService?.unsubscribeToAccelerationUpdates()
        }

    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto) {
            val storageRef = Firebase.storage(BUCKET_URL).reference
            val imageRef = storageRef.child(scooter.latestImageUrl)
            val stream = FileInputStream(photoFile)
            imageRef.putStream(stream)
            stopRideFunc()
        }
    }

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCurrentRideBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        getPermissions()

        Intent(
            this,
            LinearAccelerationUpdatesService::class.java
        ).also { intent ->
            bindService(intent, accelerationConnection, Context.BIND_AUTO_CREATE)
        }

        CoroutineScope(Dispatchers.Main).launch {
            ride = getCurrentRide()!!
            scooter = getScooter(ride.scooterId)!!

            val photoName = "${scooter.name}-latest.jpg"
            photoFile = File(this@CurrentRideActivity.applicationContext.filesDir, photoName)
            photoUri = FileProvider.getUriForFile(
                this@CurrentRideActivity,
                "dk.itu.moapd.scootersharing.vime.fileprovider",
                photoFile
            )

            binding.apply {
                loadImageInto(this@CurrentRideActivity, scooter.imageUrl, imageView)

                // Set the custom digital clock style
                duration.setTextAppearance(R.style.DigitalClockTextAppearance)

                // Set the custom background drawable
                val digitalClockBackground: Drawable? =
                    ContextCompat.getDrawable(
                        this@CurrentRideActivity,
                        R.drawable.digital_clock_background
                    )
                duration.background = digitalClockBackground

                // Create a handler to update the clock every second
                val handler = Handler()
                handler.post(object : Runnable {
                    override fun run() {
                        // Get time diff
                        ride.endTime = System.currentTimeMillis()
                        val timeDiff = ride.endTime!! - ride.startTime

                        // Get hours, minutes and remaining seconds, then format them
                        val seconds = (timeDiff / 1000).toInt()
                        val hours = seconds / 3600
                        val minutes = (seconds % 3600) / 60
                        val remainingSeconds = seconds % 60

                        val time = String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)

                        // Set the text view text to the current time
                        duration.text = time

                        // Update price as well
                        val price = timeDiff / 60000.0 * (resources.getString(R.string.price_number)
                            .toFloat())
                        ride.price = (ceil(price * 100) / 100).toFloat()

                        currentPrice.text =
                            resources.getString(R.string.price_dkk, ride.price.toString())

                        // Call this method again after one second
                        handler.postDelayed(this, 1000)
                    }
                })

                stopRideButton.setOnClickListener {
                    stopRide()
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        locationUpdatesService?.unsubscribeToLocationUpdates()
        accelerationUpdatesService?.unsubscribeToAccelerationUpdates()
        if (locationUpdatesService != null)
            unbindService(locationConnection)
        if (accelerationUpdatesService != null)
            unbindService(accelerationConnection)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
        )
        val onPermissionsGranted: () -> Unit = {
            if ((ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                Intent(
                    this@CurrentRideActivity,
                    LocationUpdatesService::class.java
                ).also { intent ->
                    bindService(intent, locationConnection, Context.BIND_AUTO_CREATE)
                }
            }
        }

        val requestPermission = getRequestUserPermissions(permissions, onPermissionsGranted)
        requestPermission()
    }

    private fun stopRide() {
        if (ContextCompat.checkSelfPermission(
                this@CurrentRideActivity,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
            takePhoto.launch(photoUri)
        else {
            stopRideFunc()
        }
    }

    private fun stopRideFunc() {
        if (
            ContextCompat.checkSelfPermission(
                this@CurrentRideActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this@CurrentRideActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            ride.topAcceleration = topAcceleration
            if (curAddr != null && curLat != null && curLon != null) {
                ride.endLocationLat = curLat
                ride.endLocationLon = curLon
                ride.endLocationAddress = curAddr
            }
            CoroutineScope(Dispatchers.Main).launch {
                endRide(ride)
                startMainActivity()
            }
        } else {
            CoroutineScope(Dispatchers.Main).launch {
                endRide(ride)
                startMainActivity()
            }
        }
    }
}










