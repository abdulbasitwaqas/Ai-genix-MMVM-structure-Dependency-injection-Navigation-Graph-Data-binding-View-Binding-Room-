package com.jsbl.genix.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.jsbl.genix.R
import com.jsbl.genix.databinding.SpinnerDropDownBinding
import com.jsbl.genix.model.profileManagement.*


class SpinnerAdapterManufacturer(val context: Context, var dataSource: List<Any>) : BaseAdapter() {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View? {
        val holder: SpinnerPlaceHolder
        if (convertView == null) {
            val itemBinding: SpinnerDropDownBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.spinner_drop_down,
                parent,
                false
            )
            holder = SpinnerPlaceHolder(itemBinding, itemBinding.getRoot())

            holder.view.tag = holder
        } else {
            holder = convertView.tag as SpinnerPlaceHolder
        }
        if (dataSource[position] is Manufacturer) {
            holder.binding.textHolder.text = (dataSource[position] as Manufacturer).name
        } else if (dataSource[position] is Color) {
            holder.binding.textHolder.text = (dataSource[position] as Color).name
        } else if (dataSource[position] is Maker) {
            holder.binding.textHolder.text = (dataSource[position] as Maker).name
        } else if (dataSource[position] is NotInsuredReason) {
            holder.binding.textHolder.text = (dataSource[position] as NotInsuredReason).title
        } else if (dataSource[position] is DeliveryMethod) {
            holder.binding.textHolder.text = (dataSource[position] as DeliveryMethod).name
        } else if (dataSource[position] is DeviceType) {
            holder.binding.textHolder.text = (dataSource[position] as DeviceType).name
        }else  if (dataSource[position] is MotorType) {
            holder.binding.textHolder.text = (dataSource[position] as MotorType).name
        } else if (dataSource[position] is String) {
            holder.binding.textHolder.text = dataSource[position] as String

        }
        return holder.view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    inner class SpinnerPlaceHolder(var binding: SpinnerDropDownBinding, var view: View)
}