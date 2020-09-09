package com.dev.credbizz.extras

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.FileProvider
import com.dev.credbizz.CreditDemoApplication
import com.dev.credbizz.R


import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0$seconds"
        } else {
            secondsString = "" + seconds
        }

        finalTimerString = "$finalTimerString$minutes:$secondsString"

        // return timer string
        return finalTimerString
    }

    /**
     * Function to get Progress percentage
     *
     * @param currentDuration
     * @param totalDuration
     */
    fun getProgressPercentage(currentDuration: Long, totalDuration: Long): Int {
        var percentage: Double? = 0.toDouble()

        val currentSeconds = (currentDuration / 1000).toInt().toLong()
        val totalSeconds = (totalDuration / 1000).toInt().toLong()

        // calculating percentage
        percentage = currentSeconds.toDouble() / totalSeconds * 100

        // return percentage
        return percentage.toInt()
    }

    /**
     * Function to change progress to timer
     *
     * @param progress      -
     * @param totalDuration returns current duration in milliseconds
     */



    fun progressToTimer(progress: Int, totalDuration: Int): Int {
        var totalDuration = totalDuration
        var currentDuration = 0
        totalDuration = totalDuration / 1000
        currentDuration = (progress.toDouble() / 100 * totalDuration).toInt()

        // return current duration in milliseconds
        return currentDuration * 1000
    }



    companion object {
        val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123
        val REQUEST_CAPTURE_IMAGE = Keys.REQUEST_CAMERA
        val REQUEST_CAPTURE_Gallery = 1
        val IMAGE_DIRECTORY = "/banajiEyecare/"
        private val screenWidth = 0
        private val screenHeight = 0

        fun contains(jsonObject: JSONObject?, key: String): Boolean {
            return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key)
        }

        fun setTextViewDrawableColor(context: Context, textView: TextView, color: Int) {
            for (drawable in textView.compoundDrawables) {
                if (drawable != null) {
                    drawable.colorFilter =
                        PorterDuffColorFilter(getColor(context, color), PorterDuff.Mode.SRC_IN)
                }
            }
        }

        public fun dp2px(resources: Resources, dp: Float): Float {
            val scale: Float = resources.getDisplayMetrics().density
            return dp * scale + 0.5f
        }

        public fun sp2px(resources: Resources, sp: Float): Float {
            val scale: Float = resources.getDisplayMetrics().scaledDensity
            return sp * scale
        }

        fun isNetworkAvailable(activity: Activity): Boolean {
            val connectivity =
                activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                val networks = connectivity.allNetworks
                var networkInfo: NetworkInfo

                for (mNetwork in networks) {
                    networkInfo = connectivity.getNetworkInfo(mNetwork)!!

                    if (networkInfo.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            } else {
                if (connectivity != null) {
                    val info = connectivity.allNetworkInfo
                    if (info != null) {
                        for (i in info.indices) {
                            if (info[i].state == NetworkInfo.State.CONNECTED) {
                                return true
                            }
                        }
                    }
                }
            }

            return false
        }

        // GET IMAGE PATH
        @SuppressLint("Recycle")
        fun getRealPathFromURI(
            uri: Uri?,
            context: Context
        ): String? {
            var idx = 0
            var cursor: Cursor? = null
            try {
                cursor = context.contentResolver.query(uri!!, null, null, null, null)
                if (cursor != null) cursor.moveToFirst()
                idx = cursor!!.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                Log.e("RealPathFromURI->", e.message!!)
            }
            return if (cursor != null) cursor.getString(idx) else ""
        }

        fun getPath(context: Context, uri: Uri): String? {
            var path: String? = ""
            val projection =
                arrayOf(MediaStore.Files.FileColumns.DATA)
            val cursor: Cursor? =
                context.contentResolver.query(uri, projection, null, null, null)
            if (cursor == null) {
                path = uri.path
            } else {
                cursor.moveToFirst()
                val column_index: Int = cursor.getColumnIndexOrThrow(projection[0])
                path = cursor.getString(column_index)
                cursor.close()
            }
            return if (path == null || path.isEmpty()) uri.path else path
        }

        fun showAlert(context: Context, message: String) {
            var isDialogShow: Int = 0
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.resources.getString(R.string.app_name))
            builder.setMessage(message)
            builder.setCancelable(false)

            builder.setPositiveButton(
                "OK"
            ) { dialog, id ->
                isDialogShow = 0
                dialog.dismiss()
            }

            val alert11 = builder.create()
            if (isDialogShow == 0) {
                alert11.show()
                isDialogShow = 1
            }

        }

        lateinit var alertDialog: AlertDialog
        lateinit var dialog: AlertDialog.Builder

        fun showAlertCustom(context: Context, message: String) {

            dialog = AlertDialog.Builder(context)
            val dialogView: View = LayoutInflater.from(context).inflate(
                R.layout.response_message_popup,
                null
            )
            dialog.setView(dialogView)
            dialog.setCancelable(true)

            val txMessage : TextView = dialogView.findViewById(R.id.tx_message)
            val txOk : TextView  = dialogView.findViewById(R.id.tx_ok)

            txMessage.text = message

            txOk.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog = dialog.create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.show()
        }

        fun showAlertCustomFinish(context: Activity, message: String) {

            dialog = AlertDialog.Builder(context)
            val dialogView: View = LayoutInflater.from(context).inflate(
                R.layout.response_message_popup,
                null
            )
            dialog.setView(dialogView)
            dialog.setCancelable(false)

            val txMessage : TextView = dialogView.findViewById(R.id.tx_message)
            val txOk : TextView  = dialogView.findViewById(R.id.tx_ok)

            txMessage.text = message

            txOk.setOnClickListener {
                context.finish()
                alertDialog.dismiss()
            }

            alertDialog = dialog.create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.show()
        }

        fun showAlertCustomRedirect(context: Activity, message: String) {

            dialog = AlertDialog.Builder(context)
            val dialogView: View = LayoutInflater.from(context).inflate(
                R.layout.response_message_popup,
                null
            )
            dialog.setView(dialogView)
            dialog.setCancelable(false)

            val txMessage : TextView = dialogView.findViewById(R.id.tx_message)
            val txOk : TextView  = dialogView.findViewById(R.id.tx_ok)

            txMessage.text = message

            txOk.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog = dialog.create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.show()
        }

        fun  getDate(time: Long) : String {
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = time
            return DateFormat.format("dd-MM-yyyy hh:mm aa", cal).toString()
        }

        fun showFinishLogin(context: Activity, message: String) {

            dialog = AlertDialog.Builder(context)
            val dialogView: View = LayoutInflater.from(context).inflate(
                R.layout.response_message_popup,
                null
            )
            dialog.setView(dialogView)
            dialog.setCancelable(false)

            val txMessage : TextView = dialogView.findViewById(R.id.tx_message)
            val txOk : TextView  = dialogView.findViewById(R.id.tx_ok)

            txMessage.text = message

            txOk.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog = dialog.create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.show()
        }



        lateinit var intent: Intent
        fun showStartActivity(context: Activity, message: String, screenName: Int) {

            dialog = AlertDialog.Builder(context)
            val dialogView: View = LayoutInflater.from(context).inflate(
                R.layout.response_message_popup,
                null
            )
            dialog.setView(dialogView)
            dialog.setCancelable(false)

            val txMessage : TextView = dialogView.findViewById(R.id.tx_message)
            val txOk : TextView  = dialogView.findViewById(R.id.tx_ok)

            txMessage.text = message

            txOk.setOnClickListener {
                context.finish()
                alertDialog.dismiss()
            }

            alertDialog = dialog.create()
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.show()
        }


        fun showAlertFinish(context: Activity, message: String) {
            var isDialogShow: Int = 0
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.resources.getString(R.string.app_name))
            builder.setMessage(message)
            builder.setCancelable(true)

            builder.setPositiveButton(
                "OK"
            ) { dialog, id ->
                isDialogShow = 0
                Handler().postDelayed(Runnable {
                    dialog.dismiss()
                    context.finish()
                }, 1000)

            }

            val alert11 = builder.create()
            if (isDialogShow == 0) {
                alert11.show()
                isDialogShow = 1
            }

        }


        fun showAlertFinishStartActivity(
            context: Activity,
            launchActivity: Activity,
            message: String
        ) {
            var isDialogShow: Int = 0
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.resources.getString(R.string.app_name))
            builder.setMessage(message)
            builder.setCancelable(true)

            builder.setPositiveButton(
                "OK"
            ) { dialog, id ->
                isDialogShow = 0
                Handler().postDelayed(Runnable {
                    dialog.dismiss()
                    context.finish()
                }, 1000)

            }

            val alert11 = builder.create()
            if (isDialogShow == 0) {
                alert11.show()
                isDialogShow = 1
            }

        }


        internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(
            this,
            color
        )

        internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(
            context.getColorCompat(
                color
            )
        )


        // GET IMAGE URI
        fun getImageUri(context: Context, image: Bitmap): Uri? {
            var path: String? = null
            try {
                val bytes = ByteArrayOutputStream()
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                path = MediaStore.Images.Media.insertImage(
                    context.contentResolver,
                    image,
                    "Title",
                    null
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return Uri.parse(path)
        }

        fun decodeSampledBitmapFromFile(
            imagePath: String?,
            reqWidth: Int, reqHeight: Int
        ): Bitmap? {

            // First decode with inJustDecodeBounds=true to check dimensions
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            //return Bitmap.createScaledBitmap(bt, reqWidth, reqHeight, false);
            return BitmapFactory.decodeFile(imagePath, options)
        }

        fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int
        ): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                    inSampleSize *= 2
                }
            }
            return inSampleSize
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun checkPermission(context: Context): Boolean {
            val currentAPIVersion = Build.VERSION.SDK_INT
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            context,
                            Manifest.permission.CAMERA
                        )
                    ) {
                        val alertBuilder = AlertDialog.Builder(context)
                        alertBuilder.setCancelable(true)
                        alertBuilder.setTitle("Permission necessary")
                        alertBuilder.setMessage("External storage permission is necessary")
                        alertBuilder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            ActivityCompat.requestPermissions(
                                context,
                                arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA
                                ),
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                            )
                        }
                        val alert = alertBuilder.create()
                        alert.show()

                    } else {
                        ActivityCompat.requestPermissions(
                            context,
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                            ),
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
                        )
                    }
                    return false
                } else {
                    return true
                }
            } else {
                return true
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun checkLocationPermission(context: Context): Boolean {
            val currentAPIVersion = Build.VERSION.SDK_INT
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) && ActivityCompat.shouldShowRequestPermissionRationale(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    ) {
                        val alertBuilder = AlertDialog.Builder(context)
                        alertBuilder.setCancelable(true)
                        alertBuilder.setTitle("Permission necessary")
                        alertBuilder.setMessage("External storage permission is necessary")
                        alertBuilder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            ActivityCompat.requestPermissions(
                                context,
                                arrayOf(Manifest.permission_group.LOCATION),
                                1
                            )
                        }
                        val alert = alertBuilder.create()
                        alert.show()

                    } else {
                        ActivityCompat.requestPermissions(
                            context,
                            arrayOf(Manifest.permission_group.LOCATION),
                            1
                        )
                    }
                    return false
                } else {
                    return true
                }
            } else {
                return true
            }
        }

        fun isarrayorObject(jsonObject: JSONObject?, key: String): Int {
            // try {

            try {
                if (jsonObject != null && jsonObject.get(key) is JSONObject && !jsonObject.isNull(
                        key
                    )
                ) {
                    return 1
                } else if (jsonObject != null && jsonObject.get(key) is JSONArray && !jsonObject.isNull(
                        key
                    )
                )
                    return 0
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return 2


        }


        fun dpToPx(context: Context, valueInDp: Float): Float {
            val metrics = context.resources.displayMetrics
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics)
        }

        fun hideSoftKeyboard(activity: Activity) {
            if (activity.currentFocus != null && activity.currentFocus!!.windowToken != null) {
                val inputMethodManager = activity.getSystemService(
                    Activity.INPUT_METHOD_SERVICE
                ) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    activity.currentFocus!!.windowToken, 0
                )
            }
        }

        fun showSoftKeyboard(activity: Activity) {
            try {
                val imm: InputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(
                    InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY
                )
            } catch (e: Exception){
                e.printStackTrace()
            }


        }

        /*public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }
*/
        //    public static MaterialDialog progress(Context context){
        //        MaterialDialog mat=new MaterialDialog.Builder(context)
        //                .title("Progress")
        //                .content("Please Wait")
        //                .progress(true, 0)
        //                .progressIndeterminateStyle(false)
        //                .show();
        //
        //        return mat;
        //    }

        fun getScreenWidth(): Int {


            val displayMetrics = DisplayMetrics()
            val windowmanager =
                CreditDemoApplication.instance!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowmanager.defaultDisplay.getMetrics(displayMetrics)

            val deviceWidth = displayMetrics.widthPixels
            val deviceHeight = displayMetrics.heightPixels
            return deviceWidth
        }

        fun getScreenHeight(): Int {


            val displayMetrics = DisplayMetrics()
            val windowmanager =
                CreditDemoApplication.instance!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowmanager.defaultDisplay.getMetrics(displayMetrics)

            val deviceWidth = displayMetrics.widthPixels
            return displayMetrics.heightPixels
        }

        fun convertDipToPixels(dips: Float): Int {
            return (dips * CreditDemoApplication.instance!!.resources.displayMetrics.density + 0.5f).toInt()
        }

        fun getFormatedTime(hour: Int, min: Int): String {
            var hour = hour
            val formatam: String
            if (hour == 0) {
                hour += 12
                formatam = "AM"
            } else if (hour == 12) {
                formatam = "PM"
            } else if (hour > 12) {
                hour -= 12
                formatam = "PM"
            } else {
                formatam = "AM"
            }


            return String.format("%02d:%02d $formatam", hour, min)
        }


        fun openCameraIntent(context: Context, act: Activity) {
            val pictureIntent = Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
            )
            if (pictureIntent.resolveActivity(context.packageManager) != null) {
                //Create a file to store the image
                var photoFile: File? = null
                try {
                    photoFile = createImageFile(context)
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    val photoURI = FileProvider.getUriForFile(
                        context,
                        context.packageName + ".provider",
                        photoFile
                    )
                    pictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        photoURI
                    )
                    act.startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE)
                }
            }
        }

        var imageFilePath: String? = ""

        @Throws(IOException::class)
        fun createImageFile(context: Context): File? {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "IMG_" + timeStamp + "_"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix  */
                storageDir /* directory*/
            )
            imageFilePath = image.absolutePath
            return image
        }

        fun saveImage(myBitmap: Bitmap, context: Context?): String? {
            val bytes = ByteArrayOutputStream()
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 70, bytes)
            try {
                //val f = File(File(Environment.getExternalStorageDirectory(), "banajiEyecare"), "profile/" + Calendar.getInstance().timeInMillis.toString() + ".jpg")
                //f.createNewFile()
                val localStorage: File = context!!.getExternalFilesDir(null)!!
                val storagePath = localStorage.absolutePath
                val rootPath = "$storagePath/banajiEyecare"
                val fileName = "/"+ Calendar.getInstance().timeInMillis.toString() + ".jpg"

                val root = File(rootPath)
                if (!root.mkdirs()) {
                    Log.i(
                        "Test",
                        "This path is already exist: " + root.absolutePath
                    )
                }

                val file = File(rootPath + fileName)
                try {
                    val permissionCheck = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        if (!file.createNewFile()) {
                            Log.i(
                                "Test",
                                "This file is already exist: " + file.absolutePath
                            )
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                val fo = FileOutputStream(file)
                    fo.write(bytes.toByteArray())
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(file.path),
                        arrayOf("image/jpeg"),
                        null
                    )
                    fo.close()
                Log.d("TAG", "File Saved::--->" + file.absolutePath)
                imageFilePath = file.absolutePath
                return imageFilePath
            } catch (e1: IOException) {
                e1.printStackTrace()
            }
            return imageFilePath
        }
    }

    }
