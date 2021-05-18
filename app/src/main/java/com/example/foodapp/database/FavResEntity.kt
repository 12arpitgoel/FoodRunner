package com.example.foodapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_restaurants")
data class FavResEntity(
    @PrimaryKey val res_id:Int,
    @ColumnInfo(name="res_name") val resName:String,
    @ColumnInfo(name="res_rating") val rating:String,
    @ColumnInfo(name="res_cost_for_one") val cost_for_one:String,
    @ColumnInfo(name="res_image_url") val image_url:String
)