package com.example.foodapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {
    @Insert
    fun insert(favResEntity: FavResEntity)

    @Delete
    fun delete(favResEntity: FavResEntity)

    @Query("SELECT * FROM favourite_restaurants")
    fun getAllFavRes():List<FavResEntity>

    @Query("SELECT * FROM favourite_restaurants WHERE res_id=:resId")
    fun getResById(resId:String):FavResEntity
}