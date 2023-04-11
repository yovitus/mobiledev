package dk.itu.moapd.scootersharing.vime
import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import dk.itu.moapd.scootersharing.vime.data.Scooter
import java.util.*

class RidesDB private constructor(context: Context) {
    private val rides = ArrayList<Scooter>()

    companion object : RidesDBHolder<RidesDB, Context>(::RidesDB)

    init {
        rides.add(
            Scooter("CPH001", "ITU", randomDate())
        )
        rides.add(
            Scooter("CPH002", "Fields", randomDate())
        )
        rides.add(
            Scooter("CPH003", "Lufthavn", randomDate())
        )
    }

    fun addScooter(name: String, location: String) {
        rides.add(Scooter(name, location))
    }

    fun deleteScooter(scooter: Scooter) {
        rides.remove(scooter)
    }

    fun updateCurrentScooter(location: String) {
        rides.last().location = location
    }

    fun getCurrentScooter(): Scooter {
        return rides.last()
    }

    fun getCurrentScooterInfo(): String {
        return rides.last().toString()
    }

    /**
     * Generate a random timestamp in the last 365 days. *
     * @return A random timestamp in the last year.
     */
    private fun randomDate(): Long {
        val random = Random()
        val now = System.currentTimeMillis()
        val year = random.nextDouble() * 1000 * 60 * 60 * 24 * 365
        return (now - year).toLong()
    }

    /**
     * Shows a message containing information about the scooter.
     */
    fun showMessage(root: LinearLayout, scooterInfo: String, TAG: String?) {
        // Print a message in the 'Logcat' system
        Log.d(TAG, scooterInfo)
        // And print at the bottom of phone
        Snackbar.make(
            root,
            scooterInfo,
            Snackbar.LENGTH_SHORT
        ).show()
    }
}

open class RidesDBHolder<out T: Any, in A>(creator: (A) -> T){
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun get(arg: A): T {
        val checkInstance = instance
        if (checkInstance != null)
            return checkInstance

        return synchronized(this) {
            val checkInstanceAgain = instance
            if (checkInstanceAgain != null)
                checkInstanceAgain

            else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}