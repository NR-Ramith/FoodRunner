package com.ramith.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DishEntity::class], version = 1)
abstract class DishDatabase : RoomDatabase() {
    abstract fun dishDao(): DishDao
}