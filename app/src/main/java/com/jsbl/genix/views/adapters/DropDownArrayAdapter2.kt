package com.jsbl.genix.views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.databinding.DataBindingUtil
import com.jsbl.genix.R
import com.jsbl.genix.databinding.SpinnerDropDownBinding
import com.jsbl.genix.model.profileManagement.*


class DropDownArrayAdapter2(
    context: Context,
    var resource: Int = R.layout.spinner_drop_down,
    var objList: List<Any?>
) :
    ArrayAdapter<Any?>(context, resource, objList) {
    private val mContext: Context = context
    var tempItems: ArrayList<Any?>;
    var suggestions: ArrayList<Any?>

    init {
        tempItems = ArrayList<Any?>() // this makes the difference.
        tempItems.addAll(objList)
        suggestions = ArrayList<Any?>()
    }

    override fun getCount(): Int {
        Log.d("****objectSize", "" + objList.size)
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

    inner class SpinnerPlaceHolder(var binding: SpinnerDropDownBinding, var view: View)


    override fun getFilter(): Filter {
        return nameFilter
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence? {

            when {
                resultValue is Manufacturer -> {
                    return resultValue.name
                }
                resultValue is Color -> {
                    return resultValue.name
                }
                resultValue is Maker -> {
                    return resultValue.name
                }
                resultValue is NotInsuredReason -> {
                    return resultValue.title
                }
                resultValue is DeliveryMethod -> {
                    return resultValue.name
                }
                resultValue is DeviceType -> {
                    return resultValue.name
                }
                resultValue is MotorType -> {
                    return resultValue.name
                }
                else -> {
                    return resultValue.toString()

                }
            }
        }

        protected override fun performFiltering(constraint: CharSequence?): FilterResults? {
            return if (!constraint.isNullOrEmpty()) {
                suggestions.clear()
                for (names in tempItems) {

                    when {
                        names is Manufacturer -> {
                            if (names.name!!.toLowerCase()
                                    .contains(constraint.toString().toLowerCase())
                            ) {
                                suggestions.add(names)
                            }
                        }
                        names is Color -> {
                            if (names.name!!.toLowerCase()
                                    .contains(constraint.toString().toLowerCase())
                            ) {
                                suggestions.add(names)
                            }
                        }
                        names is Maker -> {
                            if (names.name!!.toLowerCase()
                                    .contains(constraint.toString().toLowerCase())
                            ) {
                                suggestions.add(names)
                            }
                        }
                        names is NotInsuredReason -> {
                            if (names.title!!.toLowerCase()
                                    .contains(constraint.toString().toLowerCase())
                            ) {
                                suggestions.add(names)
                            }
                        }
                        names is DeliveryMethod -> {
                            if (names.name!!.toLowerCase()
                                    .contains(constraint.toString().toLowerCase())
                            ) {
                                suggestions.add(names)
                            }
                        }
                        names is DeviceType -> {
                            if (names.name!!.toLowerCase()
                                    .contains(constraint.toString().toLowerCase())
                            ) {
                                suggestions.add(names)
                            }
                        }
                        names is MotorType -> {
                            if (names.name!!.toLowerCase()
                                    .contains(constraint.toString().toLowerCase())
                            ) {
                                suggestions.add(names)
                            }
                        }
                        names is String -> {
                            if (names.toLowerCase().contains(constraint.toString().toLowerCase())) {
                                suggestions.add(names)
                            }
                        }
                    }

                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        protected override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            if (results != null && results.count > 0) {
                clear()
                when (results.values) {
                    is ArrayList<*> -> {
                        for (names in results.values as ArrayList<*>) {
                            when {
                                names is Manufacturer -> {
                                    add(names)
                                    notifyDataSetChanged()
                                }
                                names is Color -> {
                                    add(names)
                                    notifyDataSetChanged()
                                }
                                names is Maker -> {
                                    add(names)
                                    notifyDataSetChanged()
                                }
                                names is NotInsuredReason -> {
                                    add(names)
                                    notifyDataSetChanged()
                                }
                                names is DeliveryMethod -> {
                                    add(names)
                                    notifyDataSetChanged()
                                }
                                names is DeviceType -> {
                                    add(names)
                                    notifyDataSetChanged()
                                }
                                names is MotorType -> {
                                    add(names)
                                    notifyDataSetChanged()
                                }
                                names is String -> {
                                    add(names)
                                    notifyDataSetChanged()
                                }
                            }

                        }
                    }
                }

            }
        }
    }

}