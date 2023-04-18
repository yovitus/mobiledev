package dk.itu.moapd.scootersharing.vime.data

import android.os.Build
import java.time.Instant
import java.time.ZoneId

data class Scooter (val name: String, val location: String) {
    constructor() : this("", "")
}

