package dk.itu.moapd.scootersharing.vime.data

data class Scooter(
    val name: String,
    val locationLat: Double,
    val locationLon: Double,
    val imageUrl: String,
    val available: Boolean = true
) {
    constructor() : this("", 0.0, 0.0, "")
}

