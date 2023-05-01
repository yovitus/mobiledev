package dk.itu.moapd.scootersharing.vime.data

data class Scooter(
    val name: String,
    val address: String,
    val locationLat: Double,
    val locationLon: Double,
    val imageUrl: String,
    val latestImageUrl: String,
    val available: Boolean = true
) {
    constructor() : this("", "", 0.0, 0.0, "", "")
}

