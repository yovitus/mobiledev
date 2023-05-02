package dk.itu.moapd.scootersharing.vime.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.data.Card
import dk.itu.moapd.scootersharing.vime.databinding.FragmentEditCardDialogBinding
import dk.itu.moapd.scootersharing.vime.utils.editCard
import dk.itu.moapd.scootersharing.vime.utils.getCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditCardDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentEditCardDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var userCard: Card? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditCardDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        CoroutineScope(Dispatchers.Main).launch {
            userCard = getCard()

            if (userCard != null) {
                binding.apply {
                    editTextCardnumber.setText(userCard!!.cardNumber.toString())
                    editTextExpiration.setText(
                        root.resources.getString(
                            R.string.exp_month_year,
                            userCard!!.expMonth.toString(),
                            userCard!!.expYear.toString()
                        )
                    )
                    editTextCvv.setText(userCard!!.cvv.toString())
                }
            }
        }

        binding.apply {
            saveButton.setOnClickListener {
                binding.apply {
                    val cardNumber = editTextCardnumber.text.toString().replace(" ", "")
                    val expiration = editTextExpiration.text.toString().replace(" ", "")
                    val cvv = editTextCvv.text.toString().replace(" ", "")

                    val cardNumberULong = cardNumber.toULongOrNull()
                    if (cardNumber.length != 16 || cardNumberULong == null) {
                        Toast.makeText(
                            context,
                            "Invalid card number (e.g. '0000 0000 0000 0000')",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val exp = expiration.split("/")
                    if (exp.size != 2 || exp[0].length != 2 || exp[1].length != 2) {
                        Toast.makeText(
                            context,
                            "Invalid expiration date (e.g. '00/00')",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener

                    }

                    val expMonthUInt = exp[0].toUIntOrNull()
                    val expYearUInt = exp[1].toUIntOrNull()
                    if (expMonthUInt == null || expMonthUInt > 12u || expYearUInt == null) {
                        Toast.makeText(
                            context,
                            "Invalid expiration date (e.g. '00/00')",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    val cvvUInt = cvv.toUIntOrNull()
                    if (cvv.length != 3 || cvvUInt == null) {
                        // Show an error message indicating invalid cvv
                        Toast.makeText(context, "Invalid CVV (e.g. '000')", Toast.LENGTH_SHORT)
                            .show()
                        return@setOnClickListener
                    }

                    val card = Card(
                        cardNumberULong.toLong(),
                        expMonthUInt.toInt(),
                        expYearUInt.toInt(),
                        cvvUInt.toInt()
                    )
                    editCard(card)

                    Toast.makeText(context, "Card saved!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(
                        R.id.action_editCardDialogFragment_to_profile
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}