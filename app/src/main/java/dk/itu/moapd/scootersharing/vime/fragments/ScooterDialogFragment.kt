package dk.itu.moapd.scootersharing.vime.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.itu.moapd.scootersharing.vime.data.Scooter
import dk.itu.moapd.scootersharing.vime.databinding.FragmentScooterDialogBinding
import dk.itu.moapd.scootersharing.vime.utils.getScooter
import dk.itu.moapd.scootersharing.vime.utils.loadImageInto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScooterDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentScooterDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var scooter: Scooter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScooterDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        CoroutineScope(Dispatchers.IO).launch {
            scooter = arguments?.getString("scooterId")?.let { getScooter(it) }!!

            binding.apply {
                scooterTitle.text = scooter.name
                scooterAddress.text = scooter.address
                val imageUrl = scooter.imageUrl // should be changed to latestImageUrl
                loadImageInto(requireContext(), imageUrl, scooterImage)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}