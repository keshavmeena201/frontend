package com.dev.credbizz.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
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
import com.dev.credbizz.adapters.ContactsAdapter
import com.dev.credbizz.dbHelper.Constants
import com.dev.credbizz.dbHelper.LoadTables
import com.dev.credbizz.extras.*
import com.dev.credbizz.extras.Keys.Companion.REQUEST_CAMERA
import com.dev.credbizz.extras.Keys.Companion.SELECT_FILE
import com.dev.credbizz.models.ContactModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_add_contact.*
import kotlinx.android.synthetic.main.activity_mobile_verify.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AddContact : AppCompatActivity(), LoaderManager.LoaderCallbacks<ArrayList<ContactModel>>, ContactsAdapter.OnContactSelectListener {

    // STRING
    private var selectedDob: String = ""
    var selectedImagePath : String = ""

    // SESSION MANAGER
    lateinit var sessionManager: SessionManager

    // INTEGER
    var request: Int = 0
    var contactLoaderId : Int = 1

    // DATE
    lateinit var serverDateString : Date

    // ADAPTER
    lateinit var contactsAdapter: ContactsAdapter

    // ARRAY LIST
    var contactList : ArrayList<ContactModel> = ArrayList()
    var lstData: ArrayList<Array<String>>? = ArrayList()
    var lst : ArrayList<String> = ArrayList()

    // LOAD TABLES
    lateinit var lt: LoadTables

    // APP PERMISSIONS
    private val permit = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    // LINEAR LAYOUT
    lateinit var llNotify : LinearLayout

    // EDIT TEXT
    lateinit var edAmount : EditText

    // TEXT VIEW
    lateinit var txTxnHeader : TextView
    lateinit var txAmountHeader : TextView
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

    // DIALOG LOADER
    lateinit var dialogLoader: DialogLoader

    // CONTEXT
    internal lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)

        // INIT CONTEXT
        context = this

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestpermission()
        }

        iv_back.setOnClickListener {
            finish()
        }

        // INIT LOAD TABLES
        lt = LoadTables(context)
        lt.LoadAllTables(context)

        // INIT DIALOG LOADER
        dialogLoader = DialogLoader(context)


        // INIT SESSION MANAGER
        sessionManager = SessionManager.getInstance(context)!!

        val constant = Constants()

        // SET TOOL BAR
        tx_tool_bar.text = resources.getString(R.string.add_contact)

        // INIT RECYCLER VIEW
        rec_contacts_view.setHasFixedSize(true)
        rec_contacts_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        // INIT ARRAY LIST
        contactList = ArrayList()


        // SEARCH USER TEXT WATCHER
        ed_search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length > 1) {
                    val filteredArrayList = filter(contactList, p0.toString())
                    if (filteredArrayList.size > 0) {
                        rec_contacts_view.visibility = View.VISIBLE
                        tx_empty.visibility = View.GONE
                        contactsAdapter =
                            ContactsAdapter(context, filteredArrayList, this@AddContact)
                        rec_contacts_view.adapter = contactsAdapter
                    } else {
                        rec_contacts_view.visibility = View.VISIBLE
                        tx_empty.visibility = View.GONE
                        rec_contacts_view.visibility = View.GONE
                        tx_empty.visibility = View.VISIBLE
                    }
                } else {

                    contactsAdapter = ContactsAdapter(context, contactList, this@AddContact)
                    rec_contacts_view.adapter = contactsAdapter
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            getContactList()
        } else {
            getContactList()
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
                    getContactList()
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

    // GET LIST
    private fun getContactList(){
        try {
            if (LoadTables.getCount(Keys.tbl_contacts) > 0){
                contactList.clear()
                contactList = ArrayList()
                lstData = ArrayList()
                lstData = LoadTables.getData(Keys.tbl_contacts)
                lstData!!.forEachIndexed { index, strings ->
                    val contactModel = ContactModel()
                    contactModel.id = strings[0].toInt()
                    contactModel.contactId = strings[1]
                    contactModel.contactName = strings[2]
                    contactModel.contactNameAlpha = strings[3]
                    contactModel.contactNumber = strings[4]
                    contactModel.contactCreditScore = strings[5]
                    contactModel.isCreditBuzzUser = strings[6].toBoolean()
                    contactModel.isContactSelected = strings[7].toBoolean()
                    contactModel.transactionType = strings[8].toInt()
                    contactList.add(contactModel)
                    Log.e("test", strings[3].toString())
                }
                contactsAdapter = ContactsAdapter(context, contactList, this@AddContact)
                rec_contacts_view.adapter = contactsAdapter
            } else {
                dialogLoader.showProgressDialog()
                // START FETCHING CONTACT LIST USING LOADER
                LoaderManager.getInstance(this).initLoader(contactLoaderId, null, this)
            }
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<ArrayList<ContactModel>> {
        return ContactsLoader(context)
    }

    override fun onLoadFinished(
        loader: Loader<ArrayList<ContactModel>>,
        data: ArrayList<ContactModel>?
    ) {
        dialogLoader.hideProgressDialog()
        if (LoadTables.getCount(Keys.tbl_contacts) > 0){

            contactList.clear()
            contactList = ArrayList()
            lstData = ArrayList()
            lstData = LoadTables.getData(Keys.tbl_contacts)
            lstData!!.forEachIndexed { index, strings ->
                val contactModel = ContactModel()
                contactModel.id = strings[0].toInt()
                contactModel.contactId = strings[1]
                contactModel.contactName = strings[2]
                contactModel.contactNameAlpha = strings[3]
                contactModel.contactNumber = strings[4]
                contactModel.contactCreditScore = strings[5]
                contactModel.isCreditBuzzUser = strings[6].toBoolean()
                contactModel.isContactSelected = strings[7].toBoolean()
                contactModel.transactionType = strings[8].toInt()
                contactList.add(contactModel)
                Log.e("test", strings[3].toString())
            }
            contactsAdapter = ContactsAdapter(context, contactList, this@AddContact)
            rec_contacts_view.adapter = contactsAdapter

        } else {
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

                contactList.clear()
                contactList = ArrayList()
                lstData = ArrayList()
                lstData = LoadTables.getData(Keys.tbl_contacts)
                lstData!!.forEachIndexed { index, strings ->
                    val contactModel = ContactModel()
                    contactModel.id = strings[0].toInt()
                    contactModel.contactId = strings[1]
                    contactModel.contactName = strings[2]
                    contactModel.contactNameAlpha = strings[3]
                    contactModel.contactNumber = strings[4]
                    contactModel.contactCreditScore = strings[5]
                    contactModel.isCreditBuzzUser = strings[6].toBoolean()
                    contactModel.isContactSelected = strings[7].toBoolean()
                    contactModel.transactionType = strings[8].toInt()
                    contactList.add(contactModel)
                    Log.e("test", strings[3].toString())
                }
                contactsAdapter = ContactsAdapter(context, contactList, this@AddContact)
                rec_contacts_view.adapter = contactsAdapter
            }

        }



    }

    override fun onLoaderReset(loader: Loader<ArrayList<ContactModel>>) {

    }

    override fun onContactSelect(pos: Int) {
        showAlertCustom(context, contactList[pos].contactName,contactList[pos].contactNumber)
    }

    // SEARCH USER
    private fun filter(
        models: ArrayList<ContactModel>,
        query: String
    ): ArrayList<ContactModel> {
        //final String lowerCaseQuery = query.toLowerCase();
        val filteredModelList: ArrayList<ContactModel> = ArrayList()
        for (model in models) {
            val text = model.contactNumber.toLowerCase()
            val textUserName = model.contactName.toLowerCase()
            if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(textUserName)) {
                if (text.trim { it <= ' ' }
                        .contains(query.trim { it <= ' ' }) || textUserName.trim { it <= ' ' }
                        .contains(query.trim { it <= ' ' })) {
                    filteredModelList.add(model)
                }
            }
        }
        return filteredModelList
    }


    // SHOW PAYMENT POPUP
    @SuppressLint("SetTextI18n")
    fun showAlertCustom(context: Context, contactName: String, contactNumber : String) {

        dialog = AlertDialog.Builder(context)
        val dialogView: View = LayoutInflater.from(context).inflate(
            R.layout.add_payment_popup,
            null
        )
        dialog.setView(dialogView)
        dialog.setCancelable(true)

        val type : Int = 0

        edAmount = dialogView.findViewById(R.id.ed_amount)

        llNotify = dialogView.findViewById(R.id.ll_notify)

        txTxnHeader = dialogView.findViewById(R.id.tx_txn_header)
        txAmountHeader = dialogView.findViewById(R.id.tx_payment_status)
        txAmountDate = dialogView.findViewById(R.id.tx_payment_date)
        txAmountBills = dialogView.findViewById(R.id.tx_payment_image)
        txRupeeSymbol = dialogView.findViewById(R.id.tx_rupee_symbol)

        cbNotify = dialogView.findViewById(R.id.cb_notify)

        rgTransactionType = dialogView.findViewById(R.id.rg_action)

        btnSave = dialogView.findViewById(R.id.btn_save)

        val radioButtonID = rgTransactionType.checkedRadioButtonId
        val radioButton = rgTransactionType.findViewById(radioButtonID) as RadioButton
        val selectedText = radioButton.text as String

        txAmountHeader.text = "You gave " + Keys.rupeeSymbol + "0 to " + contactName
        txAmountHeader.setTextColor(ContextCompat.getColor(context, R.color.green))
        edAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
        txAmountDate.setTextColor(ContextCompat.getColor(context, R.color.green))
        txRupeeSymbol.setTextColor(ContextCompat.getColor(context, R.color.green))
        txAmountBills.setTextColor(ContextCompat.getColor(context, R.color.green))
        Utils.setTextViewDrawableColor(context, txAmountDate, R.color.green)
        Utils.setTextViewDrawableColor(context, txAmountBills, R.color.green)
        btnSave.setBackgroundResource(R.drawable.green_corner_bg)

        txTxnHeader.text = "Transaction with " + contactName

        // GENDER SELECT
        rgTransactionType.setOnCheckedChangeListener { radioGroup, i ->
            Utils.hideSoftKeyboard(this@AddContact)
            if (i == R.id.rb_give) {
                type == 0
                txAmountHeader.text = "You gave " + Keys.rupeeSymbol + " " + edAmount.text.toString().trim() + " to " + contactName
                txAmountHeader.setTextColor(ContextCompat.getColor(context, R.color.green))
                edAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
                txAmountDate.setTextColor(ContextCompat.getColor(context, R.color.green))
                txRupeeSymbol.setTextColor(ContextCompat.getColor(context, R.color.green))
                txAmountBills.setTextColor(ContextCompat.getColor(context, R.color.green))
                Utils.setTextViewDrawableColor(context, txAmountDate, R.color.green)
                Utils.setTextViewDrawableColor(context, txAmountBills, R.color.green)
                btnSave.setBackgroundResource(R.drawable.green_corner_bg)
            } else if (i == R.id.rb_got) {
                type == 1
                txAmountHeader.text = "You got " + Keys.rupeeSymbol + " " + edAmount.text.toString().trim() + " from " + contactName
                txAmountHeader.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                edAmount.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                txAmountDate.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                txRupeeSymbol.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                txAmountBills.setTextColor(ContextCompat.getColor(context, R.color.yellow))
                Utils.setTextViewDrawableColor(context, txAmountDate, R.color.yellow)
                Utils.setTextViewDrawableColor(context, txAmountBills, R.color.yellow)
                btnSave.setBackgroundResource(R.drawable.red_corner_bg)
            }
        }

        txAmountHeader.text = "You gave " + Keys.rupeeSymbol + "0 to " + contactName

        val dateString: String = SimpleDateFormat("dd MMM yy").format(Date())
        txAmountDate.text = dateString

        cbNotify.text = resources.getString(R.string.notify_text) + " " + contactName
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
                    txAmountHeader.text =
                        "You gave " + Keys.rupeeSymbol + " " + p0.toString() + " to " + contactName
                } else {
                    txAmountHeader.text =
                        "You got " + Keys.rupeeSymbol + " " + p0.toString() + " from " + contactName
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
            val constant = Constants()
            //addtransaction
            val reqObj = JsonObject()
            if (type == 0){
                reqObj.addProperty("fromMobileNumber", sessionManager.mobileNumber)
                reqObj.addProperty("toMobileNumber", contactNumber)
            }else{
                reqObj.addProperty("toMobileNumber", sessionManager.mobileNumber)
                reqObj.addProperty("fromMobileNumber", contactNumber)
            }
            //reqObj.addProperty("transactionDate", serverDateString.toString())

            var formatValue = edAmount.text.toString().trim()
            if (formatValue.contains(",")){
                formatValue = formatValue.replace("," , "")
            } else {
                formatValue = edAmount.text.toString().trim()
            }
            reqObj.addProperty("principleAmount",formatValue)
            addTransaction(reqObj)
            alertDialog.dismiss()
        }


        alertDialog = dialog.create()
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.window!!.attributes.windowAnimations = R.style.SlidingDialogAnimation
        alertDialog.show()
    }

    private fun addTransaction(jsonObject: JsonObject){
        try {
            if (Utils.isNetworkAvailable(this)){
                dialogLoader.showProgressDialog()
                val retrofit: Retrofit = RetrofitExtra.instance
                val apis = retrofit.create(RetrofitService::class.java)

                // REQUEST OBJECT
                val reqObj = jsonObject
                Log.e("reqObj", reqObj.toString())

                // API CALL
                apis.addtransaction(reqObj).enqueue(object : retrofit2.Callback<String>{

                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {
                        dialogLoader.hideProgressDialog()
                        try {

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
                        dialogLoader.hideProgressDialog()
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
                context,R.style.DatePickerTheme,
                DatePickerDialog.OnDateSetListener { p0, year, month, day ->
                    val newDate = Calendar.getInstance()
                    newDate.set(year, month, day)
                    val dateString: String =
                        SimpleDateFormat("dd MMM yy").format(Date(newDate.timeInMillis))
                   serverDateString = Date(newDate.timeInMillis)
                    txAmountDate.text = dateString
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
            if (requestCode == REQUEST_CAMERA || requestCode == SELECT_FILE) {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
                    if (resultCode == Activity.RESULT_OK) {
                        if (requestCode == SELECT_FILE) {
                            if (data != null) {
                                onSelectFromGalleryResult(data)
                            }
                        } else if (requestCode == REQUEST_CAMERA) {
                            if (data != null) {
                                onCaptureImageResult(data)
                            }
                        }
                    }
                } else {
                    if (requestCode == REQUEST_CAMERA) {
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
                    } else if (requestCode == SELECT_FILE) {
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

}