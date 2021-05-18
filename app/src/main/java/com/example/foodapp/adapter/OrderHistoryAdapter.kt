package com.example.foodapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.R
import com.example.foodapp.model.OrderHistoryDetails
import com.example.foodapp.model.RestaurantDetails

class OrderHistoryAdapter(
    val context: Context,
    private val orderHistoryDetails: ArrayList<OrderHistoryDetails>
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val resName: TextView = view.findViewById(R.id.txtRestaurantName)
        val orderDate: TextView = view.findViewById(R.id.txtDate)
        var recyclerCart: RecyclerView = view.findViewById(R.id.myCartRecyclerView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_previous_orders_single_row, parent, false)
        return OrderHistoryAdapter.OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistoryDetails.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val orderHistoryItem = orderHistoryDetails[position]
        holder.resName.text = orderHistoryItem.resName
        holder.orderDate.text = orderHistoryItem.orderDate.substring(0, 8)
        var cartInfoList: ArrayList<RestaurantDetails>
        cartInfoList = orderHistoryItem.orderHistoryList

        holder.recyclerCart.adapter = CartRecyclerAdapter(context, cartInfoList)
        holder.recyclerCart.layoutManager = LinearLayoutManager(context)
    }
}