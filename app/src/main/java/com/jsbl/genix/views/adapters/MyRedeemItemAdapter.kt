package com.jsbl.genix.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.MyRedeemHeaderBinding
import com.jsbl.genix.databinding.RecyclerHistoryRedeemItemBinding
import com.jsbl.genix.databinding.RecyclerMyRedeemItemBinding
import com.jsbl.genix.databinding.RecyclerRedeemItemBinding
import com.jsbl.genix.model.redeem.AvailableRedeemsModelItem
import com.jsbl.genix.model.redeem.ReedemListActiveItem
import com.jsbl.genix.model.redeem.ReedemListInActiveItem
import com.jsbl.genix.views.fragments.RedeemFragmentDirections

/**
 * Created by Muhammad Ali on 29-Apr-20.
 * Email muhammad.ali9385@gmail.com
 */
class MyRedeemItemAdapter(
    val rewardList: ArrayList<Any?>?,
    val itemCLickListener: ItemCLickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var bindingSender: RecyclerMyRedeemItemBinding? = null
    var bindingHistory: RecyclerHistoryRedeemItemBinding? = null
    var bindingHeader: MyRedeemHeaderBinding? = null

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_REDEEM = 1
        private const val TYPE_REDEEM_HISTORY = 2
    }

    inner class RedeemItemViewHolder(var view: RecyclerMyRedeemItemBinding) :
        RecyclerView.ViewHolder(view.root)

    inner class RedeemHistoryItemViewHolder(var view: RecyclerHistoryRedeemItemBinding) :
        RecyclerView.ViewHolder(view.root)

    inner class HeaderItemViewHolder(var view: MyRedeemHeaderBinding) :
        RecyclerView.ViewHolder(view.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_REDEEM -> {
                val inflater = LayoutInflater.from(parent.context)
                bindingSender =
                    DataBindingUtil.inflate<RecyclerMyRedeemItemBinding>(
                        inflater,
                        R.layout.recycler_my_redeem_item,
                        parent,
                        false
                    )
                RedeemItemViewHolder(bindingSender!!)
            }
            TYPE_REDEEM_HISTORY -> {
                val inflater = LayoutInflater.from(parent.context)
                bindingHistory =
                    DataBindingUtil.inflate<RecyclerHistoryRedeemItemBinding>(
                        inflater,
                        R.layout.recycler_history_redeem_item,
                        parent,
                        false
                    )
                RedeemHistoryItemViewHolder(bindingHistory!!)
            }
            TYPE_HEADER -> {
                val inflater = LayoutInflater.from(parent.context)
                bindingHeader =
                    DataBindingUtil.inflate<MyRedeemHeaderBinding>(
                        inflater,
                        R.layout.my_redeem_header,
                        parent,
                        false
                    )
                HeaderItemViewHolder(bindingHeader!!)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (rewardList!!.get(position) is ReedemListActiveItem) {
            return TYPE_REDEEM
        } else if (rewardList!!.get(position) is ReedemListInActiveItem) {
            return TYPE_REDEEM_HISTORY
        } else {
            return TYPE_HEADER
        }
    }

    override fun getItemCount(): Int = rewardList!!.size


    fun updateList(rewardList: java.util.ArrayList<Any?>?) {
        this.rewardList?.clear()
        this.rewardList?.addAll(rewardList!!)
        notifyDataSetChanged()
    }

    fun insertItem(rewardItem: ReedemListActiveItem) {
//        this.regChatItem.clear()
        this.rewardList?.add(rewardItem)
        notifyItemInserted(rewardList!!.size - 1)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RedeemItemViewHolder -> {
                holder.view.redeemItem =
                    rewardList!!.get(position) as ReedemListActiveItem
                holder.itemView.setOnClickListener {
                    itemCLickListener.onItemClick(rewardList.get(position) as ReedemListActiveItem)
                }
            }
            is RedeemHistoryItemViewHolder -> {
                holder.view.redeemItem =
                    rewardList!!.get(position) as ReedemListInActiveItem
            }
            is HeaderItemViewHolder -> bindingHeader?.started?.text =
                rewardList!!.get(position) as String
            else -> throw IllegalArgumentException()
        }
    }


    interface ItemCLickListener {
        fun onItemClick(myRedeemActive: ReedemListActiveItem)
    }
}