package com.example.foodapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.model.RestaurantDetails


class MenuRecyclerAdapter(
    val context: Context,
    private val restaurantMenuList: List<RestaurantDetails>,
    val listener: OnItemClickListener
) : RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishId: TextView = view.findViewById(R.id.txtDishId)
        val dishName: TextView = view.findViewById(R.id.txtDishName)
        val dishPrice: TextView = view.findViewById(R.id.txtDishPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    interface OnItemClickListener {
        fun onAddItemClick(menuItem: RestaurantDetails)
        fun onRemoveItemClick(menuItem: RestaurantDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)
        return MenuRecyclerAdapter.MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantMenuList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val restaurantMenuItem = restaurantMenuList[position]
        holder.dishId.text = (position + 1).toString()
        holder.dishName.text = restaurantMenuItem.DishName
        holder.dishPrice.text = "\u20A8 ${restaurantMenuItem.DishPrice}"

        holder.btnAdd.setOnClickListener {
            val btnText = holder.btnAdd.text.toString()
            if (btnText == "ADD") {
                holder.btnAdd.text = "REMOVE"
                val favColor = ContextCompat.getColor(context, R.color.colorRemove)
                holder.btnAdd.setBackgroundColor(favColor)
                listener.onAddItemClick(restaurantMenuItem)
            } else {
                holder.btnAdd.text = "ADD"
                val favColor = ContextCompat.getColor(context, R.color.colorPrimary)
                holder.btnAdd.setBackgroundColor(favColor)
                listener.onRemoveItemClick(restaurantMenuItem)
            }
        }
    }

}