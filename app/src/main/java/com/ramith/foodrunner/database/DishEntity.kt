package com.ramith.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dishes")
data class DishEntity(
    @PrimaryKey val dish_id: Int,
    @ColumnInfo(name = "dish_name") val dishName: String,
    @ColumnInfo(name = "hotel_price") val dishPrice: String,
)