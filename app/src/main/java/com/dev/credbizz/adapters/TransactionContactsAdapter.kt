package com.dev.credbizz.adapters

import android.R.id.text2
import android.content.Context
import android.graphics.Color
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
import com.dev.credbizz.extras.EasyMoneyEditText
import com.dev.credbizz.extras.Keys
import com.dev.credbizz.extras.SessionManager
import com.dev.credbizz.models.TransactionModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


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
                contactssViewHolder.ivAction.setImageResource(R.drawable.ic_get)
                var maskEdit : EasyMoneyEditText = EasyMoneyEditText(context)
                maskEdit.showCurrencySymbol()
                maskEdit.setCurrency(Keys.rupeeSymbol)
                maskEdit.showCommas()
                maskEdit.setText(contactsModels[position].principleAmount.toString())
                maskEdit.setTextColor(Color.RED)

                val spannable: Spannable = SpannableString(maskEdit.text.toString())

                spannable.setSpan(
                    ForegroundColorSpan(Color.RED),
                    0,
                    maskEdit.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )


                var help = contactsModels[position].principleAmount.toString()
                help = help.replace(
                    contactsModels[position].principleAmount.toString(),
                    "<font color='#D50000'>" + Keys.rupeeSymbol + contactsModels[position].principleAmount.toString() + "</font>"
                )
                if (contactsModels[position].transactionDueDate != null) {
                    var dateStr: String = ""
                    val claimTableFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                    try {
                        val claimDate: Date? = claimTableFormat.parse(contactsModels[position].transactionDueDate)
                        val timeFormat = SimpleDateFormat("dd/MM/yyyy")
                        val finalDate = timeFormat.format(claimDate)
                        dateStr = finalDate
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    contactssViewHolder.txContactName.text =
                        "Get " + maskEdit.text.toString() + " from " + contactsModels[position].toName + " on " + dateStr
                } else {
                    contactssViewHolder.txContactName.text =
                        "Get " + maskEdit.text.toString() + " from " + contactsModels[position].toName
                }
            } else {
                contactssViewHolder.ivAction.setImageResource(R.drawable.ic_give)


                var maskEdit : EasyMoneyEditText = EasyMoneyEditText(context)
                maskEdit.showCurrencySymbol()
                maskEdit.setCurrency(Keys.rupeeSymbol)
                maskEdit.showCommas()
                maskEdit.setText(contactsModels[position].principleAmount.toString())
                maskEdit.setTextColor(Color.GREEN)
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
                if (contactsModels[position].transactionDueDate != null) {
                    var dateStr: String = ""
                    val claimTableFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                    try {
                        val claimDate: Date? =
                            claimTableFormat.parse(contactsModels[position].transactionDueDate)
                        val timeFormat = SimpleDateFormat("dd/MM/yyyy")
                        val finalDate = timeFormat.format(claimDate)
                        dateStr = finalDate
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
                    contactssViewHolder.txContactTxn.text =
                        contactsModels[position].principleAmount.toString()
                    contactssViewHolder.txContactName.setText("Pay " + maskEdit.text.toString() + " to " + contactsModels[position].fromName + " on " + dateStr)
                } else {
                    contactssViewHolder.txContactName.setText("Pay " + maskEdit.text.toString() + " to " + contactsModels[position].fromName)
                }

            }

            contactssViewHolder.btnSettleUp.setOnClickListener {
                onContactSelectListener.onSettleUpSelect(position)
            }
        }

    }

    inner class contactListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var txContactName : TextView = itemView.findViewById(R.id.tx_contact_name)
        var txContactNumber : TextView = itemView.findViewById(R.id.tx_contact_number)
        var txContactCreditScore : TextView = itemView.findViewById(R.id.tx_contact_credit_score)
        val txContactTxn : TextView = itemView.findViewById(R.id.tx_contact_txn)
        var ivAppIcon : ImageView = itemView.findViewById(R.id.iv_app_icon)
        var btnSettleUp : Button = itemView.findViewById(R.id.btn_settle_up)
        var ivAction : ImageView = itemView.findViewById(R.id.iv_action)

    }

    interface OnSettleUpSelectListener {
        fun onSettleUpSelect(pos: Int)
    }
}