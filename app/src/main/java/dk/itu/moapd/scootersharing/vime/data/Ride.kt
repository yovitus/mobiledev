package dk.itu.moapd.scootersharing.vime.data

import android.os.Build
import java.text.SimpleDateFormat
import java.util.*
import java.time.Instant
import java.time.ZoneId

data class Ride (val scooterId: String, val time_start: Long, val time_end: Long) {

    constructor() : this("", 0, 0)

    private var startDate: String = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(time_start))
    private var startWeekDay = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(time_start))

    private var endDate: String = SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(time_end))
    private var endWeekDay = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(time_end))

    /**+
     * Overriding method to display a different toString().
     */
    override fun toString(): String {
        return "Ride started at ${startWeekDay.lowercase()} the $endDate"
    }

    fun getStartDate(): String {
        return startDate
    }

    fun getEndDate(): String {
        return endDate
    }
}