package com.jsbl.genix.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.RecyclerTripItemBinding
import com.jsbl.genix.trips.TripItem
import com.jsbl.genix.views.fragments.MyTripsDirections

/**
 * Created by Muhammad Ali on 29-Apr-20.
 * Email muhammad.ali9385@gmail.com
 */
class TripItemAdpter(
    val tripItems: ArrayList<TripItem>
) :
    RecyclerView.Adapter<TripItemAdpter.TripItemViewHolder>() {
    var bindingSender: RecyclerTripItemBinding? = null

    inner class TripItemViewHolder(var view: RecyclerTripItemBinding) :
        RecyclerView.ViewHolder(view.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        bindingSender =
            DataBindingUtil.inflate<RecyclerTripItemBinding>(
                inflater,
                R.layout.recycler_trip_item,
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
        holder.view.item.setOnClickListener {
//            val action = MyTripsDirections.actionMyTripsToTripDetails(tripItems[position])
//            Navigation.findNavController(it)
//                .navigate(action)
        }
    }
}