package com.ramith.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DishDao {
    @Insert
    fun insertDish(dishEntity: DishEntity)

    @Delete
    fun deleteDish(dishEntity: DishEntity)

    @Query("SELECT * FROM dishes")
    fun getAllDishes(): List<DishEntity>

    @Query("SELECT * FROM dishes WHERE dish_id = :dishId")
    fun getDishById(dishId: String): DishEntity

    @Query("DELETE FROM dishes")
    fun clearDishes()
}