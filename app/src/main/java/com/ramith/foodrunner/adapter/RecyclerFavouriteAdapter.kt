package com.ramith.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ramith.foodrunner.R
import com.ramith.foodrunner.database.HotelEntity
import com.ramith.foodrunner.fragment.HomeFragment
import com.squareup.picasso.Picasso

class RecyclerFavouriteAdapter(val context: Context, val hotelList: ArrayList<HotelEntity>): RecyclerView.Adapter<RecyclerFavouriteAdapter.FavouriteViewHolder>() {
    class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtHotelName: TextView = view.findViewById(R.id.txtHotelName)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val txtCostForOne: TextView = view.findViewById(R.id.txtCostForOne)
        val imgImage: ImageView = view.findViewById(R.id.imgHotelImage)
        val viewBorder: View = view.findViewById(R.id.viewDrawableBorderFF)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val hotel = hotelList[position]
        holder.txtHotelName.text = hotel.hotelName
        holder.txtCostForOne.text = hotel.hotelCostForOne
        holder.txtRating.text = hotel.hotelRating
        Picasso.get().load(hotel.hotelImage).error(R.drawable.ic_fork_knife).into(holder.imgImage)
        holder.txtRating.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favourite_red, 0, 0)

        holder.viewBorder.setOnClickListener {
            holder.txtRating.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favourite_border, 0, 0)
            HomeFragment.DBAsyncTask(context, hotel, 3).execute()
            hotelList.remove(hotel)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return hotelList.size
    }
}