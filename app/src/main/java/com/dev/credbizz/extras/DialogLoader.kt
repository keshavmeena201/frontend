package com.dev.credbizz.extras

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.dev.credbizz.R


/**
 * DialogLoader will be used to show and hide Dialog with ProgressBar
 */
class DialogLoader(private val context: Context) {
    private var alertDialog: AlertDialog? = null
    private var dialog: AlertDialog.Builder? = null
    private val layoutInflater: LayoutInflater
    private fun initDialog() {
        dialog = AlertDialog.Builder(context)
        val dialogView =
            layoutInflater.inflate(R.layout.layout_progress_dialog, null)
        dialog!!.setView(dialogView)
        dialog!!.setCancelable(false)
        val dialog_progressBar =
            dialogView.findViewById<View>(R.id.dialog_progressBar) as ProgressBar
        dialog_progressBar.isIndeterminate = true
        alertDialog = dialog!!.create()
        alertDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun showProgressDialog() {
        (context as Activity).runOnUiThread {
            try {
                alertDialog!!.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun hideProgressDialog() {
        try {
            alertDialog!!.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    init {
        layoutInflater = LayoutInflater.from(context)
        initDialog()
    }
}