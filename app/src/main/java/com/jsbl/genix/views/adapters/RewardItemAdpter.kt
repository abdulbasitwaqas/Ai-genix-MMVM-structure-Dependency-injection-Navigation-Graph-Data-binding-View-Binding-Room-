package com.jsbl.genix.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.trips.TripsDetailModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class RewardItemAdpter(
    private var tripsDetailModelList: List<TripsDetailModel?>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val df2: DecimalFormat = DecimalFormat("#.##")

    private val VIEW_TYPE_LOADING = 1
    private val VIEW_TYPE_ITEM = 0

    override fun getItemViewType(position: Int): Int {
        return if (tripsDetailModelList.get(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_ITEM -> {

                val itemView: View =
                    LayoutInflater.from(parent.context).inflate(R.layout.recycler_reward_item_new, null)
                return MyViewHolder(itemView)
            }
            VIEW_TYPE_LOADING -> {
                val view: View =
                    LayoutInflater.from(parent?.context).inflate(R.layout.item_loading, null)
                return LoadingViewHolder(view)
            }
            else -> {
                throw IllegalArgumentException("Invalid view type")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (holder is MyViewHolder) {
            holder.startingTV.text = ""+tripsDetailModelList[position]!!.startLocation
            holder.endingTV.text = ""+tripsDetailModelList[position]!!.endLocation
            holder.item_time.text = ""+tripsDetailModelList[position]!!.duration
            holder.item_distance.text = ""+df2.format(tripsDetailModelList[position]!!.distance)
            holder.item_score.text = "- - "
        }
        else if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        return tripsDetailModelList.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val item_trips: TextView
        val endingTV: TextView
        val startingTV: TextView
        val item_time: TextView
        val item_distance: TextView
        val item_score: TextView

        init {
//            item_trips = itemView.findViewById(R.id.item_trips)
            endingTV = itemView.findViewById(R.id.endingTV)
            startingTV = itemView.findViewById(R.id.startingTV)
            item_time = itemView.findViewById(R.id.item_time)
            item_distance = itemView.findViewById(R.id.item_distance)
            item_score = itemView.findViewById(R.id.item_score)
        }
    }

    // "Loading item" ViewHolder
    private class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var progressBar: ProgressBar

        init {
            progressBar = view.findViewById<View>(R.id.progressBar1) as ProgressBar
        }
    }
}