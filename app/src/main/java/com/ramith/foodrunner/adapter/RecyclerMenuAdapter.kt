package com.ramith.foodrunner.adapter

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ramith.foodrunner.R
import com.ramith.foodrunner.activity.RestaurantDetailsActivity
import com.ramith.foodrunner.database.DishEntity
import com.ramith.foodrunner.model.Dish

class RecyclerMenuAdapter(val context: Context, val dishList: List<Dish>, val btnProceedCart: Button, val sharedPreferences: SharedPreferences) :
    RecyclerView.Adapter<RecyclerMenuAdapter.MenuVewHolder>() {
    class MenuVewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dishName: TextView = view.findViewById(R.id.txtDishName)
        val dishPrice: TextView = view.findViewById(R.id.txtPrice)
        val slNo: TextView = view.findViewById(R.id.txtSlNo)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuVewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)
        return MenuVewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuVewHolder, position: Int) {
        val dish = dishList[position]
        val dishEntity = DishEntity(
            position + 1,
            dish.dishName,
            dish.dishPrice
        )

        holder.slNo.text = (position + 1).toString()
        holder.dishName.text = dish.dishName
        holder.dishPrice.text = "Rs. ${dish.dishPrice}"
        val tomatoColor = ContextCompat.getColor(context, R.color.tomato)
        val yellowColor = ContextCompat.getColor(context, R.color.yellow)
        var checkSelected = RestaurantDetailsActivity.DBAsyncTask(context, dishEntity, 1).execute()
        var isSelected = checkSelected.get()
        if (isSelected) {
            holder.btnAdd.setBackgroundColor(yellowColor)
            holder.btnAdd.text = "Remove"
        }

        holder.btnAdd.setOnClickListener {
            checkSelected = RestaurantDetailsActivity.DBAsyncTask(context, dishEntity, 1).execute()
            isSelected = checkSelected.get()
            if (isSelected) {
                val async = RestaurantDetailsActivity.DBAsyncTask(context, dishEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Item removed from cart",
                        Toast.LENGTH_SHORT
                    ).show()
                    sharedPreferences.edit().putInt("items_selected", sharedPreferences.getInt("items_selected",0) - 1).commit()
                    val value = sharedPreferences.getInt("amount",0) - dish.dishPrice.toInt()
                    sharedPreferences.edit().putInt("amount",value).commit()
                    holder.btnAdd.setBackgroundColor(tomatoColor)
                    holder.btnAdd.text = "Add"
                } else {
                    Toast.makeText(
                        context,
                        "Some Error Occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async = RestaurantDetailsActivity.DBAsyncTask(context, dishEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Item added to cart",
                        Toast.LENGTH_SHORT
                    ).show()
                    sharedPreferences.edit().putInt("items_selected", sharedPreferences.getInt("items_selected",0) + 1).commit()
                    val value = sharedPreferences.getInt("amount",0) + dish.dishPrice.toInt()
                    sharedPreferences.edit().putInt("amount",value).commit()
                    holder.btnAdd.setBackgroundColor(yellowColor)
                    holder.btnAdd.text = "Remove"
                } else {
                    Toast.makeText(
                        context,
                        "Some Error Occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
//            println("Value ${(noItemsSelected[0])}")
            if (sharedPreferences.getInt("items_selected",0) > 0)
                btnProceedCart.visibility = View.VISIBLE
            else
                btnProceedCart.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return dishList.size
    }
}