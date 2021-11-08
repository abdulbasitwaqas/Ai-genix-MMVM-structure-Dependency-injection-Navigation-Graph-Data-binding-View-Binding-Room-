package com.jsbl.genix.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ItemGridAvailableListBinding
import com.jsbl.genix.model.GetAllGamesResponseItem
import com.jsbl.genix.model.profileManagement.MotorType
import kotlinx.android.synthetic.main.item_available_list.view.*
import kotlinx.android.synthetic.main.item_grid_available_list.view.*
import kotlinx.android.synthetic.main.item_grid_available_list.view.iv_options


class GetAllGamesGridAdapter(
    private val getAllGamesItemsList: List<GetAllGamesResponseItem>,
    private val context: Context
) :
    RecyclerView.Adapter<GetAllGamesGridAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding= ItemGridAvailableListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData()

        /* holder.textHolder.setOnClickListener {
             clicks.motorTypePicker(
                 position,
                 motorTypeList[position].name
             )
         }*/
    }

    override fun getItemCount(): Int {

        return getAllGamesItemsList.size
    }

    inner class MyViewHolder(val itemView: ItemGridAvailableListBinding) : RecyclerView.ViewHolder(itemView.root) {
        fun bindData(){
            itemView.tv_titles.text = getAllGamesItemsList[position].title
//            itemView.iv_options.setImageResource(imageList[position])

            Glide.with(context)
                .load("http://genix.ermispk.com/service/Icons/" +getAllGamesItemsList[position].filePath)
                .placeholder(R.drawable.ic_gamification)
                .into(itemView.iv_options)
        }

    }

    fun setProductList(motorTypeList: List<MotorType>) {
        notifyDataSetChanged()
    }

    interface MotorClick {
        fun motorTypePicker(position: Int, typeName: String?)
    }

    companion object {
        lateinit var clicks: MotorClick
    }

}