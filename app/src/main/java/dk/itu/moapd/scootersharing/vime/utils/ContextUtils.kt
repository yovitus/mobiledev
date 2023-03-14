package dk.itu.moapd.scootersharing.vime.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.createDialog(
    title: String,
    message: String,
    onOkClick: () -> Unit
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
            dialog.dismiss()
        }
    builder.create().show()
}