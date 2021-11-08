package com.jsbl.genix.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.RecyclerRewardItemNewBinding
import com.jsbl.genix.trips.TripItem
import com.jsbl.genix.views.fragments.RewardsFragmentDirections

/**
 * Created by Muhammad Ali on 29-Apr-20.
 * Email muhammad.ali9385@gmail.com
 */
class RewardItemAdpterUnUsed(
    val rewardList: ArrayList<TripItem>
) :
    RecyclerView.Adapter<RewardItemAdpterUnUsed.RewardItemViewHolder>() {
    var bindingSender: RecyclerRewardItemNewBinding? = null

    inner class RewardItemViewHolder(var view: RecyclerRewardItemNewBinding) :
        RecyclerView.ViewHolder(view.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        bindingSender =
            DataBindingUtil.inflate<RecyclerRewardItemNewBinding>(
                inflater,
                R.layout.recycler_reward_item_new,
                parent,
                false
            )
        return RewardItemViewHolder(bindingSender!!)
    }

    override fun getItemCount(): Int = rewardList.size


    fun updateList(rewardList: ArrayList<TripItem>) {
        this.rewardList.clear()
        this.rewardList.addAll(rewardList)
        notifyDataSetChanged()
    }

    fun insertItem(rewardItem: TripItem) {
//        this.regChatItem.clear()
        this.rewardList.add(rewardItem)
        notifyItemInserted(rewardList.size - 1)
    }


    override fun onBindViewHolder(holder: RewardItemViewHolder, position: Int) {
        holder.view.reward = rewardList[position]
        /* if(position==0){
             holder.view.heading.visible()
             holder.view.headingLine.visible()
             holder.view.bodyItem.gone()
             holder.view.itemHeadingLine.gone()
         }else{
             holder.view.reward = rewardList[position]
             holder.view.heading.gone()
             holder.view.headingLine.gone()
             holder.view.bodyItem.visible()
             holder.view.itemHeadingLine.visible()
         }*/
//        holder.view.onViewClick = this
      /*  holder.view.item.setOnClickListener {
            val action = RewardsFragmentDirections.actionRewards2ToTripDetails(rewardList[position])
            Navigation.findNavController(it)
                .navigate(action)
        }*/
    }
}