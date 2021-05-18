package com.example.foodapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.model.RestaurantDetails

class CartRecyclerAdapter(
    val context: Context,
    private val cartInfoList: ArrayList<RestaurantDetails>
) : RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishName: TextView = view.findViewById(R.id.txtDishName)
        val dishPrice: TextView = view.findViewById(R.id.txtDishPrice)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartRecyclerAdapter.CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartInfoList.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val restaurantMenuItem = cartInfoList[position]
        holder.dishName.text = restaurantMenuItem.DishName
        holder.dishPrice.text = "\u20A8 ${restaurantMenuItem.DishPrice}"
    }

}