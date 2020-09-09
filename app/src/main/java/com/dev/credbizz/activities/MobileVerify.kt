package com.dev.credbizz.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.dev.credbizz.R
import com.dev.credbizz.dbHelper.Constants
import com.dev.credbizz.dbHelper.LoadTables
import com.dev.credbizz.extras.*
import com.dev.credbizz.models.ContactModel
import com.dev.credbizz.models.ResponseDao
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_mobile_verify.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

class MobileVerify : AppCompatActivity()  , LoaderManager.LoaderCallbacks<ArrayList<ContactModel>> {


    // INTEGER
    var request: Int = 0
    var contactLoaderId : Int = 1
    var retryCount : Int = 0

    // APP PERMISSIONS
    private val permit = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.INTERNET
    )

    // DILAOG LOADER
    lateinit var dialogLoader : DialogLoader

    // ARRAY LIST
    var lst : ArrayList<String> = ArrayList()

    // LOAD TABLES
    lateinit var lt: LoadTables

    // SESSION MANAGER
    lateinit var sessionManager: SessionManager

    // ALERT DIALOG
    lateinit var alertDialog: AlertDialog

    // DIALOG BUILDER
    lateinit var dialog: AlertDialog.Builder

    // EDIT TEXT
    lateinit var ed1 : EditText
    lateinit var ed2 : EditText
    lateinit var ed3 : EditText
    lateinit var ed4 : EditText
    lateinit var ed5 : EditText
    lateinit var ed6 : EditText

    // IMAGE VIEW
    lateinit var ivVerified : ImageView

    // TEXT VIEW
    lateinit var txVerificationMsg : TextView

    // CONTEXT
    internal lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobile_verify)

        // INIT CONTEXT
        context = this

        // INIT SESSION MANAGER
        sessionManager = SessionManager.getInstance(context)!!

        // INIT DIALOG LOADER
        dialogLoader = DialogLoader(context)

        // INIT LOAD TABLES
        lt = LoadTables(context)
        lt.LoadAllTables(context)

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestpermission()
        } else {
            // LoaderManager.getInstance(this).initLoader(contactLoaderId, null, this)
        }
        val constant = Constants()

        // DONE BUTTON CLICK
        btn_done.setOnClickListener {
            if (ed_mobile_number.text.toString().trim().isEmpty()) {
                ed_mobile_number.error = resources.getString(R.string.error_empty_mobile)
                ed_mobile_number.isFocusable = true
            } else if (!ValidationInputs.isValidNumber(ed_mobile_number.text.toString().trim())) {
                ed_mobile_number.error = resources.getString(R.string.error_valid_mobile)
                ed_mobile_number.isFocusable = true
            } else {
                Utils.hideSoftKeyboard(this)
                Log.e("flag", constant.isFirst().toString())
                getOtp()
                //constant.setfirst()
                sessionManager.mobileNumber = ed_mobile_number.text.toString()
                sessionManager.isUserFirstTime = true
                showAlertCustom(context)
            }


            //showAlertCustom(context)
        }
    }


    fun showAlertCustom(context: Context) {

        dialog = AlertDialog.Builder(context)
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.verify_otp_popup, null)
        dialog.setView(dialogView)
        dialog.setCancelable(true)

        var constants = Constants()
        constants.setMobileNum(ed_mobile_number.text.toString().trim())
        System.out.println("set the " + constants.getMobileNum())

        ed1 = dialogView.findViewById(R.id.ed_1)
        ed2 = dialogView.findViewById(R.id.ed_2)
        ed3 = dialogView.findViewById(R.id.ed_3)
        ed4 = dialogView.findViewById(R.id.ed_4)
        ed5 = dialogView.findViewById(R.id.ed_5)
        ed6 = dialogView.findViewById(R.id.ed_6)

        ivVerified = dialogView.findViewById(R.id.iv_verified)

        txVerificationMsg = dialogView.findViewById(R.id.tx_verify_label)

//        Handler(Looper.myLooper()!!).postDelayed(Runnable {
//            ed1.setText("1")
//            ed2.setText("2")
//            ed3.setText("3")
//            ed4.setText("4")
//            ed5.setText("5")
//            ed6.setText("6")




//            Handler(Looper.myLooper()!!).postDelayed(Runnable {

//            }, 2500)

//        }, 1500)

        initEditTextListener()

        alertDialog = dialog.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.attributes.windowAnimations = R.style.SlidingDialogAnimation
        alertDialog.show()
    }

    private fun initEditTextListener() {
        ed1.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (ed1.text.toString().length == 1) {
                    ed2.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        ed2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (ed2.text.toString().trim().length == 1) {
                    ed3.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ed2.text.toString().trim().isEmpty()) {
                    ed1.requestFocus()
                }
            }
        })

        ed3.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (ed3.text.toString().trim().length == 1) {
                    ed4.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ed3.text.toString().trim().isEmpty()) {
                    ed2.requestFocus()
                }
            }
        })

        ed4.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (ed4.text.toString().trim().length == 1) {
                    ed5.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ed4.text.toString().trim().isEmpty()) {
                    ed3.requestFocus()
                }
            }
        })

        ed5.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (ed5.text.toString().trim().length == 1) {
                    ed6.requestFocus()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ed5.text.toString().trim().isEmpty()) {
                    ed4.requestFocus()
                }
            }
        })

        ed6.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (ed6.text.toString().trim().length == 1) {
                    val string : String = ed1.text.toString().trim() + ed2.text.toString().trim() + ed3.text.toString().trim() + ed4.text.toString().trim() + ed5.text.toString().trim()+ ed6.text.toString().trim()
                    val constant = Constants()

                    verifyOtp(string, constant.mobileNum)
                    constant.settime()

//                    val createOrg = Intent(context, CreateOrganization :: class.java)
//                    createOrg.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(createOrg)
//                    alertDialog.dismiss()

                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ed6.text.toString().trim().isEmpty()) {
                    ed5.requestFocus()
                }
            }
        })
    }


    // SMS LISTENER
    private fun smsListener(){
        val client = SmsRetriever.getClient(context)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {

        }
        task.addOnFailureListener {

        }
    }


    // REQUEST PERMISSIONS
    fun requestpermission() {
        ActivityCompat.requestPermissions(this, permit, request)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //val permissionLocation = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
        if (grantResults.isNotEmpty()) {
            if (requestCode == request) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (LoadTables.getCount(Keys.tbl_contacts) > 0){

                    } else {
                        // START FETCHING CONTACT LIST USING LOADER
                        LoaderManager.getInstance(this).initLoader(contactLoaderId, null, this)
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(
                        context,
                        resources.getString(R.string.denied_permission),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<ArrayList<ContactModel>> {
        return ContactsLoader(context)
    }

    override fun onLoadFinished(
        loader: Loader<ArrayList<ContactModel>>,
        data: ArrayList<ContactModel>?
    ) {

        if (data!!.size > 0){
            LoadTables.deleteTable(Keys.tbl_contacts)
            lst.clear()
            lst = ArrayList()
            for (i in 0 until data.size) {
                lst = ArrayList()
                lst.add(data[i].id.toString())
                lst.add(data[i].contactId)
                lst.add(data[i].contactName)
                lst.add(data[i].contactNameAlpha)
                lst.add(data[i].contactNumber)
                lst.add(data[i].contactCreditScore)
                lst.add(data[i].isCreditBuzzUser.toString())
                lst.add(data[i].isContactSelected.toString())
                lst.add(data[i].transactionType.toString())
                lt.insertData(Keys.tbl_contacts, Keys.id, lst, LoadTables.saveContacts());
            }
        }
    }

    override fun onLoaderReset(loader: Loader<ArrayList<ContactModel>>) {

    }


    // GET OTP API
    private fun getOtp(){
        try {
            if (Utils.isNetworkAvailable(this)){
                dialogLoader.showProgressDialog()
                val retrofit: Retrofit = RetrofitExtra.instance
                val apis = retrofit.create(RetrofitService::class.java)

                // REQUEST OBJECT
                val reqObj = JsonObject()
                reqObj.addProperty(Keys.phoneNumber, ed_mobile_number.text.toString().trim())
                Log.e("reqObj", reqObj.toString())

                // API CALL
                apis.getOtp(reqObj).enqueue(object : retrofit2.Callback<String>{

                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {
                        dialogLoader.hideProgressDialog()
                       // showAlertCustom(context)
                        try {
                            Log.e("getOtpResp", response.toString())

                            if (response.code() == 200) {

                                val responseString = response.body().toString()
                                Log.e("getOtpResp", responseString)

                                //val status = responseObject.getBoolean(Keys.status)

                            } else {
                                try {
                                    val responseError = response.errorBody()?.string()
                                    val gson = Gson()
                                    val adapter = gson.getAdapter(JsonObject::class.java)
                                    if (response.errorBody() != null) {
                                        val registerResponse = adapter.fromJson(responseError)
                                        if (registerResponse.has(Keys.error)){
                                           // Utils.showAlertCustom(context, registerResponse.get(Keys.error).asString)
                                        }
                                    }
                                } catch (e : Exception) {
                                    e.printStackTrace()
                                }
                            }

                        } catch (e : Exception){
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.e("getOtpResp", t.toString())
                        dialogLoader.hideProgressDialog()
                      //  showAlertCustom(context)
                        t.printStackTrace()
                    }
                })
            } else {
                Utils.showAlertCustom(context, resources.getString(R.string.no_network_connected))
            }
        } catch (e : Exception){
            e.printStackTrace()
        }

    }

    // VERIFY OTP
    private fun verifyOtp(otp : String, mobileNum : String){
        try {
            if (Utils.isNetworkAvailable(this)){
                val retrofit: Retrofit = RetrofitExtra.normalInstance
                val apis = retrofit.create(RetrofitService::class.java)

                // REQUEST OBJECT

                // API CALL
                apis.verifyOtp(otp, mobileNum).enqueue(object : retrofit2.Callback<ResponseDao>{

                    override fun onResponse(
                        call: Call<ResponseDao>,
                        response: Response<ResponseDao>
                    ) {

                        //showAlertCustom(context)
                        try {
                            Log.e("getOtpResplolsws", response.toString())

                            if (response.code() == 200) {
                                sessionManager.previousLoginTime = System.currentTimeMillis()
                                sessionManager.isUserFirstTime = false
                                sessionManager.mobileNumber = ed_mobile_number.text.toString()
                                ivVerified.visibility = View.VISIBLE
                                txVerificationMsg.visibility = View.VISIBLE
                                sessionManager.previousLoginTime = System.currentTimeMillis()
                                sessionManager.isUserFirstTime = false
                                redirectToNextScreen()

                                val responseString = response.body().toString()
                                Log.e("getOtpResp", responseString)

                                val responseObject = JSONObject(responseString)
                                //val status = responseObject.getBoolean(Keys.status)

//                                Handler(Looper.myLooper()!!).postDelayed({
//                                    val intent =
//                                        Intent(this@MobileVerify, CreateOrganization::class.java)
//                                    startActivity(intent)
//                                    finish()
//                                }, 1000)

                            } else {
                                try {
                                    val responseError = response.errorBody()?.string()
                                    val gson = Gson()
                                    val adapter = gson.getAdapter(JsonObject::class.java)
                                    if (response.errorBody() != null) {
                                        val registerResponse = adapter.fromJson(responseError)
                                        if (registerResponse.has(Keys.error)){
                                            // Utils.showAlertCustom(context, registerResponse.get(Keys.error).asString)
                                        }
                                    }
                                } catch (e : Exception) {
                                    e.printStackTrace()
                                }
                            }

                        } catch (e : Exception){
                            e.printStackTrace()
                        }
                    }

                    override fun onFailure(call: Call<ResponseDao>, t: Throwable) {
                        ivVerified.visibility = View.VISIBLE
                        txVerificationMsg.visibility = View.VISIBLE
                        //redirectToNextScreen()
                        //showAlertCustom(context)
                        t.printStackTrace()
                    }
                })
            } else {
                Utils.showAlertCustom(context, resources.getString(R.string.no_network_connected))
            }
        } catch (e : Exception){
            e.printStackTrace()
        }

    }

    // REDIRECT TO CREATE ORGANIZATION
    fun redirectToNextScreen(){
        Handler(Looper.myLooper()!!).postDelayed({
            val intent =
                Intent(this@MobileVerify, CreateOrganization::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }


}