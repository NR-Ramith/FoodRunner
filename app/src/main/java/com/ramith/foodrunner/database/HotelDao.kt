package com.ramith.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HotelDao {
    @Insert
    fun insertHotel(hotelEntity: HotelEntity)

    @Delete
    fun deleteHotel(hotelEntity: HotelEntity)

    @Query("SELECT * FROM hotels WHERE hotel_id LIKE :userId")
    fun getAllHotels(userId: String): List<HotelEntity>

    @Query("SELECT * FROM hotels WHERE hotel_id = :hotelId")
    fun getHotelById(hotelId: String): HotelEntity
}