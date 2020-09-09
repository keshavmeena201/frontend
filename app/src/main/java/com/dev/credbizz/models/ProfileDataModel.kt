package com.dev.credbizz.models

import java.util.*

class ProfileDataModel {
    var mobileNumber  : String = ""
    var orgName : String = ""
    var creditScore : Int = 0
    var amountToGive : Double = 0.0
    var amountToPay : Int = 0

    constructor()
    constructor(
        mobileNumber: String,
        orgName: String,
        creditScore: Int,
        amountToGive: Double,
        amountToPay: Int
    ) {
        this.mobileNumber = mobileNumber
        this.orgName = orgName
        this.creditScore = creditScore
        this.amountToGive = amountToGive
        this.amountToPay = amountToPay
    }
}