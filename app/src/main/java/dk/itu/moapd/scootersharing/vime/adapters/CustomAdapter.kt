package dk.itu.moapd.scootersharing.vime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.data.Ride
import dk.itu.moapd.scootersharing.vime.data.Scooter
import dk.itu.moapd.scootersharing.vime.databinding.ListRideBinding
import dk.itu.moapd.scootersharing.vime.utils.getScooter
import dk.itu.moapd.scootersharing.vime.utils.loadImageInto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomAdapter(
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
            loadImageInto(binding.root.context, scooter.imageUrl, binding.listImage)
            binding.name.text = scooter.name
            if (ride.endTime != null) {
                binding.time.text = binding.root.resources.getString(
                    R.string.string_combiner,
                    ride.getStartDateWithFormat("dd/MM HH:mm"),
                    ride.getEndDateWithFormat("dd/MM HH:mm")
                )
            } else
                binding.time.text = ride.getStartDateWithFormat("dd/MM HH:mm")
            if (ride.price != null)
                binding.endPrice.text = binding.root.resources.getString(
                R.string.price_dkk,
                ride.price.toString()
            )
            if (ride.topAcceleration != null)
                binding.topSpeed.text = binding.root.resources.getString(
                    R.string.speed_m_ss,
                    ride.topAcceleration.toString()
                )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRideBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, ride: Ride) {
        CoroutineScope(Dispatchers.Main).launch {
            val scooter = getScooter(ride.scooterId)
            holder.apply {
                if (scooter != null)
                    bind(ride, scooter)
            }
        }
    }
}