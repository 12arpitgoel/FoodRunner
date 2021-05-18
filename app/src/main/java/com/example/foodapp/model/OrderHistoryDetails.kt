package com.example.foodapp.model

data class OrderHistoryDetails(
    val resName:String,
    val orderDate:String,
    val orderHistoryList:ArrayList<RestaurantDetails>
)
