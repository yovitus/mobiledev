package dk.itu.moapd.scootersharing.vime

import java.sql.Timestamp

data class Scooter (
    var name: String,
    var location: String,
    val timestamp: Long = System.currentTimeMillis()
    ) {
    override fun toString(): String {
        return "[Scooter] $name is placed at $location."
    }


}