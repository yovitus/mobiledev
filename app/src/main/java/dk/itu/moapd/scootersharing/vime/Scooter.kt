package dk.itu.moapd.scootersharing.vime

import android.os.Build
import java.time.Instant
import java.time.ZoneId
import java.util.*

/**
 * A class containing information about scooters.
 *
 * @property name The name of the specific scooter
 * @property location The current location of the scooter
 * @property timestamp The timestamp of the scooter being rented
 * @property date Private property to manipulate date
 */

class Scooter (val name: String, _location: String, _timestamp: Long = System.currentTimeMillis()) {
    var location: String = _location
    var timestamp: Long = _timestamp

    /**+
     * Overriding method to display a different toString().
     */
    override fun toString(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("CET")).toLocalDateTime()
            "Ride started at ${date.dayOfWeek.name.lowercase()} the ${getDate()}, using scooter $name at $location"
        } else {
            "Ride started at $timestamp, using scooter $name at $location"
        }
    }

    fun getDate(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.of("CET")).toLocalDateTime()
            "${date.dayOfMonth}/${date.monthValue}"
        } else {
            "$timestamp"
        }
    }
}