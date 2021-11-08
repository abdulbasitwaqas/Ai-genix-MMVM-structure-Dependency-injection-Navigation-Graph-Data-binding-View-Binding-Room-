package com.jsbl.genix.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jsbl.genix.R
import com.jsbl.genix.databinding.RecyclerCarDetailItemBinding
import com.jsbl.genix.model.profileManagement.PostCarDetail
import com.jsbl.genix.utils.*
import com.jsbl.genix.utils.callBacks.OnItemClickListener
import kotlin.coroutines.coroutineContext


class CarsItemAdapter(
    private val postCarList: ArrayList<PostCarDetail>,
    private val onItemClickListener: OnItemClickListener,
    private val contextt: Context
) :
    RecyclerView.Adapter<CarsItemAdapter.CarItemViewHolder>() {

    companion object {
        const val VIEW_SENDER = 1
        const val VIEW_RECEIVER = 2
    }

    var bindingSender: RecyclerCarDetailItemBinding? = null

    inner class CarItemViewHolder(var view: RecyclerCarDetailItemBinding) :
        RecyclerView.ViewHolder(view.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        bindingSender =
            DataBindingUtil.inflate<RecyclerCarDetailItemBinding>(
                inflater,
                R.layout.recycler_car_detail_item,
                parent,
                false
            )
        return CarItemViewHolder(bindingSender!!)
    }


    override fun getItemCount(): Int = postCarList.size


    fun updateList(postCarDetailItems: ArrayList<PostCarDetail>) {
        this.postCarList.clear()
        this.postCarList.addAll(postCarDetailItems)
        notifyDataSetChanged()
    }



    fun getItem(pos: Int): PostCarDetail = this.postCarList[pos]


    override fun onBindViewHolder(holder: CarItemViewHolder, position: Int) {
        holder.view.carDetail = postCarList[position]
        holder.view.onViewClick = onItemClickListener
        holder.view.pos = position
        holder.view.carDetail=postCarList[position]

//        http://genix.ermispk.com/Icons/Manufacturerslogo/Toyota.PNG

    /*    if (postCarList.get(position).isDefaultCar) {
            holder.view.checkedIcon.visible()
            if (postCarList.get(position).isDefaultCar){
                SharePreferencesHelper.invoke(contextt).setDefaultCarPos(position)
            }
        } else {
            holder.view.checkedIcon.invisible()
        }*/

        if (postCarList[position].manufacturerID!!.toInt() == 10){
            holder.view.brandicon.setBackgroundResource(R.drawable.ic_honda)
        } else if (postCarList[position].manufacturerID!!.toInt() == 30){
            holder.view.brandicon.setBackgroundResource(R.drawable.ic_toyota)
        } else {
            holder.view.brandicon.setBackgroundResource(R.drawable.ic_default_interest)
        }


        if (postCarList.get(position).isDefaultCar ){
            logD("**defaultCarPos",""+ postCarList[position].registrationNo)
            holder.view.checkedIcon.visibility=View.VISIBLE
            SharePreferencesHelper.invoke(contextt).setDefaultCarPos(position)
        } else{
            holder.view.checkedIcon.visibility=View.GONE
        }
    }
}