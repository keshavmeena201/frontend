package com.dev.credbizz.models

class ContactModel {
    var id  : Int = 0
    var contactId : String = ""
    var contactName : String = ""
    var contactNumber : String = ""
    var contactCreditScore : String = ""
    var contactNameAlpha : String = ""
    var isCreditBuzzUser : Boolean = false
    var isContactSelected : Boolean = false
    var transactionType : Int = 0

    constructor()
    constructor(
        id: Int,
        contactId: String,
        contactName: String,
        contactNumber: String,
        contactCreditScore: String,
        contactNameAlpha: String,
        isCreditBuzzUser: Boolean,
        isContactSelected: Boolean,
        transactionType: Int
    ) {
        this.id = id
        this.contactId = contactId
        this.contactName = contactName
        this.contactNumber = contactNumber
        this.contactCreditScore = contactCreditScore
        this.contactNameAlpha = contactNameAlpha
        this.isCreditBuzzUser = isCreditBuzzUser
        this.isContactSelected = isContactSelected
        this.transactionType = transactionType
    }


}