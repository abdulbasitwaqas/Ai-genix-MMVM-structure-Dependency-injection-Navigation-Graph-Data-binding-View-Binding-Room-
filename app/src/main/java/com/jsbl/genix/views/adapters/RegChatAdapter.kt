package com.jsbl.genix.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.RegChatRecieverBinding
import com.jsbl.genix.databinding.RegChatSenderBinding
import com.jsbl.genix.model.RegChatItem
import com.jsbl.genix.utils.callBacks.OnViewClickListener

/**
 * Created by Muhammad Ali on 29-Apr-20.
 * Email muhammad.ali9385@gmail.com
 */
class RegChatAdapter(
    val regChatItem: ArrayList<RegChatItem>,
    val onViewClickListener: OnViewClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_SENDER = 1
        const val VIEW_RECEIVER = 2
    }

    var bindingSender: RegChatSenderBinding? = null
    var bindingReceiver: RegChatRecieverBinding? = null

    inner class SenderViewHolder(var view: RegChatSenderBinding) :
        RecyclerView.ViewHolder(view.root)

    inner class ReceiverViewHoler(var view: RegChatRecieverBinding) :
        RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_SENDER) {
            val inflater = LayoutInflater.from(parent.context)
            bindingSender =
                DataBindingUtil.inflate<RegChatSenderBinding>(
                    inflater,
                    R.layout.reg_chat_sender,
                    parent,
                    false
                )
            bindingSender?.onClickListener = onViewClickListener
            SenderViewHolder(bindingSender!!)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            bindingReceiver =
                DataBindingUtil.inflate<RegChatRecieverBinding>(
                    inflater,
                    R.layout.reg_chat_reciever,
                    parent,
                    false
                )
//            bindingReceiver?.onClickListener = onViewClickListener
            ReceiverViewHoler(bindingReceiver!!)
        }

    }

    override fun getItemCount(): Int = regChatItem.size


    override fun getItemViewType(position: Int): Int {
        return regChatItem.get(position).sender
    }

    fun updateList(chatItems: ArrayList<RegChatItem>) {
        this.regChatItem.clear()
        this.regChatItem.addAll(chatItems)
        notifyDataSetChanged()
    }

    fun insertItem(chatItem: RegChatItem) {
//        this.regChatItem.clear()
        this.regChatItem.add(chatItem)
        notifyItemInserted(regChatItem.size - 1)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SenderViewHolder)
            holder.view.regItem = regChatItem[position]
        else if (holder is ReceiverViewHoler)
            holder.view.regItem = regChatItem[position]

    }


}