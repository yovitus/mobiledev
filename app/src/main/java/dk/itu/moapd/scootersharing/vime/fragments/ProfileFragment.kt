/*
 * MIT License
 *
 * Copyright (c) 2023 Vitus Girelli Meiner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.scootersharing.vime.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.activities.LoginActivity
import dk.itu.moapd.scootersharing.vime.databinding.FragmentProfileBinding
import dk.itu.moapd.scootersharing.vime.utils.requestUserPermissions
import java.io.File
import java.io.FileInputStream

/**
 * An activity class with methods to manage the main activity of Getting Started application.
 */
class ProfileFragment : Fragment() {
    companion object {
//        private val TAG = ProfileFragment::class.qualifiedName
    }
    /*
    * These are viewbindings that allows easy read
     */

    private var _binding: FragmentProfileBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val BUCKET_URL = "gs://scooter-sharing-6a9a7.appspot.com"

    private lateinit var auth: FirebaseAuth

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { didTakePhoto: Boolean ->
        if (didTakePhoto) {
            val storageRef = Firebase.storage(BUCKET_URL).reference
            val imageRef = storageRef.child("images/CPH001-latest.jpg")
            val stream = FileInputStream(photoFile)
            imageRef.putStream(stream)
        }
    }

    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

    private var requestCameraPermission: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentProfileBinding.inflate(inflater, container, false)

        val photoName = "CPH001-latest.jpg"
        photoFile = File(requireContext().applicationContext.filesDir, photoName)
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "dk.itu.moapd.scootersharing.vime.fileprovider",
            photoFile
        )

        val permissions = arrayOf(Manifest.permission.CAMERA)
        val onGranted: () -> Unit = {
            takePhoto.launch(photoUri)
        }
        requestCameraPermission = requestUserPermissions(permissions, onGranted)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            editCardButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_profile_to_editCardDialogFragment
                )
            }

            signOutButton.setOnClickListener {
                auth.signOut()
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            capturePhotoButton.setOnClickListener {
                if (requestCameraPermission != null)
                    requestCameraPermission!!()
                else
                    takePhoto.launch(photoUri)
            }
        }
    }
}
