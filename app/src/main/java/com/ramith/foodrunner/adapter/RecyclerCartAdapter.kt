package com.ramith.foodrunner.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.ramith.foodrunner.R
import com.ramith.foodrunner.activity.CartActivity
import com.ramith.foodrunner.activity.RestaurantDetailsActivity
import com.ramith.foodrunner.database.DishEntity

class RecyclerCartAdapter(val context: Context, var dishList: ArrayList<DishEntity>, val sharedPreferences: SharedPreferences, val btnPlaceOrder: Button) :
    RecyclerView.Adapter<RecyclerCartAdapter.CartViewHolder>() {
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnRemove: Button = view.findViewById(R.id.btnRemove)
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishPrice: TextView = view.findViewById(R.id.txtDishPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val dish = dishList[position]
        //sharedPreferences.edit().putInt("amount",sharedPreferences.getInt("amount",0) + dish.dishPrice.toInt())

//        println("AMOUNT ${(amount[0])}")

        holder.txtDishName.text = dish.dishName
        holder.txtDishPrice.text = "Rs. ${dish.dishPrice}"
        holder.btnRemove.setOnClickListener {
            val async = RestaurantDetailsActivity.DBAsyncTask(context, dish, 3).execute()
            val removed = async.get()
            if (removed){
                sharedPreferences.edit().putInt("items_selected",sharedPreferences.getInt("items_selected",0) - 1).commit()
                sharedPreferences.edit().putInt("amount",sharedPreferences.getInt("amount",0) - dish.dishPrice.toInt()).commit()
                Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT)
                btnPlaceOrder.text = "Place Order(Total Rs. ${(sharedPreferences.getInt("amount",0))})"
                dishList.remove(dish)
                notifyDataSetChanged()
            } else{
                Toast.makeText(context, "Some Error occurred", Toast.LENGTH_SHORT)
            }

            if (sharedPreferences.getInt("items_selected",0) == 0){
                val intent = Intent(context, RestaurantDetailsActivity::class.java)
                //intent.putExtra()
                context.startActivity(intent)
                (context as Activity).finish()
            }
        }
    }

    override fun getItemCount(): Int {
        return dishList.size
    }
}