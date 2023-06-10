package com.ramith.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HotelEntity::class], version = 1)
abstract class HotelDatabase : RoomDatabase() {
    abstract fun hotelDao(): HotelDao
}