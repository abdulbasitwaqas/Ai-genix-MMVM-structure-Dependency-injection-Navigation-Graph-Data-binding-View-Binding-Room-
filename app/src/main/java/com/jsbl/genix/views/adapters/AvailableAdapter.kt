package com.jsbl.genix.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jsbl.genix.BuildConfig
import com.jsbl.genix.R
import com.jsbl.genix.databinding.ItemAvailableListBinding
import com.jsbl.genix.model.GetAllGamesResponseItem
import com.jsbl.genix.model.games.FaltuInterface
import com.jsbl.genix.model.games.GameListActiveItem
import com.jsbl.genix.model.games.GameListAvailableItem
import com.jsbl.genix.model.games.GameListInActiveItem
import com.jsbl.genix.model.profileManagement.MotorType
import com.jsbl.genix.url.APIsURL
import com.jsbl.genix.utils.loadManufacturerIcon
import kotlinx.android.synthetic.main.item_available_list.view.*

class AvailableAdapter (
        private val getAllGamesItemsList: List<FaltuInterface>,
        private val context: Context
) :
    RecyclerView.Adapter<AvailableAdapter.MyViewHolder>() {

    private var binding: ItemAvailableListBinding? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        binding= ItemAvailableListBinding.inflate(LayoutInflater.from(parent.context),
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
                binding!!.tvTitle.text = item.title
                binding!!.tvDesc.text = item.description

                Glide.with(context)
                        .load(BuildConfig.GAMES_ICONS+item.filePath)
                        .placeholder(R.drawable.ic_gamification_signal)
                        .into(binding!!.ivOptions)
            }
            is GameListActiveItem ->{
                val item = getAllGamesItemsList[position] as GameListActiveItem
                binding!!.tvTitle.text = item.title
                binding!!.tvDesc.text = item.description
                Glide.with(context)
                        .load(BuildConfig.GAMES_ICONS+item.filePath)
                        .placeholder(R.drawable.ic_gamification_signal)
                        .into(binding!!.ivOptions)
            }
            is GameListAvailableItem ->{
                val item = getAllGamesItemsList[position] as GameListAvailableItem
                binding!!.tvTitle.text = item.title
                binding!!.tvDesc.text = item.description

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

    inner class MyViewHolder(val itemView: ItemAvailableListBinding) : RecyclerView.ViewHolder(itemView.root) {

    }
    
    interface MotorClick {
        fun motorTypePicker(position: Int, typeName: String?)
    }

    companion object {
        lateinit var clicks: MotorClick
    }

}