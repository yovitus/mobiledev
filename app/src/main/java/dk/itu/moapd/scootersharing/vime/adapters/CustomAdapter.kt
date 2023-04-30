package dk.itu.moapd.scootersharing.vime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.data.Ride
import dk.itu.moapd.scootersharing.vime.data.Scooter
import dk.itu.moapd.scootersharing.vime.databinding.ListRideBinding

class CustomAdapter(
    private val database: DatabaseReference,
    options: FirebaseRecyclerOptions<Ride>
) :
    FirebaseRecyclerAdapter<Ride,
            CustomAdapter.ViewHolder>(options) {
    companion object {
//        private val TAG = CustomAdapter::class.qualifiedName
    }

    class ViewHolder(
        private val binding: ListRideBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ride: Ride, scooter: Scooter) {
            binding.listNameLocation.text = scooter.name
            if (ride.endTime != null) {
                binding.listLocationTime.text = binding.root.resources.getString(
                    R.string.stringCombiner,
                    ride.getStartDateWithFormat("dd/MM HH:mm"),
                    ride.getEndDateWithFormat("dd/MM HH:mm")
                )
            } else
                binding.listLocationTime.text = ride.getStartDateWithFormat("dd/MM HH:mm")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRideBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, ride: Ride) {
        database.child("scooters").child(ride.scooterId).get().addOnSuccessListener {
            val scooter = it.getValue(Scooter::class.java)
            holder.apply {
                if (scooter != null) {
                    bind(ride, scooter)
                }
            }
        }
    }
}