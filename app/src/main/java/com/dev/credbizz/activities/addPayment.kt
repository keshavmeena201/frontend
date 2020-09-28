package com.dev.credbizz.activities

import android.R.attr.bitmap
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.dev.credbizz.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import com.google.zxing.WriterException
import kotlinx.android.synthetic.main.activity_add_payment.*
import me.dm7.barcodescanner.zxing.ZXingScannerView
import java.util.jar.Manifest


class addPayment : AppCompatActivity() ,ZXingScannerView.ResultHandler {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_payment)
        setScannerProperties()
        contactsImageView.setOnClickListener {
            contactListShow()
        }
        editTextNumber.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s==null || s.length==0){
                    contactsImageView.setImageResource(R.drawable.contact)
                    contactsImageView.setOnClickListener {
                        contactListShow()
                    }
                }
                else{
                    contactsImageView.setImageResource(R.drawable.send)
                    contactsImageView.setOnClickListener {
                        finishByEditText()
                    }
                }
            }

        })
    }

    fun contactListShow(){
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }
    fun finishByEditText(){
        val number = editTextNumber.text.toString()
        if(number.length!=10){
            Toast.makeText(this,"Wrong Number",Toast.LENGTH_SHORT).show()
        }
        else{
            val intent = Intent()
            intent.putExtra("name",number)
            intent.putExtra("number",number)
            this.setResult(Activity.RESULT_OK,intent)
            finish()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==1){
            if(data!=null){
                val uri = data!!.data
                val cursor1: Cursor?
                val cursor2: Cursor?
                val TempNameHolder: String
                var TempNumberHolder: String
                val TempContactID: String
                var IDresult = ""
                val IDresultHolder: Int

                cursor1 = contentResolver.query(uri!!, null, null, null, null)

                if (cursor1!!.moveToFirst()) {
                    TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID))
                    IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                    IDresultHolder = Integer.valueOf(IDresult)
                    if (IDresultHolder == 1) {
                        cursor2 = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID,
                            null,
                            null
                        )
                        if (cursor2!!.moveToNext()) {
                            TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            Log.i("tag name", TempNameHolder)
                            Log.i("tag number",TempNumberHolder)
                            val intent = Intent()
                            intent.putExtra("name",TempNameHolder)
                            intent.putExtra("number",TempNumberHolder)
                            this.setResult(Activity.RESULT_OK,intent)
                            finish()
                        }
                    }
                }
            }
        }
    }
    private fun setScannerProperties() {
        qrCodeScanner.setFormats(listOf(BarcodeFormat.QR_CODE))
        qrCodeScanner.setAutoFocus(true)
        qrCodeScanner.setLaserColor(R.color.colorAccent)
        qrCodeScanner.setMaskColor(R.color.colorAccent)
        if (Build.MANUFACTURER.equals("HUAWEI", ignoreCase = true))
            qrCodeScanner.setAspectTolerance(0.5f)
    }
    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 123)
                return
            }
        }
        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
    }
    override fun onPause() {
        super.onPause()
        qrCodeScanner.stopCamera()
    }
    override fun handleResult(rawResult: Result?) {
        val result = rawResult?.text.toString()
        if(result.length>10){
            Log.i("name",result.subSequence(10,result.length).toString())
            Log.i("number",result.subSequence(0,10).toString())

            val intent = Intent()
            intent.putExtra("name",result.subSequence(10,result.length).toString())
            intent.putExtra("number",result.subSequence(0,10).toString())
            this.setResult(Activity.RESULT_OK,intent)
            finish()
        }
        qrCodeScanner.resumeCameraPreview(this)
    }
}