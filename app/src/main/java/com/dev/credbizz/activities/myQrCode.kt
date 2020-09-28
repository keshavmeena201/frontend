package com.dev.credbizz.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.fragment.app.FragmentActivity
import com.dev.credbizz.R
import com.dev.credbizz.extras.QRCodeHelper
import com.dev.credbizz.extras.SessionManager
import com.google.zxing.WriterException
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import kotlinx.android.synthetic.main.activity_my_qr_code.*

class myQrCode : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_qr_code)
        val sessionManager = SessionManager(this)
        Log.i("mobile number",sessionManager.mobileNumber.toString())

        val bitmap = QRCodeHelper
            .newInstance(this)
            .setContent(sessionManager.mobileNumber.toString())
            .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
            .setMargin(2)
            .qrcOde

        QRimageView.setImageBitmap(bitmap)
    }
}