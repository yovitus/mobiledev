package dk.itu.moapd.scootersharing.vime.livedata

import androidx.lifecycle.LiveData
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.scootersharing.vime.data.Scooter
import dk.itu.moapd.scootersharing.vime.utils.DATABASE_URL
import dk.itu.moapd.scootersharing.vime.utils.getIdsToScooters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ScootersLiveData() : LiveData<Map<String, Scooter>>() {
    val db = Firebase.database(DATABASE_URL).reference

    private val listener = object : ChildEventListener {
        private fun onChildAddedOrChanged(snapshot: DataSnapshot) {
            val key = snapshot.key
            val scooter = snapshot.getValue(Scooter::class.java)

            value?.let {
                val newList = it.toMutableMap().apply {
                    if (key != null && scooter != null) {
                        if (scooter.available)
                            put(key, scooter)
                        else
                            remove(key)
                    }

                }
                postValue(newList)
            }
        }

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            onChildAddedOrChanged(snapshot)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            onChildAddedOrChanged(snapshot)
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val key = snapshot.key
            value?.let {
                val newList = it.toMutableMap().apply {
                    if (key != null)
                        remove(key)
                }
                postValue(newList)
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onActive() {
        super.onActive()
        db.child("scooters").addChildEventListener(listener)
        coroutineScope.launch {
            val scooters = getIdsToScooters().filter { (_, scooter) ->
                scooter.available
            }
            postValue(scooters)
        }
    }

    override fun onInactive() {
        super.onInactive()
        db.child("scooters").removeEventListener(listener)
        coroutineScope.cancel()
    }
}