package com.ramith.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ramith.foodrunner.R
import org.json.JSONArray

class RecyclerHistory2Adapter(val context: Context, var orderList: JSONArray) :
    RecyclerView.Adapter<RecyclerHistory2Adapter.History2ViewHolder>() {
    class History2ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishPrice: TextView = view.findViewById(R.id.txtDishPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): History2ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row, parent, false)
        return History2ViewHolder(view)
    }

    override fun onBindViewHolder(holder: History2ViewHolder, position: Int) {
        val dish = orderList.optJSONObject(position)
        holder.txtDishName.text = dish.getString("name")
        holder.txtDishPrice.text = dish.getString("cost")
    }

    override fun getItemCount(): Int {
        return orderList.length()
    }
}