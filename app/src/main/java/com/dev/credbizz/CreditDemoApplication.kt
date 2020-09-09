package com.dev.credbizz

import android.app.Application
import com.google.firebase.FirebaseApp

class CreditDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        FirebaseApp.initializeApp(applicationContext);
    }

    companion object {

        var instance: Application? = null
            private set

        val TAG: String = CreditDemoApplication::class.java.simpleName

    }

}