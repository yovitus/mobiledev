package dk.itu.moapd.scootersharing.vime.singletons

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
import java.io.FileInputStream

const val BUCKET_URL = "gs://scooter-sharing-6a9a7.appspot.com"
const val DATABASE_URL =
    "https://scooter-sharing-6a9a7-default-rtdb.europe-west1.firebasedatabase.app/"

/**
 * FirebaseManager is a singleton implemented using a companion object. To get the instance
 * of the singleton, use FirebaseManager.getInstance().
 * Implemented with the help of https://www.baeldung.com/kotlin/singleton-classes#1-companion-object.
 */
class FirebaseManager private constructor() {
    companion object {
        @Volatile
        private var instance: FirebaseManager? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: FirebaseManager().also { instance = it }
            }
    }

    private val storageRef = Firebase.storage(BUCKET_URL).reference
    val db = Firebase.database(DATABASE_URL).reference
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser!!


    /**
     * The following include functions for getting and adding scooters.
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
     * The following are functions for the user's rides. This includes startRide, endRide etc.
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
     * The following are functions for editing and getting the user's payment card.
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
     * The following are functions for loading and uploading images to the firebase storage.
     */

    fun loadImageInto(ctx: Context, imageUrl: String, view: ImageView) {
        // Necessary if statement, otherwise it may crash if view is destroyed while loading.
        if (ctx is Activity && !ctx.isFinishing)
            storageRef.child(imageUrl).downloadUrl.addOnSuccessListener {
                Glide.with(ctx).load(it)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(view)
            }
    }

    fun uploadImage(stream: FileInputStream, path: String) {
        storageRef.child(path).putStream(stream)
    }
}