package com.jsbl.genix.views.adapters

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.trips.TripsResponse
import java.text.SimpleDateFormat
import java.util.*

import android.widget.ProgressBar


class TripListDateAdapter(
    private val allTripsDetailList: java.util.ArrayList<TripsResponse?>,
    private val context: Context

) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var adpter: TripsListAdapterVertical
    private val VIEW_TYPE_LOADING = 1
    private val VIEW_TYPE_ITEM = 0

    override fun getItemViewType(position: Int): Int {
        return if (allTripsDetailList.get(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            VIEW_TYPE_ITEM -> {

                val itemView: View =
                    LayoutInflater.from(parent?.context).inflate(R.layout.trip_list_date, null)
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
            val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.getDefault())
            val date = simpleDateFormat.parse(allTripsDetailList[position]!!.date)
            simpleDateFormat.applyPattern("dd-MMM-yyyy")
            holder.dateHeaderTV.text = simpleDateFormat.format(date)
            adpter = TripsListAdapterVertical(allTripsDetailList[position]!!.tripsDetailsModelList, context)
            holder.recyclerViewDateViseTrips.layoutManager = LinearLayoutManager(context)
            holder.recyclerViewDateViseTrips.adapter = adpter
        }
        else if (holder is LoadingViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    override fun getItemCount(): Int {
        Log.d("**tripsssSize", "getItemCount: " + allTripsDetailList.size)
        return allTripsDetailList.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateHeaderTV: TextView
        val recyclerViewDateViseTrips: RecyclerView


        init {
            dateHeaderTV = itemView.findViewById(R.id.dateHeaderTV)
            recyclerViewDateViseTrips = itemView.findViewById(R.id.recyclerViewDateViseTrips)

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