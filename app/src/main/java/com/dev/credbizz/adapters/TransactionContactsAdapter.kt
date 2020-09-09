package com.dev.credbizz.adapters

import android.content.Context
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
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
import com.dev.credbizz.extras.Keys
import com.dev.credbizz.extras.SessionManager
import com.dev.credbizz.models.TransactionModel


class TransactionContactsAdapter(
    val context: Context,
    val contactsModels: ArrayList<TransactionModel>,
    var onContactSelectListener: OnSettleUpSelectListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        Log.e("lolerror", "contactsModels[position].fromMobileNumber")
        if (holder is contactListViewHolder){

            val contactssViewHolder = holder

            contactssViewHolder.txContactName.text = contactsModels[position].fromMobileNumber
            //contactssViewHolder.txContactNumber.text = contactsModels[position].contactNumber
            contactssViewHolder.txContactCreditScore.text = "600"
            contactssViewHolder.txContactCreditScore.visibility = View.VISIBLE

            if (contactsModels[position].settled){
                contactssViewHolder.ivAppIcon.visibility = View.VISIBLE
            } else {
                contactssViewHolder.ivAppIcon.visibility = View.GONE
            }

            if (contactsModels[position].fromMobileNumber == sessionManager.mobileNumber){
                contactssViewHolder.txContactAlpha.text = sessionManager.mobileNumber.toString().substring(0, 1)
                var help = contactsModels[position].principleAmount.toString()
                help = help.replace(contactsModels[position].principleAmount.toString(), "<font color='#D50000'>" + Keys.rupeeSymbol + contactsModels[position].principleAmount.toString() + "</font>")
                val preString = "Pay " +  Html.fromHtml(help) +  " to " +contactsModels[position].fromMobileNumber
                val ss = SpannableString(preString)
                val d = ContextCompat.getDrawable(context, R.drawable.ic_give)
                val span = ImageSpan(d!!, ImageSpan.ALIGN_BASELINE)
                ss.setSpan(span, 0, preString.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                contactssViewHolder.txContactName.text = ss.toString()
            } else {
                contactssViewHolder.txContactAlpha.text = contactsModels[position].fromMobileNumber.toString().substring(0, 1)
                var help = contactsModels[position].principleAmount.toString()
                help = help.replace(
                    contactsModels[position].principleAmount.toString(),
                    "<font color='#43A047'>" + Keys.rupeeSymbol + contactsModels[position].principleAmount.toString() + "</font>"
                )
                contactssViewHolder.txContactTxn.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
                contactssViewHolder.txContactTxn.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_get,
                    0,
                    0,
                    0
                )
                contactssViewHolder.txContactTxn.text = contactsModels[position].principleAmount.toString()
                contactssViewHolder.txContactName.text = contactsModels[position].fromMobileNumber + " will give you "
            }

            contactssViewHolder.btnSettleUp.setOnClickListener {
                onContactSelectListener.onSettleUpSelect(position)
            }
        }

    }

    inner class contactListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
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