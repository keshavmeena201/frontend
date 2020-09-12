package com.dev.credbizz.extras

interface Keys {
    companion object {

        const val DBVERSION: Int = 1
        // Please change your ip address here for testing
        const val privacyUrl : String = "https://credbizz.com/privacy-policy/"
        const val BASEURL : String = "https://upbeat-palace-288813.el.r.appspot.com/"
        const val authorization : String = "authorization"
        const val tbl_contacts : String = "tbl_contacts"
        const val dynamicLinkPageUrl : String = "https://credbizz.com/"
        const val dynamicLinkPageTransactionLink : String = "https://credbizz.page.link"
        const val dynamicLinkPageShareLink : String = "https://credbizz.page.link"

        // SERVICE URLS
        const val ServiceGetOtp = "sendOtp"
        const val ServiceVerifyOtp = "validateotp/"

        const val profileupdate = "profileUpdate"
        const val addTransaction = "addBill"
        const val applicationId = "123456789"

        //transaction URLS
        const val getTransactions = "getallTransactions/"
        const val getProfileScore = "getScore/"
        const val getProfile = "getProfile/"
        const val settleUp = "settleUp"
        const val getAllProfiles = "getAllProfiles"

        // REQUEST PARAMETERS
        const val phoneNumber = "phoneNumber"

        // RESPONSE PARAMETERS
        const val message = "message"
        const val error = "error"

        const val REQUEST_CAMERA: Int = 456
        const val SELECT_FILE = 789
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 101

        const val rupeeSymbol : String = "₹"

        const val id : String = "id"
        const val contact_id : String = "contact_id"
        const val contact_name : String = "contact_name"
        const val contact_alpha : String = "contact_alpha"
        const val contact_number : String = "contact_number"
        const val contact_credit_score : String = "contact_credit_score"
        const val is_credit_buzz_user : String = "is_credit_buzz_user"
        const val contact_selected : String = "contact_selected"
        const val transaction_type : String = "transaction_type"


    }
}