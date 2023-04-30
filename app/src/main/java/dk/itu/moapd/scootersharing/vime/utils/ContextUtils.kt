package dk.itu.moapd.scootersharing.vime.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

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