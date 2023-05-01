package dk.itu.moapd.scootersharing.vime.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.requestUserPermissions(
    permissions: Array<String>,
    onGranted: () -> Unit = {},
    onNotGranted: () -> Unit = {}
): (() -> Unit)? {
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
    return null
}

fun AppCompatActivity.requestUserPermissions(
    permissions: Array<String>,
    onGranted: () -> Unit = {},
    onNotGranted: () -> Unit = {}
): (() -> Unit)? {
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
    return null
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