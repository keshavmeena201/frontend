package com.dev.credbizz.models

import java.util.*

class TransactionModel {
    var transactionId  : String = ""
    var fromMobileNumber : String = ""
    var transactionDueDate : Date = java.sql.Date.valueOf("2019-01-26")
    var toMobileNumber : String = ""
    var transactionDate : Date = java.sql.Date.valueOf("2019-01-26")
    var principleAmount : Int = 0
    var settled : Boolean = false
    var partial : Boolean = false
    var nextDate : String = "2019-01-26"
    var picUrl : String = ""
    var amountPaid : Int = 0
    var oldRatio : Double = 0.0
    var scoreUpdated : Boolean = false
    var transactionNumber : Int = 0
    var previousTransactionId : String = ""

    constructor()
    constructor(
        transactionId: String,
        fromMobileNumber: String,
        toMobileNumber: String,
        transactionDueDate: Date,
        transactionDate: Date,
        principleAmount: Int,
        settled: Boolean,
        partial: Boolean,
        picUrl: String,
        amountPaid: Int,
        oldRatio: Double,
        scoreUpdated: Boolean,
        transactionNumber: Int,
        previousTransactionId: String
    ) {
        this.transactionId = transactionId
        this.fromMobileNumber = fromMobileNumber
        this.transactionDueDate = transactionDueDate
        this.toMobileNumber = toMobileNumber
        this.transactionDate = transactionDate
        this.principleAmount = principleAmount
        this.settled = settled
        this.partial = partial
        this.nextDate = nextDate
        this.picUrl = picUrl
        this.amountPaid = amountPaid
        this.oldRatio = oldRatio
        this.transactionNumber = transactionNumber
        this.scoreUpdated = scoreUpdated
        this.previousTransactionId = previousTransactionId
    }

}