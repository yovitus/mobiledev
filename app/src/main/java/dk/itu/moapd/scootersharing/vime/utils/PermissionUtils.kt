package dk.itu.moapd.scootersharing.vime.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Utility function for getting a request to get user permissions in a fragment.
 * @param permissions Permissions to request for.
 * @param onGranted Function to call if the permission is granted.
 * @param onNotGranted Function to call if the permission is not granted.
 * @return A function to call when you wish to request for the specified user permissions. If no
 * user request is needed, because they already are granted, 'onGranted' is returned.
 */
fun Fragment.getRequestUserPermissions(
    permissions: Array<String>,
    onGranted: () -> Unit = {},
    onNotGranted: () -> Unit = {}
): () -> Unit {
    val permissionsToRequest = permissionsToRequest(requireContext(), permissions)

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
        return { requestPermissionLauncher.launch(permissionsToRequest.toTypedArray()) }
    }
    return onGranted
}

/**
 * Utility function for getting a request to get user permissions in an activity.
 * @param permissions Permissions to request for.
 * @param onGranted Function to call if the permission is granted.
 * @param onNotGranted Function to call if the permission is not granted.
 * @return A function to call when you wish to request for the specified user permissions. If no
 * user request is needed, because they already are granted, 'onGranted' is returned.
 */
fun AppCompatActivity.getRequestUserPermissions(
    permissions: Array<String>,
    onGranted: () -> Unit = {},
    onNotGranted: () -> Unit = {}
): () -> Unit {
    val permissionsToRequest = permissionsToRequest(this, permissions)

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
        return { requestPermissionLauncher.launch(permissionsToRequest.toTypedArray()) }
    }
    return onGranted
}

private fun permissionsToRequest(ctx: Context, permissions: Array<String>): ArrayList<String> {
    val result: ArrayList<String> = ArrayList()

    for (permission in permissions)
        if (ContextCompat.checkSelfPermission(
                ctx,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        )
            result.add(permission)

    return result
}