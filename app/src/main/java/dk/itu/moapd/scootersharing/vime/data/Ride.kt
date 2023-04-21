package dk.itu.moapd.scootersharing.vime.data

import java.text.SimpleDateFormat
import java.util.*

data class Ride(
    val scooterId: String,
    val timeStart: Long,
    val timeEnd: Long,
    val endLocationLat: Long,
    val endLocationLon: Long,
    val price: Int
) {

    constructor() : this("", 0, 0, 0, 0, 0)

    private var startDate: String =
        SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timeStart))
    private var startWeekDay = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(timeStart))

    private var endDate: String =
        SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timeEnd))
    private var endWeekDay = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(timeEnd))

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