package dk.itu.moapd.scootersharing.vime.data

import java.text.SimpleDateFormat
import java.util.*

data class Ride(
    val scooterId: String,
    val startTime: Long = System.currentTimeMillis(),
    var endTime: Long? = null,
    var endLocationAddress: String? = null,
    var endLocationLat: Double? = null,
    var endLocationLon: Double? = null,
    var price: Float? = null,
    var topAcceleration: Int? = null
) {
    constructor() : this("")

    /**+
     * Overriding method to display a different toString().
     */
    override fun toString(): String {
        return "Ride started at ${getStartDateWithFormat("EEEE").lowercase()} the ${
            getStartDateWithFormat(
                "dd/MM"
            )
        }"
    }

    /**+
     * Method to get start date in specific format.
     * E.g. pattern 'dd/MM' for date, pattern 'EEEE' for weekday, etc.
     * See https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
     * for more details.
     */
    fun getStartDateWithFormat(pattern: String): String {
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(startTime))
    }

    /**+
     * Method to get end date in specific format.
     * E.g. pattern 'dd/MM' for date, pattern 'EEEE' for weekday, etc.
     * See https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
     * for more details.
     */
    fun getEndDateWithFormat(pattern: String): String? {
        return if (endTime != null)
            SimpleDateFormat(pattern, Locale.getDefault()).format(Date(endTime!!))
        else
            null
    }
}