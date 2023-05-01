package dk.itu.moapd.scootersharing.vime.data

data class Scooter(
    val name: String,
    var address: String,
    var locationLat: Double,
    var locationLon: Double,
    val imageUrl: String,
    val latestImageUrl: String,
    var available: Boolean = true
) {
    constructor() : this("", "", 0.0, 0.0, "", "")
}

