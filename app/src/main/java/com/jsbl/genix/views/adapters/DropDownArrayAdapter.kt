package com.jsbl.genix.views.adapters

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.databinding.DataBindingUtil
import com.jsbl.genix.R
import com.jsbl.genix.databinding.SpinnerDropDownBinding
import com.jsbl.genix.model.profileManagement.*
import java.util.*


class DropDownArrayAdapter(
    context: Context,
    var resource: Int = R.layout.spinner_drop_down,
    var objList: List<Any?>
//    var objList: ArrayList<Maker>
) :
    ArrayAdapter<Any?>(context, resource, objList) {
    private val mContext: Context = context

    override fun getCount(): Int {
        return objList.size
    }

    lateinit var holder: SpinnerPlaceHolder

    override fun getItem(position: Int): Any? {
        return objList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView: View? = convertView
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
        when {
            objList[position] is Manufacturer -> {
                holder.binding.textHolder.text = (objList[position] as Manufacturer).name
            }
            objList[position] is Color -> {
                holder.binding.textHolder.text = (objList[position] as Color).name
            }
            objList[position] is Maker -> {
                holder.binding.textHolder.text = (objList[position] as Maker).name
            }
            objList[position] is NotInsuredReason -> {
                holder.binding.textHolder.text = (objList[position] as NotInsuredReason).title
            }
            objList[position] is DeliveryMethod -> {
                holder.binding.textHolder.text = (objList[position] as DeliveryMethod).name
            }
            objList[position] is DeviceType -> {
                holder.binding.textHolder.text = (objList[position] as DeviceType).name
            }
            objList[position] is MotorType -> {
                holder.binding.textHolder.text = (objList[position] as MotorType).name
            }
            objList[position] is String -> {
                holder.binding.textHolder.text = objList[position] as String

            }
        }
        return holder.view
    }

    fun setProductList(productList: List<Any?>) {
        this.objList = productList
        notifyDataSetChanged()

    }






    inner class SpinnerPlaceHolder(var binding: SpinnerDropDownBinding, var view: View)

}