package com.dev.credbizz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.credbizz.R
import com.dev.credbizz.models.ContactModel


class ContactsAdapter(val context: Context, val contactsModels : ArrayList<ContactModel>, var onContactSelectListener : OnContactSelectListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return contactListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return contactsModels.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is contactListViewHolder){

            val contactsViewHolder = holder
            contactsViewHolder.txContactAlpha.text = contactsModels[position].contactNameAlpha
            contactsViewHolder.txContactName.text = contactsModels[position].contactName
            contactsViewHolder.txContactNumber.text = contactsModels[position].contactNumber
            //contactsViewHolder.txContactCreditScore.text = contactsModels[position].contactCreditScore

            if (contactsModels[position].isCreditBuzzUser){
                contactsViewHolder.ivAppIcon.visibility = View.VISIBLE
               //contactsViewHolder.txContactCreditScore.visibility = View.VISIBLE
            } else {
                contactsViewHolder.ivAppIcon.visibility = View.GONE
                //contactsViewHolder.txContactCreditScore.visibility = View.GONE
            }

            contactsViewHolder.itemView.setOnClickListener {
                onContactSelectListener.onContactSelect(position)
            }
        }

    }

    inner class contactListViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var txContactAlpha : TextView = itemView.findViewById(R.id.tx_contact_alpha)
        var txContactName : TextView = itemView.findViewById(R.id.tx_contact_name)
        var txContactNumber : TextView = itemView.findViewById(R.id.tx_contact_number)
        var txContactCreditScore : TextView = itemView.findViewById(R.id.tx_contact_credit_score)
        var ivAppIcon : ImageView = itemView.findViewById(R.id.iv_app_icon)

    }

    interface OnContactSelectListener {
        fun onContactSelect(pos: Int)
    }
}