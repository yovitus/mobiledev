package dk.itu.moapd.scootersharing.vime.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.vime.databinding.FragmentScooterDialogBinding
import dk.itu.moapd.scootersharing.vime.utils.loadScooterImageInto

class ScooterDialogFragment : BottomSheetDialogFragment() {
    private val BUCKET_URL = "gs://scooter-sharing-6a9a7.appspot.com"

    private var _binding: FragmentScooterDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScooterDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            scooterTitle.text = arguments?.getString("scooterTitle")
            scooterAddress.text = arguments?.getString("scooterAddress")
            val imageUrl = arguments?.getString("scooterImageUrl")
            val storageRef = Firebase.storage(BUCKET_URL).reference
            if (imageUrl != null)
                storageRef.child(imageUrl).loadScooterImageInto(requireContext(), scooterImage)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}