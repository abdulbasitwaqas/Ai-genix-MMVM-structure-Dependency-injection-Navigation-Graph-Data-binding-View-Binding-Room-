package com.jsbl.genix.views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.model.profileManagement.MotorType

class MotorTypeAdapter(
    private var motorTypeList: List<MotorType>,
    clicks: MotorClick,
    private val contextt: Context
) :
    RecyclerView.Adapter<MotorTypeAdapter.MyViewHolder>() {
    private var clicks: MotorTypeAdapter.MotorClick

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.spinner_drop_down, null)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textHolder.text = motorTypeList[position].name
        holder.textHolder.setOnClickListener {
            clicks.motorTypePicker(
                position,
                motorTypeList[position].name
            )
        }
    }

    override fun getItemCount(): Int {
        Log.d("brandSize", "" + motorTypeList.size)
        return motorTypeList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textHolder: TextView

        init {
            textHolder = itemView.findViewById(R.id.textHolder)
        }
    }

    fun setProductList(motorTypeList: List<MotorType>) {
        this.motorTypeList = motorTypeList
        notifyDataSetChanged()
    }

    interface MotorClick {
        fun motorTypePicker(position: Int, typeName: String?)
    }

    companion object {
        lateinit var clicks: MotorClick
    }

    init {
        this.clicks = clicks
    }
}