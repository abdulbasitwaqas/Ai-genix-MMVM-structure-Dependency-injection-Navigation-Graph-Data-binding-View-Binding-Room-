package com.jsbl.genix.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.RecyclerRedeemItemBinding
import com.jsbl.genix.model.RedeemItem
import com.jsbl.genix.model.redeem.AvailableRedeemsModelItem

/**
 * Created by Muhammad Ali on 29-Apr-20.
 * Email muhammad.ali9385@gmail.com
 */
class RedeemItemAdapter(
    val rewardList: ArrayList<AvailableRedeemsModelItem>,
    val itemCLickListener: ItemCLickListener
) :
    RecyclerView.Adapter<RedeemItemAdapter.RedeemItemViewHolder>() {
    var bindingSender: RecyclerRedeemItemBinding? = null

    inner class RedeemItemViewHolder(var view: RecyclerRedeemItemBinding) :
        RecyclerView.ViewHolder(view.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedeemItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        bindingSender =
            DataBindingUtil.inflate<RecyclerRedeemItemBinding>(
                inflater,
                R.layout.recycler_redeem_item,
                parent,
                false
            )
        return RedeemItemViewHolder(bindingSender!!)
    }

    override fun getItemCount(): Int = rewardList.size


    fun updateList(rewardList: java.util.ArrayList<AvailableRedeemsModelItem>) {
        this.rewardList.clear()
        this.rewardList.addAll(rewardList)
        notifyDataSetChanged()
    }

    fun insertItem(rewardItem: AvailableRedeemsModelItem) {
//        this.regChatItem.clear()
        this.rewardList.add(rewardItem)
        notifyItemInserted(rewardList.size - 1)
    }


    override fun onBindViewHolder(holder: RedeemItemViewHolder, position: Int) {
        holder.view.redeemItem = rewardList[position]
//        holder.view.onViewClick = this
          holder.view.item.setOnClickListener {
              itemCLickListener.onItemClick(rewardList.get(position))
          }
    }

    interface ItemCLickListener{
        fun onItemClick(availableRedeemsModelItem: AvailableRedeemsModelItem);
    }
}