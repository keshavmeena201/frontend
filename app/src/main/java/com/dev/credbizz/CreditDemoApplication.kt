package com.dev.credbizz

import android.app.Application

class CreditDemoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {

        var instance: Application? = null
            private set

        val TAG: String = CreditDemoApplication::class.java.simpleName

    }

}