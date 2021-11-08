package com.jsbl.genix.views.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.jsbl.genix.R
import com.jsbl.genix.databinding.RecyclerCartItemBinding
import com.jsbl.genix.model.redeem.RedeemCartListItem
import com.jsbl.genix.utils.logD
import java.util.ArrayList

/**
 * Created by Muhammad Ali on 29-Apr-20.
 * Email muhammad.ali9385@gmail.com
 */
class CartItemAdapter(
    val context: Context,
    val rewardList: ArrayList<RedeemCartListItem>? = java.util.ArrayList(),
    val cartUpdateListener: CartUpdateListener,
    val listUpdateListener: UpdateIdsListListener
) :
    RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {
    var bindingSender: RecyclerCartItemBinding? = null

    companion object{

        var rewardList1: ArrayList<RedeemCartListItem>? = java.util.ArrayList()
        var cartList = ArrayList<ArrayList<String>>()
        var redeemPoints: Int = 0
        var Noofitem:Int=1;
    }

    inner class CartItemViewHolder(var view: RecyclerCartItemBinding) :
        RecyclerView.ViewHolder(view.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        bindingSender =
            DataBindingUtil.inflate(
                inflater,
                R.layout.recycler_cart_item,
                parent,
                false
            )
        return CartItemViewHolder(bindingSender!!)
    }

    override fun getItemCount(): Int = rewardList!!.size


    fun updateList(list: java.util.ArrayList<RedeemCartListItem>?) {
        rewardList1!!.clear()
        rewardList!!.clear()
        rewardList.addAll(list!!)
        notifyDataSetChanged()
    }

    fun insertItem(rewardItem: RedeemCartListItem) {
//        this.regChatItem.clear()
        rewardList!!.add(rewardItem)
        notifyItemInserted(rewardList.size - 1)
    }


    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        rewardList1=rewardList
        holder.view.tripItem = rewardList!!.get(position)
//        holder.view.onViewClick = this

        holder.view.updateCart.visibility = View.GONE
        //count before change
        val oldCount = rewardList!!.get(position).reedeemCount
        logD("oldcount ", "$oldCount")
//        cartList.clear()
//        val count = holder.view.cartItemCount.text.toString()
        for (i in 0 until oldCount!!) {
           // redeemPoints = redeemPoints + (oldCount.toInt() * rewardList!!.get(position).point!!)

            var idsList = ArrayList<String>()
            idsList.add(rewardList!!.get(position).reedeemID.toString())
            cartList.add(idsList)
        }


        listUpdateListener.onListUpdate(cartList, redeemPoints)

        holder.view.plusImage.setOnClickListener {

            if (holder.view.updateCart.visibility== View.VISIBLE){
                holder.view.updateCart.visibility=View.GONE
            }
            holder.view.minusImage.isEnabled = true
            val count = holder.view.cartItemCount.text.toString()
            holder.view.cartItemCount.text = count.toIntOrNull()?.let { it + 1 }.toString()

            logD("newcount ", "${holder.view.cartItemCount.text.toString()}")
            if (holder.view.cartItemCount.text.toString().equals(oldCount!!.toString())) {
                holder.view.updateCart.visibility = View.GONE
            } else {
                holder.view.updateCart.visibility = View.VISIBLE
            }
//              cartUpdateListener.onCartUpdateClick(rewardList!!.get(position),true,holder.view.cartItemCount.text.toString())
        }
        holder.view.minusImage.setOnClickListener {
            val count = holder.view.cartItemCount.text.toString()
            val newCount = count.toIntOrNull()?.let { it - 1 }

            newCount!!.let {
                if (it <= 0) {
                    val builder: AlertDialog.Builder? = context?.let {
                        AlertDialog.Builder(context)
                    }

                    builder!!.setMessage("Are you sure you want to delete this item from cart?")
                        .setTitle("Delete")

                    builder.apply {
                        setPositiveButton("Yes") { dialog, id ->
                            holder.view.cartItemCount.text = newCount.toString()
                            holder.view.minusImage.isEnabled = false

                            if (holder.view.cartItemCount.text.toString()
                                    .equals(oldCount!!.toString())
                            ) {
                                holder.view.updateCart.visibility = View.GONE
                            } else {
                                holder.view.updateCart.visibility = View.VISIBLE
                            }
                        }
                        setNegativeButton("No") { dialog, id ->
                        }
                    }
                    val dialog: AlertDialog? = builder.create()

                    dialog!!.show()
                } else {
                    holder.view.cartItemCount.text = newCount.toString()
                }
                if (holder.view.cartItemCount.text.toString().equals(oldCount!!.toString())) {
                    holder.view.updateCart.visibility = View.GONE
                } else {
                    holder.view.updateCart.visibility = View.VISIBLE
                }
            }
//              cartUpdateListener.onCartUpdateClick(rewardList!!.get(position),false,"0")
        }
        holder.view.tickImage.setOnClickListener {


            Noofitem += Integer.valueOf(holder.view.cartItemCount.text.toString())
            holder.view.updateCart.visibility = View.GONE
            val count = holder.view.cartItemCount.text.toString()
            for (i in 0 until oldCount - count.toInt()) {
//                redeemPoints = redeemPoints + (count.toInt() * rewardList!!.get(position).point!!)
                var idsList = ArrayList<String>()
                cartList.remove(cartList.get(position))
                idsList.add(rewardList!!.get(position).reedeemID.toString())
                cartList.add(idsList)
            }

            listUpdateListener.onListUpdate(cartList, redeemPoints)
            if (count.equals("0")) {
                cartUpdateListener.onCartUpdateClick(
                    rewardList!!.get(position),
                    false,
                    holder.view.cartItemCount.text.toString()
                )
            } else {
                cartUpdateListener.onCartUpdateClick(
                    rewardList!!.get(position),
                    true,
                    holder.view.cartItemCount.text.toString()
                )
            }
        }
        holder.view.crossImage.setOnClickListener {

            holder.view.cartItemCount.text = oldCount.toString()
            holder.view.updateCart.visibility = View.GONE
//              cartUpdateListener.onCartUpdateClick(rewardList!!.get(position),false,"0")
        }

    }

    interface CartUpdateListener {
        fun onCartUpdateClick(
            redeemCartListItem: RedeemCartListItem,
            isInsert: Boolean,
            count: String
        )
    }

    interface UpdateIdsListListener {
        fun onListUpdate(list: ArrayList<ArrayList<String>>, points: Int)
    }
}