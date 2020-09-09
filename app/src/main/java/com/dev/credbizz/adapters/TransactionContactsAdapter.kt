package com.dev.credbizz.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dev.credbizz.R
import com.dev.credbizz.dbHelper.Constants
import com.dev.credbizz.extras.SessionManager
import com.dev.credbizz.models.ContactModel
import com.dev.credbizz.models.TransactionModel


class TransactionContactsAdapter(val context: Context, val contactsModels : ArrayList<TransactionModel>, var onContactSelectListener : OnSettleUpSelectListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_contact, parent, false)
        return contactListViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.e("item count", contactsModels.size.toString())
        return contactsModels.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val constant = Constants()
        var sessionManager = SessionManager.getInstance(context)!!
        Log.e("lolerror","contactsModels[position].fromMobileNumber")
        if (holder is contactListViewHolder){

            val contactssViewHolder = holder
            contactssViewHolder.txContactAlpha.text = sessionManager.orgName
            contactssViewHolder.txContactName.text = contactsModels[position].fromMobileNumber
            //contactssViewHolder.txContactNumber.text = contactsModels[position].contactNumber
            contactssViewHolder.txContactCreditScore.text = "600"
            contactssViewHolder.txContactCreditScore.visibility = View.VISIBLE

            if (contactsModels[position].settled){
                contactssViewHolder.ivAppIcon.visibility = View.VISIBLE
            } else {
                contactssViewHolder.ivAppIcon.visibility = View.GONE
            }

            if (contactsModels[position].fromMobileNumber == constant.getMobileNum()){
                contactssViewHolder.txContactTxn.setTextColor(ContextCompat.getColor(context, R.color.red))
                contactssViewHolder.txContactTxn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_give, 0, 0, 0)
                contactssViewHolder.txContactTxn.text = contactsModels[position].principleAmount.toString()
                contactssViewHolder.txContactName.text = contactsModels[position].fromMobileNumber + " gave to "
            } else {
                contactssViewHolder.txContactTxn.setTextColor(ContextCompat.getColor(context, R.color.green))
                contactssViewHolder.txContactTxn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_get, 0, 0, 0)
                contactssViewHolder.txContactTxn.text = contactsModels[position].principleAmount.toString()
                contactssViewHolder.txContactName.text = contactsModels[position].fromMobileNumber + " will give you "
            }

            contactssViewHolder.btnSettleUp.setOnClickListener {
                onContactSelectListener.onSettleUpSelect(position)
            }
        }

    }

    inner class contactListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var txContactAlpha : TextView = itemView.findViewById(R.id.tx_contact_alpha)
        var txContactName : TextView = itemView.findViewById(R.id.tx_contact_name)
        var txContactNumber : TextView = itemView.findViewById(R.id.tx_contact_number)
        var txContactCreditScore : TextView = itemView.findViewById(R.id.tx_contact_credit_score)
        val txContactTxn : TextView = itemView.findViewById(R.id.tx_contact_txn)
        var ivAppIcon : ImageView = itemView.findViewById(R.id.iv_app_icon)
        var btnSettleUp : Button = itemView.findViewById(R.id.btn_settle_up)

    }

    interface OnSettleUpSelectListener {
        fun onSettleUpSelect(pos: Int)
    }
}