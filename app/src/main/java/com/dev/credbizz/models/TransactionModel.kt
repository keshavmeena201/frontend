package com.dev.credbizz.models

import java.util.*

class TransactionModel {
    var transactionId  : String = ""
    var fromMobileNumber : String = ""
    var transactionDueDate : String = ""
    var toMobileNumber : String = ""
    lateinit var transactionDate : String
    var principleAmount : Int = 0
    var settled : Boolean = false
    var partial : Boolean = false
    var nextDate : String = "2019-01-26"
    var picUrl : String = ""
    var amountPaid : Int = 0
    var oldRatio : Double = 0.0
    var scoreUpdated : Boolean = false
    var transactionType : Boolean = false
    var transactionNumber : Int = 0
    var previousTransactionId : String = ""
    var fromName : String = ""
    var toName : String = ""
    var flag : Boolean = false


    constructor()

}