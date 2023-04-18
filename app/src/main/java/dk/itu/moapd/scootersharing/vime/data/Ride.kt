package dk.itu.moapd.scootersharing.vime.data

import android.os.Build
import java.text.SimpleDateFormat
import java.util.*
import java.time.Instant
import java.time.ZoneId

data class Ride (val scooterId: String, val start_time: Long, val end_time: Long) {

    var date: String = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(start_time))
    private var weekDay = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(start_time))

    /**+
     * Overriding method to display a different toString().
     */
    override fun toString(): String {
        return "Ride started at ${weekDay.lowercase()} the $date, using scooter ${scooterId}"
    }
}