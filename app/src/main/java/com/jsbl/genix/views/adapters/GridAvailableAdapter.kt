package com.jsbl.genix.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jsbl.genix.BuildConfig
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ItemAvailableListBinding
import com.jsbl.genix.databinding.ItemGridAvailableListBinding
import com.jsbl.genix.databinding.RecyclerRedeemItemBinding
import com.jsbl.genix.model.GetAllGamesResponseItem
import com.jsbl.genix.model.games.FaltuInterface
import com.jsbl.genix.model.games.GameListActiveItem
import com.jsbl.genix.model.games.GameListAvailableItem
import com.jsbl.genix.model.games.GameListInActiveItem
import com.jsbl.genix.model.profileManagement.MotorType
import com.jsbl.genix.url.APIsURL
import kotlinx.android.synthetic.main.item_available_list.view.*
import kotlinx.android.synthetic.main.item_available_list.view.iv_options
import kotlinx.android.synthetic.main.item_grid_available_list.view.*

class GridAvailableAdapter(
        private val getAllGamesItemsList: List<FaltuInterface>,
        private val context: Context
) :
    RecyclerView.Adapter<GridAvailableAdapter.MyViewHolder>() {

    var binding: ItemGridAvailableListBinding? = null

    inner class MyViewHolder(var view: ItemGridAvailableListBinding) :
            RecyclerView.ViewHolder(view.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding =
                DataBindingUtil.inflate<ItemGridAvailableListBinding>(
                        inflater,
                        R.layout.item_grid_available_list,
                        parent,
                        false
                )
        return MyViewHolder(binding!!)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        when(getAllGamesItemsList[position]){
            is GameListInActiveItem ->{
                val item = getAllGamesItemsList[position] as GameListInActiveItem
                binding!!.tvTitles.text = item.title
//             itemView.iv_options.setImageResource(imageList[position])

                Glide.with(context)
                        .load(BuildConfig.GAMES_ICONS+item.filePath)
                        .placeholder(R.drawable.ic_gamification_signal)
                        .into(binding!!.ivOptions)
            }
            is GameListActiveItem ->{
                val item = getAllGamesItemsList[position] as GameListActiveItem
                binding!!.tvTitles.text = item.title
//             itemView.iv_options.setImageResource(imageList[position])

                Glide.with(context)
                        .load(BuildConfig.GAMES_ICONS+item.filePath)
                        .placeholder(R.drawable.ic_gamification_signal)
                        .into(binding!!.ivOptions)
            }
            is GameListAvailableItem ->{
                val item = getAllGamesItemsList[position] as GameListAvailableItem
                binding!!.tvTitles.text = item.title
//             itemView.iv_options.setImageResource(imageList[position])

                Glide.with(context)
                        .load(BuildConfig.GAMES_ICONS+item.filePath)
                        .placeholder(R.drawable.ic_gamification_signal)
                        .into(binding!!.ivOptions)
            }
        }

    }

    override fun getItemCount(): Int {

        return getAllGamesItemsList.size
    }

    fun setProductList(motorTypeList: List<MotorType>) {
        // this.motorTypeList = motorTypeList
        notifyDataSetChanged()
    }

    interface MotorClick {
        fun motorTypePicker(position: Int, typeName: String?)
    }

    companion object {
        lateinit var clicks: MotorClick
    }

}