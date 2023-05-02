package dk.itu.moapd.scootersharing.vime.utils

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Query
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.vime.data.Card
import dk.itu.moapd.scootersharing.vime.data.Ride
import dk.itu.moapd.scootersharing.vime.data.Scooter
import kotlinx.coroutines.tasks.await

const val BUCKET_URL = "gs://scooter-sharing-6a9a7.appspot.com"
const val DATABASE_URL =
    "https://scooter-sharing-6a9a7-default-rtdb.europe-west1.firebasedatabase.app/"

val storageRef = Firebase.storage(BUCKET_URL).reference
val db = Firebase.database(DATABASE_URL).reference
val auth = FirebaseAuth.getInstance()
val currentUser = auth.currentUser!!

/**
 * The following include extension function for the table 'scooters' in the firebase realtime db.
 */

// Used for creating the 3 default scooters
fun addScooter(scooter: Scooter) {
    val uid = db.child("scooters").push().key

    if (uid != null) {
        db.child("scooters").child(uid).setValue(scooter)
    }
}

suspend fun getScooter(id: String): Scooter? {
    val snapshot = db.child("scooters").child(id).get().await()

    return if (snapshot.exists())
        snapshot.getValue(Scooter::class.java)
    else
        null
}

suspend fun getIdsToScooters(): Map<String, Scooter> {
    val map = mutableMapOf<String, Scooter>()
    val snapshot = db.child("scooters").get().await()
    snapshot.children.forEach { scooterSnap ->
        scooterSnap.getValue(Scooter::class.java)?.let { scooter ->
            map[scooterSnap.key!!] = scooter
        }
    }
    return map
}


/**
 * The following include extension function for the table 'rides' in the firebase realtime db.
 */

fun startRide(ride: Ride) {
    db.child("scooters").child(ride.scooterId).child("available").setValue(false)

    val userRef = db.child("users").child(currentUser.uid)
    val uid = userRef.child("rides").push().key

    if (uid != null) {
        userRef.child("rides").child(uid).setValue(ride)
        userRef.child("currentRide").setValue(uid)
    }
}

suspend fun endRide(ride: Ride) {
    val scooter = getScooter(ride.scooterId)!!

    if (ride.endLocationLat != null && ride.endLocationLon != null && ride.endLocationAddress != null) {
        scooter.locationLon = ride.endLocationLon!!
        scooter.locationLat = ride.endLocationLat!!
        scooter.address = ride.endLocationAddress!!
    } else {
        ride.endLocationLat = scooter.locationLat
        ride.endLocationLon = scooter.locationLon
        ride.endLocationAddress = scooter.address
    }
    scooter.available = true

    db.child("scooters").child(ride.scooterId).setValue(scooter)

    val userRef = db.child("users").child(currentUser.uid)

    val rideId = getCurrentRideId()
    if (rideId != null) {
        userRef.child("rides").child(rideId).setValue(ride)
    }
    userRef.child("currentRide").removeValue()
}

fun getRidesQuery(): Query {
    // Realtime database doesn't allow queries ordering by descending
    return db.child("users").child(currentUser.uid).child("rides").orderByChild("time_end")
        .limitToLast(10)
}

suspend fun getCurrentRideId(): String? {
    val snapshot = db.child("users").child(currentUser.uid).child("currentRide").get().await()

    return if (snapshot.exists())
        snapshot.getValue(String::class.java)
    else
        null
}

suspend fun getCurrentRide(): Ride? {
    val id = getCurrentRideId()

    return if (id != null) {
        val snapshot =
            db.child("users").child(currentUser.uid).child("rides").child(id).get().await()

        if (snapshot.exists())
            snapshot.getValue(Ride::class.java)
        else
            null
    } else
        null
}


/**
 * The following include extension function for the table 'cards' in the firebase realtime db.
 */

fun editCard(card: Card) {
    db.child("users").child(currentUser.uid).child("card").setValue(card)
}

suspend fun getCard(): Card? {
    val snapshot = db.child("users").child(currentUser.uid).child("card").get().await()

    return if (snapshot.exists())
        snapshot.getValue(Card::class.java)
    else
        null
}


/**
 * The following include extension function for scooter images in the firebase storage.
 */

fun loadImageInto(ctx: Context, imageUrl: String, view: ImageView) {
    if (ctx is Activity && !ctx.isFinishing)
        storageRef.child(imageUrl).downloadUrl.addOnSuccessListener {
            Glide.with(ctx).load(it)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .into(view)
        }
}