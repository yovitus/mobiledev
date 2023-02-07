package dk.itu.moapd.scootersharing.vime

data class Scooter (var name: String, var location: String) {
    override fun toString(): String {
        return "[Scooter] $name is placed at $location."
    }
}