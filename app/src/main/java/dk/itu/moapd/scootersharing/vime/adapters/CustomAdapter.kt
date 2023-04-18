package dk.itu.moapd.scootersharing.vime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.vime.data.Ride
import dk.itu.moapd.scootersharing.vime.databinding.ListRideBinding

class CustomAdapter(options: FirebaseRecyclerOptions<Ride>
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
        fun bind(ride: Ride) {
            binding.listName.text = ride.scooterId
            //binding.listLocationTime.text = binding.root.resources.getString(R.string.locationTimeText, scooter.location)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRideBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, ride: Ride) {
        holder.apply {
            bind(ride)
        }
    }
}