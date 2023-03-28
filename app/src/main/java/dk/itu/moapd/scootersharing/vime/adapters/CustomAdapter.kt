package dk.itu.moapd.scootersharing.vime.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.Scooter
import dk.itu.moapd.scootersharing.vime.databinding.ListRideBinding

class CustomAdapter(
    private val data: List<Scooter>,
    private val onItemClicked: (Scooter) -> Unit,
    private val onRemoveClicked: (Scooter) -> Unit) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    companion object {
//        private val TAG = CustomAdapter::class.qualifiedName
    }

    class ViewHolder(
        private val binding: ListRideBinding,
        val onRemoveClicked: (Scooter) -> Unit)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(scooter: Scooter) {
            binding.listName.text = scooter.name
            binding.listLocationTime.text = binding.root.resources.getString(R.string.locationTimeText, scooter.location, scooter.date)

            binding.listDeleteButton.setOnClickListener {
                onRemoveClicked(scooter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRideBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onRemoveClicked)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scooter = data[position]
        holder.bind(scooter)
        holder.itemView.setOnClickListener { onItemClicked(scooter) }
    }

    override fun getItemCount(): Int = data.size
}