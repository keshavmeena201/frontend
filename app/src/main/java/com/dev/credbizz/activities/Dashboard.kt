package com.dev.credbizz.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.credbizz.R
import com.dev.credbizz.adapters.TransactionContactsAdapter
import com.dev.credbizz.dbHelper.Constants
import com.dev.credbizz.dbHelper.LoadTables
import com.dev.credbizz.extras.*
import com.dev.credbizz.models.ContactModel
import com.dev.credbizz.models.ProfileDataModel
import com.dev.credbizz.models.TransactionModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_mobile_verify.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Dashboard : AppCompatActivity() ,  TransactionContactsAdapter.OnSettleUpSelectListener{


    private var selectedDob: String = ""
    var selectedImagePath : String = ""

    // LINEAR LAYOUT
    lateinit var llNotify : LinearLayout

    // EDIT TEXT
    lateinit var edAmount : EditText

    // TEXT VIEW
    //lateinit var txAmountHeader : TextView
    lateinit var txRepaymentDateLabel : TextView
    lateinit var txAmountDate : TextView
    lateinit var txAmountBills : TextView
    lateinit var txRupeeSymbol : TextView
    lateinit var txSendMessage : TextView
    lateinit var txSendWhatsApp : TextView

    // RADIO GROUP
    lateinit var rgTransactionType : RadioGroup

    // CHECK BOX
    lateinit var cbNotify : CheckBox

    // BUTTON
    lateinit var btnSave : Button

    // ALERT DIALOG
    lateinit var alertDialog: AlertDialog

    // DIALOG BUILDER
    lateinit var dialog: AlertDialog.Builder

    // ADAPTER
    lateinit var transactionContactsAdapter : TransactionContactsAdapter

    // ARRAY LIST
    var transactionList : ArrayList<ContactModel> = ArrayList()

    var profileScore : Double = 0.0

    var transactionList1 : ArrayList<TransactionModel> = ArrayList()


    // LOAD TABLES
    lateinit var lt: LoadTables

    // SESSION MANAGER
    lateinit var sessionManager: SessionManager

    // CONTEXT
    internal lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // INIT CONTEXT
        context = this

        // INIT SESSION MANAGER
        sessionManager = SessionManager.getInstance(context)!!

        // INIT LOAD TABLES
        //contacts storing
        lt = LoadTables(context)
        lt.LoadAllTables(context)

        val constant = Constants()
        getProfileScore(sessionManager.mobileNumber!!)
        // SET PROGRESS
        setScoreProgress(600.0)

        // INIT RECYCLER VIEW
        rec_transaction_details.setHasFixedSize(true)
        rec_transaction_details.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )
        //fetch transaction from backend
        // INIT ARRAY LIST
        //for (i in 0 until 15)
        val retrofit: Retrofit = RetrofitExtra.instance
        val apis = retrofit.create(RetrofitService::class.java)
        Log.i("lolwa",constant.getMobileNum())
        if(constant.getMobileNum()== "9106729587") {
            Log.e("lol","lol1")
        }
        getTransactions(sessionManager.mobileNumber!!, this)
//        Log.e("transactione", constant.getAllTransactons().get(0).fromMobileNumber)

        transactionContactsAdapter = TransactionContactsAdapter(context, constant.allTransactons, this)
        rec_transaction_details.adapter = transactionContactsAdapter


        //}

        // INIT ADAPTER

        // ADD PAYMENT CLICK
        btn_add_payment.setOnClickListener {
            val addContact = Intent(context, AddContact::class.java)
            startActivity(addContact)
        }

        // EDIT COMPANY NAME
        tx_company_name.setOnClickListener {
            showEditNamePopup()
        }


    }

    override fun onResume() {
        super.onResume()
        if (sessionManager.orgName != ""){
            tx_company_name.text = sessionManager.orgName
        }
        val constant = Constants()
        getProfileScore(sessionManager.mobileNumber!!)
        getTransactions(sessionManager.mobileNumber!!, this)
    }

    // SET PROGRESS
    private fun setScoreProgress(score : Double){
        circular_progress.maxProgress = 1065.0
        circular_progress.setCurrentProgress(score)
        circular_progress.setShouldDrawDot(true)

        circular_progress.progress // returns 5000
        circular_progress.maxProgress // returns 10000
    }

    private fun getProfileScore(mobileNum : String){
        try {
            if (Utils.isNetworkAvailable(this)){
                val retrofit: Retrofit = RetrofitExtra.instance
                val apis = retrofit.create(RetrofitService::class.java)

                // REQUEST OBJECT
                val reqObj = JsonObject()


                // API CALL
                apis.getProfile(mobileNum).enqueue(object : retrofit2.Callback<ProfileDataModel>{

                    override fun onResponse(
                        call: Call<ProfileDataModel>,
                        response: Response<ProfileDataModel>
                    ) {
                        try {
                            Log.e("getOtpRespprofile", response.toString())
                            if (response.code() == 200) {
                                Log.e("reqObj", response.raw().toString())
                                val result : ProfileDataModel;

                                val responseString = response.body().toString()
                                result = response.body()!!
                                Log.e("getOtpRespprofile", responseString)

                                setScoreProgress(result.creditScore.toDouble())
                                val constant = Constants()
                                constant.setamountToGive(result.amountToGive.toInt())
                                constant.setamountToPay(result.amountToPay.toInt())
                                tx_get_txn.text = result.amountToPay.toString()
                                tx_give_txn.text = result.amountToGive.toString()


                            } else {
                                try {
                                    val responseError = response.errorBody()?.string()
                                    val gson = Gson()
                                    val adapter = gson.getAdapter(JsonObject::class.java)
                                    Log.e("gettransactionResp", response.message())
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

                    override fun onFailure(call: Call<ProfileDataModel>, t: Throwable) {
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

    private fun getTransactions(mobileNum : String, onContactSelectListener : TransactionContactsAdapter.OnSettleUpSelectListener){
        try {
            if (Utils.isNetworkAvailable(this)){
                val retrofit: Retrofit = RetrofitExtra.instance
                val apis = retrofit.create(RetrofitService::class.java)

                // REQUEST OBJECT
                val reqObj = JsonObject()


                // API CALL
                apis.getTransactions(mobileNum).enqueue(object : retrofit2.Callback<List<TransactionModel>>{

                    override fun onResponse(
                        call: Call<List<TransactionModel>>,
                        response: Response<List<TransactionModel>>
                    ) {
                        try {
                            Log.e("getOtpResp", response.toString())
                            if (response.code() == 200) {
                                Log.e("reqObj", response.raw().toString())

                                val responseString = response.body().toString()
                                Log.e("getOtpResp", responseString)

//                                val gson = Gson()
//                                val itemType = object : TypeToken<List<TransactionModel>>() {}.type
                                val transactions1 : List<TransactionModel>
                                var count = 0;
                                transactions1 = response.body()!!
                                val constant = Constants()
                                constant.cleartransaction()
                                transactionList1.clear()
                                for(transaction in transactions1) {
                                    transactionList1.add(count,transaction)
                                    constant.addTransaction(transaction)
                                    count.inc()
                                }
                                Log.e("transactione", transactionList1.get(0).fromMobileNumber)
                                transactionContactsAdapter = TransactionContactsAdapter(context, transactionList1, onContactSelectListener)
                                rec_transaction_details.adapter = transactionContactsAdapter

                                //lol=transactionList1

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
                                    Log.e("gettransactionResp", response.message())
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

                    override fun onFailure(call: Call<List<TransactionModel>>, t: Throwable) {
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




    override fun onSettleUpSelect(pos: Int) {
        showAlertCustom(context, transactionList1[pos])
    }
    // SHOW PAYMENT POPUP
    @SuppressLint("SetTextI18n")
    fun showAlertCustom(context: Context, transactionModel: TransactionModel) {

        dialog = AlertDialog.Builder(context)
        val dialogView: View = LayoutInflater.from(context).inflate(
            R.layout.settle_up_popup,
            null
        )
        dialog.setView(dialogView)
        dialog.setCancelable(true)

        val type : Int = 0

        edAmount = dialogView.findViewById(R.id.ed_amount)

        llNotify = dialogView.findViewById(R.id.ll_notify)

        //txAmountHeader = dialogView.findViewById(R.id.tx_payment_status)
        txAmountDate = dialogView.findViewById(R.id.tx_payment_date)
        txAmountBills = dialogView.findViewById(R.id.tx_payment_image)
        txRupeeSymbol = dialogView.findViewById(R.id.tx_rupee_symbol)
        txRepaymentDateLabel = dialogView.findViewById(R.id.tx_repayment_label)

        cbNotify = dialogView.findViewById(R.id.cb_notify)

        rgTransactionType = dialogView.findViewById(R.id.rg_action)

        btnSave = dialogView.findViewById(R.id.btn_save)

        val radioButtonID = rgTransactionType.checkedRadioButtonId
        val radioButton = rgTransactionType.findViewById(radioButtonID) as RadioButton
        val selectedText = radioButton.text as String


        // GENDER SELECT
        rgTransactionType.setOnCheckedChangeListener { radioGroup, i ->
            Utils.hideSoftKeyboard(this@Dashboard)
            if (i == R.id.rb_give) {
                type == 0
                transactionModel.settled = true
                //txAmountHeader.text = "You got " + Keys.rupeeSymbol + "0 from " + contactName
                //txAmountHeader.setTextColor(ContextCompat.getColor(context, R.color.green))
                edAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
                txAmountDate.setTextColor(ContextCompat.getColor(context, R.color.green))
                txRupeeSymbol.setTextColor(ContextCompat.getColor(context, R.color.green))
                txAmountBills.setTextColor(ContextCompat.getColor(context, R.color.green))
                txRepaymentDateLabel.setTextColor(ContextCompat.getColor(context, R.color.green))
                txAmountDate.visibility = View.GONE
                txRepaymentDateLabel.visibility = View.GONE
                Utils.setTextViewDrawableColor(context, txAmountDate, R.color.green)
                Utils.setTextViewDrawableColor(context, txAmountBills, R.color.green)
                btnSave.setBackgroundResource(R.drawable.green_corner_bg)
            } else if (i == R.id.rb_got) {
                type == 1
                transactionModel.partial = true
                //txAmountHeader.text = "You gave " + Keys.rupeeSymbol + "0 to " + contactName
                //txAmountHeader.setTextColor(ContextCompat.getColor(context, R.color.red))
                edAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
                txAmountDate.setTextColor(ContextCompat.getColor(context, R.color.green))
                txRupeeSymbol.setTextColor(ContextCompat.getColor(context, R.color.green))
                txAmountBills.setTextColor(ContextCompat.getColor(context, R.color.green))
                txRepaymentDateLabel.setTextColor(ContextCompat.getColor(context, R.color.green))
                txAmountDate.visibility = View.VISIBLE
                txRepaymentDateLabel.visibility = View.VISIBLE
                Utils.setTextViewDrawableColor(context, txAmountDate, R.color.green)
                Utils.setTextViewDrawableColor(context, txAmountBills, R.color.green)
                btnSave.setBackgroundResource(R.drawable.green_corner_bg)
            }
        }

        //txAmountHeader.text = "You gave " + Keys.rupeeSymbol + "0 to " + contactName

        val dateString: String = SimpleDateFormat("yyyy-MM-dd").format(Date())
        txAmountDate.text = dateString

        cbNotify.text = resources.getString(R.string.notify_text) + " " + transactionModel.fromMobileNumber
        cbNotify.setOnCheckedChangeListener { compoundButton, b ->
            if (b){
                llNotify.visibility = View.VISIBLE
            } else {
                llNotify.visibility = View.GONE
            }
        }

        val font: Typeface = Typeface.createFromAsset(assets, "fonts/montserrat_regular.ttf")
        edAmount.typeface = font

        edAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (type == 0) {
                    // txAmountHeader.text =
                    "You gave " + Keys.rupeeSymbol + " " + p0.toString() + " to " + transactionModel.fromMobileNumber
                } else {
                    // txAmountHeader.text = "You got " + Keys.rupeeSymbol + " " + p0.toString() + " from " + contactName
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        txAmountDate.setOnClickListener {
            showDatePickerDialog()
        }

        txAmountBills.setOnClickListener {
            attachBills()
        }

        btnSave.setOnClickListener {
            transactionModel.amountPaid = edAmount.text.toString().trim().toInt()
            transactionModel.nextDate = SimpleDateFormat("yyyy-MM-dd").format(Date())
            settleUp(transactionModel)
            //send addtransaction request
            alertDialog.dismiss()
        }


        alertDialog = dialog.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.attributes.windowAnimations = R.style.SlidingDialogAnimation
        alertDialog.show()
    }


    private fun settleUp(transactionModel: TransactionModel){
        try {
            if (Utils.isNetworkAvailable(this)){
                val retrofit: Retrofit = RetrofitExtra.instance
                val apis = retrofit.create(RetrofitService::class.java)

                // REQUEST OBJECT


                // API CALL
                apis.settleUp(transactionModel).enqueue(object : retrofit2.Callback<String>{

                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {
                        try {
                            Log.e("getOtpResp", response.toString())
                            if (response.code() == 200) {
                                Log.e("reqObj", response.raw().toString())


                            } else {
                                try {
                                    val responseError = response.errorBody()?.string()
                                    val gson = Gson()
                                    val adapter = gson.getAdapter(JsonObject::class.java)
                                    Log.e("gettransactionResp", response.message())
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



    // SHOW DATE PICKER DIALOG
    fun showDatePickerDialog() {
        try {
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val day = c[Calendar.DAY_OF_MONTH]

            val datePicker: DatePickerDialog = DatePickerDialog(
                context, R.style.DatePickerTheme,
                DatePickerDialog.OnDateSetListener { p0, year, month, day ->
                    val newDate = Calendar.getInstance()
                    newDate.set(year, month, day)
                    val dateString: String =
                        SimpleDateFormat("dd MMM yy").format(Date(newDate.timeInMillis))
                    val serverDateString: String = SimpleDateFormat("yyyy-MM-dd").format(
                        Date(
                            newDate.timeInMillis
                        )
                    )
                    txAmountDate.text = dateString
                    selectedDob = serverDateString
                }, year, month, day
            )


            datePicker.setTitle(resources.getString(R.string.select_date))
            datePicker.datePicker.minDate = System.currentTimeMillis()
            datePicker.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // ATTACH BILLS DIALOG
    fun attachBills(){
        var alertDialog : AlertDialog? = null
        val dialog = AlertDialog.Builder(context)
        val dialogView: View = LayoutInflater.from(context).inflate(
            R.layout.attach_bills_popup,
            null
        )
        dialog.setView(dialogView)
        dialog.setCancelable(true)


        val txTakePicture : TextView = dialogView.findViewById(R.id.tx_take_picture)
        val txOpenGallery : TextView = dialogView.findViewById(R.id.tx_open_gallery)


        val result = Utils.checkPermission(this)

        txTakePicture.setOnClickListener {
            if (result) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                    cameraIntent()
                } else {
                    Utils.openCameraIntent(this, this)
                }
                alertDialog!!.dismiss()
            }
        }

        txOpenGallery.setOnClickListener {
            if (result) {
                galleryIntent()
                alertDialog!!.dismiss()
            }
        }


        alertDialog = dialog.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.attributes.windowAnimations = R.style.SlidingDialogAnimation
        alertDialog.show()
    }

    // OPEN CAMERA INTENT
    private fun cameraIntent() {
        requestedOrientation = resources.configuration.orientation
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, Keys.REQUEST_CAMERA)
    }

    // OPEN GALLERY INTENT
    private fun galleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, Keys.SELECT_FILE)
    }

    // GET GALLERY IMAGE RESULT
    private fun onSelectFromGalleryResult(data: Intent) {
        var bm: Bitmap? = null
        try {
            try {
                bm =
                    Utils.decodeSampledBitmapFromFile(
                        Utils.getRealPathFromURI(data.data, this),
                        200,
                        200
                    )

            } catch (e: IOException) {
                e.printStackTrace()
            }
            val compressBitmap = compressImage(bm!!)
            val tempUri: Uri = Utils.getImageUri(this, compressBitmap!!)!!
            val s = Utils.getRealPathFromURI(tempUri, this)
            selectedImagePath = s!!
        } catch (e: IOException) {
            Log.e("OnSelectGallery->", e.printStackTrace().toString())
        } catch (e: Exception) {
            Log.e("OnSelectGallery->", e.printStackTrace().toString())
        }
    }

    // GET CAMERA CAPTURE RESULT
    private fun onCaptureImageResult(data: Intent) {
        val thumbnail = data.extras!!.get("data") as Bitmap
        val bytes = ByteArrayOutputStream()

        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        val compressBitmap = compressImage(thumbnail)
        val tempUri = Utils.getImageUri(this, compressBitmap!!)
        val s = Utils.getRealPathFromURI(tempUri, this)
        selectedImagePath = s!!
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Keys.REQUEST_CAMERA || requestCode == Keys.SELECT_FILE) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                    if (resultCode == Activity.RESULT_OK) {
                        if (requestCode == Keys.SELECT_FILE) {
                            if (data != null) {
                                onSelectFromGalleryResult(data)
                            }
                        } else if (requestCode == Keys.REQUEST_CAMERA) {
                            if (data != null) {
                                onCaptureImageResult(data)
                            }
                        }
                    }
                } else {
                    if (requestCode == Keys.REQUEST_CAMERA) {
                        val imagePath = Utils.imageFilePath
                        //selectedImagePath = imagePath!!

                        val bmOptions = BitmapFactory.Options()
                        bmOptions.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(imagePath, bmOptions)
                        val photoW = bmOptions.outWidth
                        val photoH = bmOptions.outHeight

                        // Determine how much to scale down the image
                        val scaleFactor = Math.min(photoW / 100, photoH / 100)

                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false
                        bmOptions.inSampleSize = scaleFactor
                        bmOptions.inPurgeable = true

                        val bitmap = BitmapFactory.decodeFile(imagePath, bmOptions)

                        try {
                            selectedImagePath = Utils.saveImage(bitmap, context)!!
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else if (requestCode == Keys.SELECT_FILE) {
                        if (data != null) {
                            val contentURI: Uri = data.data!!
                            try {
//                                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, contentURI)
//                                val path: String  = Utils.getRealPathFromURI(contentURI, this)!!

                                val bitmap = MediaStore.Images.Media.getBitmap(
                                    contentResolver,
                                    contentURI
                                )
                                val path: String = Utils.getPath(context, contentURI)!!
                                selectedImagePath = Utils.saveImage(bitmap, context)!!
                                //selectedImagePath = path

                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun compressImage(image: Bitmap): Bitmap? {
        val baos = ByteArrayOutputStream()
        image.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            baos
        ) //Compression quality, here 100 means no compression, the storage of compressed data to baos
        var options = 90
        while (baos.toByteArray().size / 1024 > 1000) {  //Loop if compressed picture is greater than 400kb, than to compression
            baos.reset() //Reset baos is empty baos
            image.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                baos
            ) //The compression options%, storing the compressed data to the baos
            options -= 10 //Every time reduced by 10
        }
        val isBm = ByteArrayInputStream(baos.toByteArray()) //The storage of compressed data in the baos to ByteArrayInputStream
        return BitmapFactory.decodeStream(isBm, null, null)
    }

    // SHOW EDIT NAME POPUP
    private fun showEditNamePopup(){
        try {
            var alertDialog : AlertDialog? = null
            val dialog = AlertDialog.Builder(context)
            val dialogView: View = LayoutInflater.from(context).inflate(
                R.layout.org_name_popup,
                null
            )
            dialog.setView(dialogView)
            dialog.setCancelable(true)

            val btnSave : Button = dialogView.findViewById(R.id.btn_save)
            val edCompanyName : EditText = dialogView.findViewById(R.id.ed_name)

            if (tx_company_name.text.toString() != "Name"){
                edCompanyName.setText(tx_company_name.text.toString())
            }

            btnSave.setOnClickListener {
                if (edCompanyName.text.toString().isNotEmpty()) {
                    sessionManager.orgName = edCompanyName.text.toString()
                    tx_company_name.text = edCompanyName.text.toString()
                    edCompanyName.setText("")
                    alertDialog!!.dismiss()
                } else {
                    edCompanyName.error = resources.getString(R.string.error_name)
                    edCompanyName.isFocusable = true
                }
            }

            alertDialog = dialog.create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.window!!.attributes.windowAnimations = R.style.SlidingDialogAnimation
            alertDialog.show()


        } catch (e: Exception){
            e.printStackTrace()
        }
    }

}