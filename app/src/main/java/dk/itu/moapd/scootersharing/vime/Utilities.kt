package dk.itu.moapd.scootersharing.vime

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Context.hideKeyboard(view: View) {
    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun createDialog(
    context: Context,
    title: String,
    message: String,
    onOkClick: () -> Unit
) {
    val builder = AlertDialog.Builder(context)
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