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
import com.jsbl.genix.trips.TripsDetailModel
import com.jsbl.genix.views.fragments.MyTrips
import com.jsbl.genix.views.fragments.MyTripsDirections
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class TripsListAdapterVertical(
    private val allTripsDetailList: List<TripsDetailModel>,
    private val context: Context
) :
    RecyclerView.Adapter<TripsListAdapterVertical.MyViewHolder>() {
    private val df2: DecimalFormat = DecimalFormat("#.##")

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_trip_item, null)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val formatter =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                SimpleDateFormat("EEEE hh:mm a", Locale.ENGLISH)
            } else {
                SimpleDateFormat("EEEE hh:mm a", Locale.ENGLISH)
            }

        holder.distance.text = "Distance "+df2.format(allTripsDetailList[position].distance)+" KM"
        holder.scoreTV.text = "Score -"
        holder.endingTV.text = "" + allTripsDetailList[position].endLocation
        holder.startingTV.text = "" + allTripsDetailList[position].startLocation
        holder.tripStartTimeTV.text = "" + formatter.format(allTripsDetailList[position].startLocalTimestamp)
        if (allTripsDetailList.size>1){
            holder.grayLine.visibility = View.VISIBLE
        } else {
            holder.grayLine.visibility = View.GONE
        }

        holder.item.setOnClickListener {
         /*   val bundle = Bundle()
            val duration: String =allTripsDetailList[position].duration
            val endTriptime: String =allTripsDetailList[position].endLocalTimestamp
            val startLat: Double = allTripsDetailList[position].startLatitude
            val startLng : Double = allTripsDetailList[position].startLongitude
            val endLat : Double = allTripsDetailList[position].endLatitude
            val endLng : Double = allTripsDetailList[position].endLongitude
            val position : Int = position



            bundle.putParcelableArrayList("tripEventList", allTripsDetailList[position].tripEventModel[position].p);
            bundle.putInt("position",position)
            bundle.putDouble("startingLat",startLat)
            bundle.putDouble("startingLng",startLng)
            bundle.putDouble("endingLat",endLat)
            bundle.putDouble("endingLng",endLng)
            bundle.putString("duration","$duration")

            val tripListAdapter = MyTrips()
            tripListAdapter.setArguments(bundle)

*/

            val action = MyTripsDirections.actionMyTripsToTripDetails(allTripsDetailList[position])
            Navigation.findNavController(it).navigate(action)

        }



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
        val tripStartTimeTV: TextView
        val grayLine: View
        val item: ConstraintLayout


        init {
            startingTV = itemView.findViewById(R.id.startingTV)
            endingTV = itemView.findViewById(R.id.endingTV)
            distance = itemView.findViewById(R.id.distance)
            scoreTV = itemView.findViewById(R.id.scoreTV)
            tripStartTimeTV = itemView.findViewById(R.id.tripStartTimeTV)
            grayLine = itemView.findViewById(R.id.grayLine)
            item = itemView.findViewById(R.id.item)

        }
    }
}