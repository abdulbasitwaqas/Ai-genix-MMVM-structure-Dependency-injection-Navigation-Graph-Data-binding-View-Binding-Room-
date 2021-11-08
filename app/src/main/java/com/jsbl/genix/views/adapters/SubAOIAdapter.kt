package com.jsbl.genix.views.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jsbl.genix.R
import com.jsbl.genix.model.profileManagement.SubInterestList
import com.jsbl.genix.utils.logD

class SubAOIAdapter(
    private val subInterestListList: ArrayList<SubInterestList>?,
    private val context: Context,
    private val clicks: onClickOnItem
) :
    RecyclerView.Adapter<SubAOIAdapter.MyViewHolder>() {

    var subInterestList: ArrayList<String> = ArrayList()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.area_of_interest, null)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
//        var check = true
        holder.areaOfInterestTV.text = subInterestListList?.get(position)?.title
        Glide.with(context)
            .load("http://genix.ermispk.com/Icons/SubInterest/" + subInterestListList?.get(position)?.FilePath)
            .placeholder(R.drawable.place_holder)
            .into(holder.areaOfInterestIV)


        holder.innerConstraint.setOnClickListener {
            if (!subInterestListList?.get(position)!!.isSelected) {
                holder.innerConstraint.setBackgroundResource(R.drawable.new_ic_btn_bg_selected)
                subInterestListList?.get(position)?.iD
                subInterestListList?.get(position)?.parentInterestID
                if (!subInterestList.contains(subInterestListList!!.get(position).iD.toString()))
                    subInterestList.add(subInterestListList!!.get(position).iD.toString())
                logD("**subID", "" + subInterestList.size)
                clicks.subInterestList(
                    subInterestList,
                    "" + subInterestListList?.get(position)?.parentInterestID
                )
                subInterestListList?.get(position)!!.isSelected = true
                notifyItemChanged(position)
            } else {
                holder.innerConstraint.setBackgroundResource(R.drawable.new_ic_btn_bg_unselected)
                subInterestListList?.get(position)!!.isSelected = false
                if (subInterestList.contains(subInterestListList!!.get(position).iD.toString()))
                    subInterestList.remove(subInterestListList!!.get(position).iD.toString())
                clicks.subInterestList(
                    subInterestList,
                    "" + subInterestListList?.get(position)?.parentInterestID
                )
                notifyItemChanged(position)
            }
        }

        if (subInterestListList?.get(position)!!.isSelected) {
            holder.innerConstraint.setBackgroundResource(R.drawable.new_ic_btn_bg_selected)

            if (!subInterestList.contains(subInterestListList!!.get(position).iD.toString()))
                subInterestList.add(subInterestListList!!.get(position).iD.toString())
            clicks.subInterestList(
                subInterestList,
                "" + subInterestListList?.get(position)?.parentInterestID
            )
        } else {
            holder.innerConstraint.setBackgroundResource(R.drawable.new_ic_btn_bg_unselected)
        }


        /*  if (subInterestListList?.get(position)?.isSelected ==false) {
              holder.innerConstraint.setBackgroundResource(R.drawable.new_ic_btn_bg_unselected)
              subInterestListList?.get(position)?.isSelected = true
          }
          else if (subInterestListList?.get(position)?.isSelected == true) {
              holder.innerConstraint.setBackgroundResource(R.drawable.new_ic_btn_bg_selected)
              subInterestListList?.get(position)?.Interest
              subInterestListList?.get(position)?.parentInterestID
              subInterestListList?.get(position)?.isSelected = false
          }
  */
    }

    override fun getItemCount(): Int {
        return subInterestListList!!.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var areaOfInterestIV: ImageView
        var areaOfInterestTV: TextView
        var innerConstraint: LinearLayout

        init {
            areaOfInterestTV = itemView.findViewById(R.id.areaOfInterestTV)
            areaOfInterestIV = itemView.findViewById(R.id.areaOfInterestIV)
            innerConstraint = itemView.findViewById(R.id.innerConstraint)
        }
    }

    interface onClickOnItem {
        fun subInterestList(SubInterest: ArrayList<String>, InterestID: String)


    }
}