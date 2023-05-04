package dk.itu.moapd.scootersharing.vime.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.activities.CurrentRideActivity
import dk.itu.moapd.scootersharing.vime.data.Ride
import dk.itu.moapd.scootersharing.vime.data.Scooter
import dk.itu.moapd.scootersharing.vime.databinding.FragmentQrScannerDialogBinding
import dk.itu.moapd.scootersharing.vime.livedata.ScootersLiveData
import dk.itu.moapd.scootersharing.vime.singletons.FirebaseManager
import dk.itu.moapd.scootersharing.vime.utils.createDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.CameraBridgeViewBase
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfRotatedRect
import org.opencv.objdetect.QRCodeDetector

class QrScannerDialogFragment : BottomSheetDialogFragment(),
    CameraBridgeViewBase.CvCameraViewListener2 {
    private var _binding: FragmentQrScannerDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val firebaseManager = FirebaseManager.getInstance()

    private var loaderCallback: BaseLoaderCallback? = null
    private lateinit var imageMat: Mat

    private var hasScanned = false

    private var idsToScooters: Map<String, Scooter>? = null
    private var scootersLiveData: ScootersLiveData? = null
    private var observer: Observer<Map<String, Scooter>>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQrScannerDialogBinding.inflate(layoutInflater, container, false)

        scootersLiveData = ScootersLiveData()
        observer = Observer { idsToScooters -> this.idsToScooters = idsToScooters }

        scootersLiveData!!.observe(viewLifecycleOwner, observer!!)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            findNavController().navigate(R.id.action_qrScannerFragment_to_home)
        } else {
            startCamera()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (loaderCallback != null) {
            if (!OpenCVLoader.initDebug())
                OpenCVLoader.initAsync(
                    OpenCVLoader.OPENCV_VERSION,
                    requireContext(), loaderCallback
                )
            else
                loaderCallback!!.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.cameraView.disableView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        observer?.let { scootersLiveData?.removeObserver(it) }
    }

    override fun onCameraViewStarted(width: Int, height: Int) {
        imageMat = Mat(height, width, CvType.CV_8UC4)
    }

    override fun onCameraViewStopped() {
        imageMat.release()
    }

    override fun onCameraFrame(inputFrame: CameraBridgeViewBase.CvCameraViewFrame?): Mat {
        val rgba = inputFrame?.rgba() ?: return Mat()

//        Doesn't work:
//        Core.rotate(rgba, rgba, Core.ROTATE_90_CLOCKWISE)

        imageMat = rgba

        val qrCodeDetector = QRCodeDetector()
        val qrCodes = MatOfRotatedRect()
        qrCodeDetector.detect(inputFrame.gray(), qrCodes)

        if (!qrCodes.empty()) {
            val img = inputFrame.gray()
            val qrCodeString = qrCodeDetector.decode(img, qrCodes)
            if (idsToScooters != null && idsToScooters!!.keys.contains(qrCodeString) && !hasScanned) {
                onScooterScan(qrCodeString)
            }
        }


        return rgba
    }

    private fun startCamera() {
        // Setup the OpenCV camera view.
        binding.cameraView.apply {
            visibility = SurfaceView.VISIBLE
            setCameraPermissionGranted()
            setCvCameraViewListener(this@QrScannerDialogFragment)
        }

        // Initialize the callback from OpenCV Manager to handle the OpenCV library.
        loaderCallback = object : BaseLoaderCallback(requireContext()) {
            override fun onManagerConnected(status: Int) {
                when (status) {
                    SUCCESS -> binding.cameraView.enableView()
                    else -> super.onManagerConnected(status)
                }
            }
        }

        if (!OpenCVLoader.initDebug())
            OpenCVLoader.initAsync(
                OpenCVLoader.OPENCV_VERSION,
                requireContext(), loaderCallback
            )
        else
            loaderCallback!!.onManagerConnected(LoaderCallbackInterface.SUCCESS)
    }

    private fun onScooterScan(scooterId: String) {
        hasScanned = true
        CoroutineScope(Dispatchers.Main).launch {
            val onOkClick = {
                val ride = Ride(scooterId)
                firebaseManager.startRide(ride)
                startCurrentRideActivity()

            }
            val onCancelClick = {
                hasScanned = false
            }
            requireContext().createDialog(
                "Start Ride",
                "Start ride with scooter ${idsToScooters!![scooterId]?.name}?",
                onOkClick,
                onCancelClick
            )
        }
    }

    private fun startCurrentRideActivity() {
        val intent = Intent(requireContext(), CurrentRideActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}