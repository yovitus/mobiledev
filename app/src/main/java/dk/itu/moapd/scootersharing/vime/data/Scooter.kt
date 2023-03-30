package dk.itu.moapd.scootersharing.vime.data

import android.os.Build
import java.time.Instant
import java.time.ZoneId

/**
 * A class containing information about scooters.
 *
 * @property name The name of the specific scooter
 * @property location The current location of the scooter
 * @property timestamp The timestamp of the scooter being rented
 */

class Scooter (name: String, location: String, timestamp: Long = System.currentTimeMillis()) {
    var name = name
    var location = location
    var timestamp = timestamp
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