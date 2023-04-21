package dk.itu.moapd.scootersharing.vime.utils

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import dk.itu.moapd.scootersharing.vime.data.Ride
import dk.itu.moapd.scootersharing.vime.data.Scooter

fun FirebaseUser?.addRide(db: DatabaseReference, ride: Ride) {
    // Getting the scooter
    db.child("scooters").child(ride.scooterId).get().addOnSuccessListener { snapshot ->
        val scooter = snapshot.getValue(Scooter::class.java)

        if (scooter != null) {
            // Adding ride
            this.let { user ->
                val uid = user?.let { db.child("rides").child(it.uid).push().key }

                if (uid != null) {
                    db.child("rides").child(user.uid).child(uid).setValue(ride)
                }
            }

            // Updating the scooter
            db.child("scooters").child(ride.scooterId)
                .setValue(Scooter(scooter.name, 0, 0, "url", true))
        }

    }
}

fun DatabaseReference.addScooter(scooter: Scooter) {
    val uid = this.child("scooters").push().key

    if (uid != null) {
        this.child("scooters").child(uid).setValue(scooter)
    }
}