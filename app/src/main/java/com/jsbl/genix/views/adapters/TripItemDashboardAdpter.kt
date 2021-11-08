package com.jsbl.genix.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.RecyclerTripItemDashboardBinding
import com.jsbl.genix.trips.TripItem
import com.jsbl.genix.views.fragments.DashBoard4Directions

/**
 * Created by Muhammad Ali on 29-Apr-20.
 * Email muhammad.ali9385@gmail.com
 */
class TripItemDashboardAdpter(
    val tripItems: ArrayList<TripItem>
) :
    RecyclerView.Adapter<TripItemDashboardAdpter.TripItemViewHolder>() {
    var bindingSender: RecyclerTripItemDashboardBinding? = null

    inner class TripItemViewHolder(var view: RecyclerTripItemDashboardBinding) :
        RecyclerView.ViewHolder(view.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        bindingSender =
            DataBindingUtil.inflate<RecyclerTripItemDashboardBinding>(
                inflater,
                R.layout.recycler_trip_item_dashboard,
                parent,
                false
            )
        return TripItemViewHolder(bindingSender!!)
    }

    override fun getItemCount(): Int = tripItems.size


    fun updateList(tripItems: ArrayList<TripItem>) {
        this.tripItems.clear()
        this.tripItems.addAll(tripItems)
        notifyDataSetChanged()
    }

    fun insertItem(tripItem: TripItem) {
//        this.regChatItem.clear()
        this.tripItems.add(tripItem)
        notifyItemInserted(tripItems.size - 1)
    }


    override fun onBindViewHolder(holder: TripItemViewHolder, position: Int) {
//        holder.view.tripItem = tripItems[position]
//        holder.view.onViewClick = this
       /* holder.view.item.setOnClickListener {
            val action = DashBoard4Directions.actionDashBoard4ToTripDetails(tripItems[position])
            Navigation.findNavController(it)
                .navigate(action)
        }*/
    }
}