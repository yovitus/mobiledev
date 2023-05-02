package dk.itu.moapd.scootersharing.vime.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat

/**
 * Context extension function for creating and showing a dialog.
 * @param title The title of the dialog.
 * @param message The message the dialog should display.
 * @param onOkClick The function to be called if the user presses 'OK'.
 * @param onCancelClick The function to be called if the user presses 'Cancel'.
 */
fun Context.createDialog(
    title: String,
    message: String,
    onOkClick: () -> Unit,
    onCancelClick: () -> Unit = {}
) {
    val builder = AlertDialog.Builder(this)
    builder.setTitle(title)
        .setCancelable(false)
        .setMessage(message)
        .setPositiveButton("OK") { dialog, _ ->
            onOkClick()
            dialog.dismiss()
        }
        .setNegativeButton("Cancel") { dialog, _ ->
            onCancelClick()
            dialog.dismiss()
        }
    builder.create().show()
}

/**
 * Context extension function for converting a vector drawable to a bitmap.
 * @param drawableId The id of the drawable that should be converted.
 * @return The drawable as a Bitmap object.
 */
fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(this, drawableId)
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}