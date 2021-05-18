package com.example.foodapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavResEntity::class],version = 1)
abstract class ResDatabase:RoomDatabase() {
    abstract fun resDao():RestaurantDao
}