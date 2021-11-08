package com.jsbl.genix.views.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.trips.TripsDetailModel
import com.jsbl.genix.views.fragments.BottomSheetListDirections
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TripsListAdapterHorizontal(
    private val allTripsDetailList: List<TripsDetailModel>,
    private val context: Context
) :
    RecyclerView.Adapter<TripsListAdapterHorizontal.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_trip_item_dashboard, null)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val formatter =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
            } else {
                SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH)
            }
//        formatter.setTimeZone(TimeZone.getTimeZone("UTC"))

        holder.distance.text = "Distance: " + allTripsDetailList[position].distance + " KM"
        holder.endingTV.text = "" + allTripsDetailList[position].endLocation
        holder.startingTV.text = "" + allTripsDetailList[position].startLocation
        holder.time.text =formatter.format(allTripsDetailList[position].startLocalTimestamp)

        holder.item.setOnClickListener {
            val actionss = BottomSheetListDirections.actionDashBoard4ToTripDetails(allTripsDetailList[position])
            Navigation.findNavController(it).navigate(actionss)
        }
    }

    override fun getItemCount(): Int {
        if (allTripsDetailList.size > 5) {
            return 5
        } else
            return allTripsDetailList.size
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //        val date_day: TextView
        val time: TextView
        val startingTV: TextView
        val distance: TextView
        val endingTV: TextView
        val cardView: CardView
        val item: LinearLayout

        init {
//            date_day = itemView.findViewById(R.id.date_day)
            time = itemView.findViewById(R.id.time)
            startingTV = itemView.findViewById(R.id.startingTV)
            distance = itemView.findViewById(R.id.distance)
            endingTV = itemView.findViewById(R.id.endingTV)
            cardView = itemView.findViewById(R.id.cardView)
            item = itemView.findViewById(R.id.itemLL)
        }
    }


}