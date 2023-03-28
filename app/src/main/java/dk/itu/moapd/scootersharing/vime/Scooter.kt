package dk.itu.moapd.scootersharing.vime

import java.text.SimpleDateFormat
import java.util.*

/**
 * A class containing information about scooters.
 *
 * @property name The name of the specific scooter
 * @property location The current location of the scooter
 * @property timestamp The timestamp of the scooter being rented
 */

class Scooter (var name: String, var location: String, var timestamp: Long = System.currentTimeMillis()) {
    var date: String = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timestamp))
    private var weekDay = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(timestamp))

    /**+
     * Overriding method to display a different toString().
     */
    override fun toString(): String {
        return "Ride started at ${weekDay.lowercase()} the $date, using scooter $name at $location"
    }
}