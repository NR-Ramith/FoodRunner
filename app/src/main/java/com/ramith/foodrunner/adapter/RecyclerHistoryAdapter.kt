package com.ramith.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ramith.foodrunner.R
import org.json.JSONArray
import org.json.JSONObject

class RecyclerHistoryAdapter(val context: Context, var orderList: ArrayList<JSONObject>) :
    RecyclerView.Adapter<RecyclerHistoryAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val hotelName: TextView = view.findViewById(R.id.txtHotelName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerHistory2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_single_row, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val order = orderList[position]
        val layoutManager = LinearLayoutManager(context)
        val layoutAdapter = RecyclerHistory2Adapter(context, order.getJSONArray("food_items"))
        holder.hotelName.text = order.getString("restaurant_name")
        holder.txtDate.text = order.getString("order_placed_at")
        holder.recyclerView.layoutManager = layoutManager
        holder.recyclerView.adapter = layoutAdapter
    }

    override fun getItemCount(): Int {
        return orderList.size
    }
}