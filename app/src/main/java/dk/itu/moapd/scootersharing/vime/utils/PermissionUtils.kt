package dk.itu.moapd.scootersharing.vime.utils

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.requestUserPermissions(permissions: Array<String>, onGranted: () -> Unit = {}, onNotGranted: () -> Unit = {}) {
    val permissionsToRequest = permissionsToRequest(permissions)

    if (permissionsToRequest.size > 0) {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val allGranted = perms.all { it.value }
            if (allGranted)
                onGranted()
            else
                onNotGranted()
        }
        requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
    }
}

private fun Fragment.permissionsToRequest(permissions: Array<String>): ArrayList<String> {
    val result: ArrayList<String> = ArrayList()

    for (permission in permissions)
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        )
            result.add(permission)

    return result
}