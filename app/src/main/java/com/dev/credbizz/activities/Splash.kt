package com.dev.credbizz.activities

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.dev.credbizz.R
import com.dev.credbizz.dbHelper.Constants
import com.dev.credbizz.extras.SessionManager
import java.util.*


class Splash : AppCompatActivity() {

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

        val constant = Constants()

        Handler(Looper.myLooper()!!).postDelayed(Runnable {
            Log.e("time", constant.isallowed().toString())
           /* if(constant.isFirst() && constant.isallowed()) {
                constant.setfirst()
                Log.e("time", constant.isallowed().toString())
                val mobileVerify = Intent(context, MobileVerify :: class.java)
                mobileVerify.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mobileVerify)
            } else {
                val dashboard = Intent(context, Dashboard :: class.java)
                dashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(dashboard)
            }*/
            val difference = 300000
            if(sessionManager.isUserFirstTime!!) {

                        sessionManager.previousLoginTime = 0
                        val mobileVerify = Intent(context, MobileVerify :: class.java)
                        mobileVerify.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(mobileVerify)
//                    } else {
//                        val dashboard = Intent(context, Dashboard :: class.java)
//                        dashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        startActivity(dashboard)
//                    }
//                } else {
//                    sessionManager.previousLoginTime = System.currentTimeMillis()
//                    val dashboard = Intent(context, MobileVerify :: class.java)
//                    dashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(dashboard)
//                }
            } else {
                if(Math.abs(System.currentTimeMillis() - sessionManager.previousLoginTime) > difference){
                    val mobileVerify = Intent(context, MobileVerify :: class.java)
                    mobileVerify.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(mobileVerify)
                } else {
                    val dashboard = Intent(context, Dashboard :: class.java)
                    dashboard.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(dashboard)
                }
//                sessionManager.previousLoginTime = System.currentTimeMillis()
//                constant.setfirst()
//                Log.e("time", constant.isallowed().toString())
//                val mobileVerify = Intent(context, MobileVerify :: class.java)
//                mobileVerify.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(mobileVerify)
            }

        }, 1000)

    }

    fun getMinuteDiff(c1 : Calendar, c2 : Calendar) : Int{
        var minute = 0
        val d1 = c1.time
        val d2 = c2.time
        val mills = d1.time - d2.time
        var min = (mills/ 60000 % 60).toInt()
        min = minute
        return  minute
    }


}