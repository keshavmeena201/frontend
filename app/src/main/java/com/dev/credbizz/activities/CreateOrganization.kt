package com.dev.credbizz.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.dev.credbizz.R
import com.dev.credbizz.dbHelper.Constants
import com.dev.credbizz.dbHelper.LoadTables
import com.dev.credbizz.extras.*
import com.dev.credbizz.models.ContactModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_create_organization.*
import kotlinx.android.synthetic.main.activity_mobile_verify.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit

class CreateOrganization : AppCompatActivity() {



    // SESSION MANAGER
    lateinit var sessionManager: SessionManager

    // CONTEXT
    internal lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_organization)

        // INIT CONTEXT
        context = this

        // INIT SESSION MANAGER
        sessionManager = SessionManager.getInstance(context)!!


        // START BUTTON CLICK
        btn_start.setOnClickListener {
            sessionManager.orgName = ed_name.text.toString()
            val constant = Constants()
            constant.setOrgname(ed_name.text.toString().trim())
            sessionManager.orgName = ed_name.text.toString().trim()
            //send req to profile controller
            updateProfile(constant.getorgName(), constant.getMobileNum())
            val dashboard = Intent(context, Dashboard :: class.java)
            dashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(dashboard)
        }




    }

    private fun updateProfile(orgname : String, mobileNum : String){
        try {
            if (Utils.isNetworkAvailable(this)){
                val retrofit: Retrofit = RetrofitExtra.normalInstance
                val apis = retrofit.create(RetrofitService::class.java)

                // REQUEST OBJECT
                val reqObj = JsonObject()
                reqObj.addProperty("mobileNumber", mobileNum)
                reqObj.addProperty("orgName", orgname)
                Log.e("reqObj", reqObj.toString())

                // API CALL
                apis.updateProfile(reqObj).enqueue(object : retrofit2.Callback<String>{

                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {

                        try {
                            Log.e("getOtpResp", response.toString())

                            if (response.code() == 200) {

                                val responseString = response.body().toString()
                                Log.e("getOtpResp", responseString)


                                //profile updated

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



}