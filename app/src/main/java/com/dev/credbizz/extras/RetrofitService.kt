package com.dev.credbizz.extras

import com.dev.credbizz.models.ProfileDataModel
import com.dev.credbizz.models.ResponseDao
import com.dev.credbizz.models.TransactionModel
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.security.Key


interface RetrofitService {

    @POST(Keys.ServiceGetOtp)
    fun getOtp(@Body jsonObject: JsonObject): Call<ResponseBody>

    @GET(Keys.ServiceVerifyOtp + "{otp}/{mobileNum}" )
    fun verifyOtp(@Path("otp") otp: String, @Path("mobileNum") mobileNum: String) : Call<ResponseDao>

    @GET(Keys.getTransactions+ "{frommobileNumber}")
    fun getTransactions(@Path("frommobileNumber") frommobileNumber: String) : Call<List<TransactionModel>>

    @POST(Keys.profileupdate)
    fun updateProfile(@Body jsonObject: JsonObject): Call<String>

    @POST(Keys.addTransaction)
    fun addtransaction(@Body jsonObject: JsonObject): Call<JsonObject>

    @GET(Keys.getProfileScore+"{mobileNumber}")
    fun getProfileScore(@Path("mobileNumber") mobileNumber: String) : Call<String>

    @GET(Keys.getProfile+"{mobileNumber}")
    fun getProfile(@Path("mobileNumber") mobileNumber: String) : Call<ProfileDataModel>

    @POST(Keys.settleUp )
    fun settleUp(@Body jsonObject: TransactionModel) : Call<JsonObject>

    @GET(Keys.getAllProfiles)
    fun getAllProfiles() : Call<List<ProfileDataModel>>

}
