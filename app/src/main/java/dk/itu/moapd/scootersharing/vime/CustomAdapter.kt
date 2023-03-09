package dk.itu.moapd.scootersharing.vime

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dk.itu.moapd.scootersharing.vime.databinding.ListRideBinding

class CustomAdapter(
    private val data: List<Scooter>,
    private val onItemClicked: (Scooter) -> Unit) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    companion object {
        private val TAG = CustomAdapter::class.qualifiedName
    }

    class ViewHolder(private val binding: ListRideBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(scooter: Scooter) {
            binding.listName.text = scooter.name
            binding.listLocationTime.text = scooter.location + " - " + scooter.getDate()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRideBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val scooter = data[position]
        holder.bind(scooter)
        holder.itemView.setOnClickListener { onItemClicked(scooter) }
    }

    override fun getItemCount(): Int = data.size
}