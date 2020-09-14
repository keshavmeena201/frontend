package com.dev.credbizz.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dev.credbizz.R
import com.dev.credbizz.extras.SessionManager
import com.dev.credbizz.extras.Utils
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks


class Splash : AppCompatActivity() {

    // BOOLEAN
    var isDeepLink : Boolean = false

    // SESSION MANAGER
    lateinit var sessionManager: SessionManager

    // CONTEXT
    internal lateinit var context: Context

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // INIT CONTEXT
        context = this

        // INIT SESSION MANAGER
        sessionManager = SessionManager.getInstance(context)!!

        FirebaseDynamicLinks
            .getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(
                this
            ) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                isDeepLink = true
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                }
            }
            .addOnFailureListener(
                this
            ) { e -> Log.w("", "getDynamicLink:onFailure", e) }


        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            if (sessionManager.isUserFirstTime!!) {
                if (Utils.isNetworkAvailable(this)){
                    val mobileVerify = Intent(context, Dashboard::class.java)
                    mobileVerify.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mobileVerify)
                } else {
                    Utils.showAlertCustom(context, resources.getString(R.string.no_network_connected))
                }
            } else {
                if (Utils.isNetworkAvailable(this)) {
                    val dashboard = Intent(context, MobileVerify::class.java)
                    dashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(dashboard)
                }  else {
                    Utils.showAlertCustom(context, resources.getString(R.string.no_network_connected))
                }
            }

        }, 1000)

    }


}