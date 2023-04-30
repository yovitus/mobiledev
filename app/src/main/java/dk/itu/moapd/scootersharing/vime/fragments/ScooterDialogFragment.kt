package dk.itu.moapd.scootersharing.vime.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.itu.moapd.scootersharing.vime.databinding.FragmentScooterDialogBinding

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    ScooterDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class ScooterDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentScooterDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScooterDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            scooterTitle.text = arguments?.getString("scooterTitle")
            scooterAddress.text = arguments?.getString("scooterAddress")
//            scooterImage.setImageResource(arguments?.getString("scooterImage"))
        }
    }

    companion object {

        // TODO: Customize parameters
        fun newInstance(itemCount: Int): ScooterDialogFragment =
            ScooterDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}