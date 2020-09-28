package com.dev.credbizz.extras
import android.content.Context
import android.content.SharedPreferences
import com.dev.credbizz.dbHelper.Constants
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


class SessionManager(_context: Context) {

    // Shared Preferences
    var pref: SharedPreferences

    // Editor for Shared preferences
    var editor: SharedPreferences.Editor

    // Shared pref mode
    var PRIVATE_MODE = 0


    fun clearData() { // Clearing all data from Shared Preferences
        editor.clear()
        editor.commit()
    }

    var firebaseToken : String?
        get() = pref.getString(FIREBASE_TOKEN, "")
        set(token) {
            editor.putString(FIREBASE_TOKEN, token)
            editor.commit()
        }


    var name : String?
        get() = pref.getString(NAME, "")
        set(name) {
            editor.putString(NAME, name)
            editor.commit()
        }

    var firstName : String?
        get() = pref.getString(FIRST_NAME, "")
        set(name) {
            editor.putString(FIRST_NAME, name)
            editor.commit()
        }

    var lastName : String?
        get() = pref.getString(LAST_NAME, "")
        set(name) {
            editor.putString(LAST_NAME, name)
            editor.commit()
        }


    var email : String?
        get() = pref.getString(EMAIL, "")
        set(email) {
            editor.putString(EMAIL, email)
            editor.commit()
        }



    var provider : String?
        get() = pref.getString(PROVIDER, "")
        set(provider) {
            editor.putString(PROVIDER, provider)
            editor.commit()
        }


    var providerId : String?
        get() = pref.getString(PROVIDER_ID, "")
        set(providerId) {
            editor.putString(PROVIDER_ID, providerId)
            editor.commit()
        }


    var accessToken : String?
        get() = pref.getString(ACCESS_TOKEN, "")
        set(accessToken) {
            editor.putString(ACCESS_TOKEN, accessToken)
            editor.commit()
        }



    var currentNewsLink : String?
        get() = pref.getString(CURRENT_NEWS_LINK, "")
        set(link) {
            editor.putString(CURRENT_NEWS_LINK, link)
            editor.commit()
        }

    var previousParent : Int?
        get() = pref.getInt(PREVIOUS_PARENT_POSITION, -1)
        set(link) {
            editor.putInt(PREVIOUS_PARENT_POSITION, link!!)
            editor.commit()
        }

    var profilePic : String?
        get() = pref.getString(PROFILE_PIC, "")
        set(profilePic) {
            editor.putString(PROFILE_PIC, profilePic)
            editor.commit()
        }

    var gender : String?
        get() = pref.getString(GENDER, "")
        set(gender) {
            editor.putString(GENDER, gender)
            editor.commit()
        }

    var isUserVerified : Boolean?
        get() = pref.getBoolean(IS_USER_VERIFIED, false)
        set(isUserVerified) {
            editor.putBoolean(IS_USER_VERIFIED, isUserVerified!!)
            editor.commit()
        }

    var isUserFirstTime : Boolean?
        get() = pref.getBoolean(IS_USER_FIRST_TIME, true)
        set(isUserFirstTime) {
            editor.putBoolean(IS_USER_FIRST_TIME, isUserFirstTime!!)
            editor.commit()
        }

    var previousLoginTime : Long
        get() = pref.getLong(PREVIOUS_LOGIN_TIME, 0)
        set(isUserVerified) {
            editor.putLong(PREVIOUS_LOGIN_TIME, isUserVerified)
            editor.commit()
        }

    var orgName : String?
        get() = pref.getString(ORG_NAME, "")
        set(id) {
            editor.putString(ORG_NAME, id)
            editor.commit()
        }

    var mobileNumber : String?
        get() = pref.getString(MOBILE_NUMBER, "")
        set(id) {
            editor.putString(MOBILE_NUMBER, id)
            editor.commit()
        }

    var isUserAllowed : Boolean?
        get() = pref.getBoolean(IS_USER_ALLOWED, false)
        set(isUserVerified) {
            val currenttime = LocalDateTime.now()
            val minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), currenttime)
            val time = System.currentTimeMillis()
            println("lolololo$minutes")
            var isAllowed : Boolean = false
            isAllowed = Math.abs(minutes) < 36000
            editor.putBoolean(IS_USER_ALLOWED, isAllowed!!)
            editor.commit()
        }

    var isOtpVerified : Boolean?
        get() = pref.getBoolean(IS_OTP_VERIFIED, false)
        set(isOtpVerified) {
            editor.putBoolean(IS_OTP_VERIFIED, isOtpVerified!!)
            editor.commit()
        }

    var userId : String?
        get() = pref.getString(USER_ID, "")
        set(id) {
            editor.putString(USER_ID, id)
            editor.commit()
        }

    var state : String?
        get() = pref.getString(STATE, "")
        set(state) {
            editor.putString(STATE, state)
            editor.commit()
        }

    var city : String?
        get() = pref.getString(CITY, "")
        set(city) {
            editor.putString(CITY, city)
            editor.commit()
        }

    companion object {
        private var sessionManager: SessionManager? = null

        // Sharedpref file name
        private const val PREF_NAME = "SportolicPref"

        // All Shared Preferences Keys
        private const val IS_USER_VERIFIED = "is_user_verified"
        private const val IS_OTP_VERIFIED = "is_otp_verified"
        private const val FIREBASE_TOKEN = "firebase_token"
        private const val NAME = "name"
        private const val FIRST_NAME = "first_name"
        private const val LAST_NAME = "last_name"
        private const val EMAIL = "email"
        private const val GENDER = "gender"
        private const val PROFILE_PIC = "profile_pic"
        private const val PROVIDER = "provider"
        private const val PROVIDER_ID = "provider_id"
        private const val ACCESS_TOKEN = "access_token"
        private const val MOBILE_NUMBER = "mobile_number"
        private const val CURRENT_NEWS_LINK = "current_news_link"
        private const val PREVIOUS_PARENT_POSITION = "previous_parent_poistion"
        private const val USER_ID = "user_id"
        private const val STATE = "state"
        private const val CITY = "city"
        private const val IS_USER_FIRST_TIME = "is_first_time_user"
        private const val IS_USER_ALLOWED = "is_user_allowed"
        private const val PREVIOUS_LOGIN_TIME = "previous_login_time"
        private const val ORG_NAME = "org_name"
        private const val MY_MOBILE_NO = "my_mobile_no"

        @Synchronized
        fun getInstance(context: Context): SessionManager? {
            if (sessionManager == null) {
                /**
                 * Use application Context to prevent leak.
                 */
                sessionManager = SessionManager(context.applicationContext)
            }
            return sessionManager
        }
    }

    // Constructor
    init {
        pref = _context.getSharedPreferences(
            PREF_NAME,
            PRIVATE_MODE
        )
        editor = pref.edit()
    }
}