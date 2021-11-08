//package com.watchtrading.watchtrade.Adapters
//
//import android.content.Context
//import android.content.Intent
//import android.util.Log
//import android.view.*
//import android.widget.*
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.watchtrading.watchtrade.Activities.ChattingActivity
//import com.watchtrading.watchtrade.Activities.ReportActivity
//import com.watchtrading.watchtrade.Activities.RepostActivity
//import com.watchtrading.watchtrade.Activities.UpdateWatchActivity
//import com.watchtrading.watchtrade.Activities.UserProfileActivity
//import com.watchtrading.watchtrade.Constants.APIContract
//import com.watchtrading.watchtrade.Models.BrandModel
//import com.watchtrading.watchtrade.R
//import com.watchtrading.watchtrade.Utils.SharedPreferencesSotreData
//
//class BrandAdapter(brandModelList: List<BrandModel>, clicks: Clicks, context: Context) :
//    RecyclerView.Adapter<BrandAdapter.MyViewHolder>() {
//    private var brandModelList: List<BrandModel>
//    private val context: Context
//    private val pos = 0
//    private var userID: String? = null
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): MyViewHolder {
//        val itemView: View =
//            LayoutInflater.from(parent.context).inflate(R.layout.brands_list, null)
//        return MyViewHolder(itemView)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.makeTV.setText(brandModelList[position].getPostmake())
//        holder.caseSizeTVBL.setText(brandModelList[position].getCasesize())
//        holder.modelTV.setText(brandModelList[position].getPostmodel())
//        holder.yearTV.setText(brandModelList[position].getPostyear())
//        holder.typeeTV.setText(brandModelList[position].getPostype())
//        holder.locationTV.setText(brandModelList[position].getPostaddress())
//        holder.priceTVBL.setText(brandModelList[position].getEnterprice())
//        userID = brandModelList[position].getCreateuserID()
//        val createuserID: String = brandModelList[position].getCreateuserID()
//        val box: String = brandModelList[position].getWatchbox()
//        val paper: String = brandModelList[position].getPaperwatch()
//        if (box == "Yes" || box == "yes" || box == "checked") {
//            holder.boxCB.isChecked = true
//        } else {
//            holder.boxCB.isChecked = false
//        }
//        holder.boxCB.isEnabled = false
//        holder.papersCB.isEnabled = false
//        if (paper == "Yes" || paper == "yes" || paper == "checked") {
//            holder.papersCB.isChecked = true
//        } else {
//            holder.papersCB.isChecked = false
//        }
//        holder.moveBLBtn.setOnClickListener {
//            val intent = Intent(context, ChattingActivity::class.java)
//            intent.putExtra("uuuID", "" + brandModelList[position].getCreateuserID())
//            intent.putExtra("uuuName", "" + brandModelList[position].getPostmake())
//            context.startActivity(intent)
//        }
//        holder.menuIV.setOnClickListener { v ->
//            if (SharedPreferencesSotreData.getInstance().getID()
//                    .equals(brandModelList[position].getCreateuserID())
//            ) {
//                personalPop(v, position, brandModelList[position].getCreateuserID())
//            } else {
//                showPopupMenu(v, position, brandModelList[position].getCreateuserID())
//            }
//        }
//        holder.editBtn.setOnClickListener {
//            val intent = Intent(context, UpdateWatchActivity::class.java)
//            intent.putExtra("postID", "" + brandModelList[position].getPostID())
//            intent.putExtra("createuserID", "" + createuserID)
//            intent.putExtra("postType", "" + brandModelList[position].getPostype())
//            intent.putExtra("postmake", "" + brandModelList[position].getPostmake())
//            intent.putExtra("casesize", "" + brandModelList[position].getCasesize())
//            intent.putExtra("postmode", "" + brandModelList[position].getPostmodel())
//            intent.putExtra("enterprice", "" + brandModelList[position].getEnterprice())
//            intent.putExtra("postyear", "" + brandModelList[position].getPostyear())
//            intent.putExtra("postarea", "" + brandModelList[position].getPostaddress())
//            intent.putExtra("postcountry", "" + brandModelList[position].getPostcountry())
//            intent.putExtra("watchbox", "" + brandModelList[position].getWatchbox())
//            intent.putExtra("paperwatch", "" + brandModelList[position].getPaperwatch())
//            context.startActivity(intent)
//        }
//        Glide.with(context)
//            .load(APIContract.IMAGE_URL + brandModelList[position].getFile_name())
//            .placeholder(R.drawable.app_icon)
//            .into(holder.userImageCLSL)
//
//        /*  Glide.with(context)
//                .load(APIContract.IMAGE_URL + brandModelList.get(position).getPostID()+".png")
//                .placeholder(R.drawable.app_icon)
//                .into(holder.userImageCLSL);*/Log.d(
//            "picturequery",
//            APIContract.IMAGE_URL + brandModelList[position].getFile_name()
//        )
//        holder.userImageCLSL.setOnClickListener {
//            clicks.showImage(
//                position,
//                APIContract.IMAGE_URL + brandModelList[position].getFile_name()
//            )
//        }
//    }
//
//    private fun showPopupMenu(view: View, position: Int, userID: String) {
//        // inflate menu
//        val popup = PopupMenu(context, view)
//        val inflater = popup.menuInflater
//        inflater.inflate(R.menu.menubutton, popup.menu)
//        popup.setOnMenuItemClickListener(MyMenuItemClickListener(position, userID))
//        popup.show()
//    }
//
//    private inner class MyMenuItemClickListener(var position: Int, var userId: String) :
//        PopupMenu.OnMenuItemClickListener {
//        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
//            when (menuItem.itemId) {
//                R.id.message -> {
//                    val intentt = Intent(context, ChattingActivity::class.java)
//                    intentt.putExtra("uuuID", "" + brandModelList[position].getCreateuserID())
//                    intentt.putExtra("uuuName", "" + brandModelList[position].getPostmake())
//                    context.startActivity(intentt)
//                }
//                R.id.profile -> {
//                    //                    Toast.makeText(context, "profile", Toast.LENGTH_SHORT).show();
//                    val profIntent = Intent(context, UserProfileActivity::class.java)
//                    profIntent.putExtra("userID", "" + brandModelList[position].getCreateuserID())
//                    context.startActivity(profIntent)
//                }
//                R.id.report -> {
//                    val intent = Intent(context, ReportActivity::class.java)
//                    intent.putExtra("postID", "" + brandModelList[position].getPostID())
//                    intent.putExtra("createuserID", "" + brandModelList[position].getCreateuserID())
//                    intent.putExtra("postType", "" + brandModelList[position].getPostype())
//                    intent.putExtra("postmake", "" + brandModelList[position].getPostmake())
//                    intent.putExtra("casesize", "" + brandModelList[position].getCasesize())
//                    intent.putExtra("postmode", "" + brandModelList[position].getPostmodel())
//                    intent.putExtra("enterprice", "" + brandModelList[position].getEnterprice())
//                    intent.putExtra("postyear", "" + brandModelList[position].getPostyear())
//                    intent.putExtra("postarea", "" + brandModelList[position].getPostaddress())
//                    intent.putExtra("postcountry", "" + brandModelList[position].getPostcountry())
//                    intent.putExtra("watchbox", "" + brandModelList[position].getWatchbox())
//                    intent.putExtra("paperwatch", "" + brandModelList[position].getPaperwatch())
//                    context.startActivity(intent)
//                }
//                R.id.blockUser -> clicks.block(pos, brandModelList[position].getCreateuserID())
//            }
//            return false
//        }
//    }
//
//    fun setProductList(brandModelList: List<BrandModel>) {
//        this.brandModelList = brandModelList
//        notifyDataSetChanged()
//    }
//
//    override fun getItemCount(): Int {
//        Log.d("brandSize", "" + brandModelList.size)
//        return brandModelList.size
//    }
//
//    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val makeTV: TextView
//        private val modelTV: TextView
//        private val locationTV: TextView
//        private val yearTV: TextView
//        private val typeeTV: TextView
//        private val caseSizeTVBL: TextView
//        private val priceTVBL: TextView
//        private val papersCB: CheckBox
//        private val boxCB: CheckBox
//        private val moveBLBtn: ImageView
//        private val editBtn: ImageView
//        private val menuIV: ImageView
//        private val userImageCLSL: ImageView
//        private val paperAndBoxRG: RadioGroup
//
//        init {
//            moveBLBtn = itemView.findViewById(R.id.moveBLBtn)
//            menuIV = itemView.findViewById(R.id.menuIV)
//            editBtn = itemView.findViewById(R.id.editBtn)
//            makeTV = itemView.findViewById(R.id.makeTV)
//            modelTV = itemView.findViewById(R.id.modelTV)
//            priceTVBL = itemView.findViewById(R.id.priceTVBL)
//            locationTV = itemView.findViewById(R.id.locationTV)
//            caseSizeTVBL = itemView.findViewById(R.id.caseSizeTVBL)
//            //            locationTV=itemView.findViewById(R.id.locationTV);
//            yearTV = itemView.findViewById(R.id.yearTV)
//            papersCB = itemView.findViewById(R.id.papersCB)
//            boxCB = itemView.findViewById(R.id.boxCB)
//            paperAndBoxRG = itemView.findViewById(R.id.paperAndBoxRG)
//            typeeTV = itemView.findViewById(R.id.typeeTV)
//            userImageCLSL = itemView.findViewById(R.id.userImageCLSL)
//        }
//    }
//
//    interface Clicks {
//        fun addToContacts(position: Int)
//        fun message(position: Int)
//        fun block(position: Int, userID: String?)
//        fun delete(position: Int, userID: String?)
//        fun showImage(position: Int, imageURL: String?)
//    }
//
//    private fun personalPop(view: View, position: Int, userID: String) {
//        // inflate menu
//        val popup = PopupMenu(context, view)
//        val inflater = popup.menuInflater
//        inflater.inflate(R.menu.profile_menu, popup.menu)
//        popup.setOnMenuItemClickListener(PersonalMenuPOP(position, userID))
//        popup.show()
//    }
//
//    private inner class PersonalMenuPOP(private val position: Int, private val userId: String) :
//        PopupMenu.OnMenuItemClickListener {
//        override fun onMenuItemClick(menuItem: MenuItem): Boolean {
//            when (menuItem.itemId) {
//                R.id.repost -> {
//                    val intent = Intent(context, RepostActivity::class.java)
//                    intent.putExtra("postID", "" + brandModelList[position].getPostID())
//                    intent.putExtra("createuserID", "" + brandModelList[position].getCreateuserID())
//                    intent.putExtra("postType", "" + brandModelList[position].getPostype())
//                    intent.putExtra("postmake", "" + brandModelList[position].getPostmake())
//                    intent.putExtra("casesize", "" + brandModelList[position].getCasesize())
//                    intent.putExtra("postmode", "" + brandModelList[position].getPostmodel())
//                    intent.putExtra("enterprice", "" + brandModelList[position].getEnterprice())
//                    intent.putExtra("postyear", "" + brandModelList[position].getPostyear())
//                    intent.putExtra("postarea", "" + brandModelList[position].getPostaddress())
//                    intent.putExtra("postcountry", "" + brandModelList[position].getPostcountry())
//                    intent.putExtra("watchbox", "" + brandModelList[position].getWatchbox())
//                    intent.putExtra("paperwatch", "" + brandModelList[position].getPaperwatch())
//                    context.startActivity(intent)
//                }
//                R.id.delete -> clicks.delete(
//                    position, brandModelList[position].getPostID()
//                )
//            }
//            return false
//        }
//    }
//
//    companion object {
//        var clicks: Clicks
//    }
//
//    init {
//        this.brandModelList = brandModelList
//        this.context = context
//        this.clicks = clicks
//    }
//}