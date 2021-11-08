package com.jsbl.genix.views.adapters

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.model.GetAllGamesResponseItem
import com.jsbl.genix.trips.TripsDetailModel
import com.jsbl.genix.views.fragments.MyTrips
import com.jsbl.genix.views.fragments.MyTripsDirections
import java.text.SimpleDateFormat
import java.util.*


class GetAllGamesAdapter(
    private val allTripsDetailList: List<GetAllGamesResponseItem>,
    private val context: Context
) :
    RecyclerView.Adapter<GetAllGamesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_available_list, null)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {




    }

    override fun getItemCount(): Int {
        Log.d("**tripsssSize", "getItemCount: " + allTripsDetailList.size)
        return allTripsDetailList.size
    }



    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val startingTV: TextView
        val endingTV: TextView
        val distance: TextView
        val scoreTV: TextView
        val item: ConstraintLayout


        init {
            startingTV = itemView.findViewById(R.id.startingTV)
            endingTV = itemView.findViewById(R.id.endingTV)
            distance = itemView.findViewById(R.id.distance)
            scoreTV = itemView.findViewById(R.id.scoreTV)
            item = itemView.findViewById(R.id.item)

        }
    }
}