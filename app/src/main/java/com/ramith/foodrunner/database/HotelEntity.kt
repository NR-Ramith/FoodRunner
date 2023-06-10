package com.ramith.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey val hotel_id: String,
    @ColumnInfo(name = "hotel_name") val hotelName: String,
    @ColumnInfo(name = "hotel_rating") val hotelRating: String,
    @ColumnInfo(name = "hotel_cost_for_one") val hotelCostForOne: String,
    @ColumnInfo(name = "hotel_image") val hotelImage: String,
)