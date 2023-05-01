package dk.itu.moapd.scootersharing.vime.data
data class Card(
    val cardNumber: Long,
    val expMonth: Int,
    val expYear: Int,
    val cvv: Int
) {
    constructor() : this(0, 0, 0, 0)
}