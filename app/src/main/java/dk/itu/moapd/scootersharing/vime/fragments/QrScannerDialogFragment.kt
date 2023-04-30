package dk.itu.moapd.scootersharing.vime.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dk.itu.moapd.scootersharing.vime.R
import dk.itu.moapd.scootersharing.vime.databinding.FragmentQrScannerDialogBinding
import dk.itu.moapd.scootersharing.vime.utils.requestUserPermissions
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
    private lateinit var binding: FragmentQrScannerDialogBinding
    private var loaderCallback: BaseLoaderCallback? = null
    private lateinit var imageMat: Mat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentQrScannerDialogBinding.inflate(layoutInflater, container, false)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            val permissions = arrayOf(Manifest.permission.CAMERA)
            val onGranted: () -> Unit = {
                startCamera()
            }
            val onNotGranted: () -> Unit = {
                findNavController().navigate(
                    R.id.action_qrScannerFragment_to_home
                )
            }
            requestUserPermissions(permissions, onGranted, onNotGranted)
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

    override fun onDestroy() {
        super.onDestroy()
        binding.cameraView.disableView()
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

//        // draw QR code rectangles on the camera frame
//        qrCodes.toArray().forEach { rect ->
//            Imgproc.rectangle(rgba, rect.boundingRect(), Scalar(255.0, 0.0, 0.0), 2)
//        }

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
}