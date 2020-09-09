package com.dev.credbizz.extras

import com.dev.credbizz.dbHelper.Constants
import java.util.concurrent.TimeUnit
import com.google.gson.GsonBuilder
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager


object RetrofitExtra {

    val normalInstance: retrofit2.Retrofit
        get() {
            val constant = Constants()
            val httpclient = constant.getUnsafeOkHttpClient()
            val okHttpClient = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build()

            return retrofit2.Retrofit.Builder()
                    .baseUrl(Keys.BASEURL)
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .client(httpclient)
                    .build()
        }

    val instance: retrofit2.Retrofit
        get() {
            val constant = Constants()
            val httpclient = constant.getUnsafeOkHttpClient()
            val okHttpClient = okhttp3.OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder().build()
                        chain.proceed(request)
                    }
                    .build()
            return retrofit2.Retrofit.Builder()
                    .baseUrl(Keys.BASEURL)
                    .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                    .client(httpclient)
                    .build()
        }

    fun getRetroAuthInstance(authString: String): retrofit2.Retrofit {
        val constant = Constants()
        val httpclient = constant.getUnsafeOkHttpClient()
        val okHttpClient = okhttp3.OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder().addHeader(Keys.authorization,
                        "Bearer $authString"
                    ).build()
                    chain.proceed(request)
                }
                .build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        return retrofit2.Retrofit.Builder()
                .baseUrl(Keys.BASEURL)
                .client(httpclient)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
    }
}
