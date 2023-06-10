package com.ramith.foodrunner.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.ramith.foodrunner.R
import com.ramith.foodrunner.activity.CartActivity
import com.ramith.foodrunner.activity.RestaurantDetailsActivity
import com.ramith.foodrunner.database.HotelEntity
import com.ramith.foodrunner.fragment.HomeFragment
import com.ramith.foodrunner.model.Hotel
import com.squareup.picasso.Picasso

class RecyclerHomeAdapter(val context: Context, val hotelList: ArrayList<Hotel>, val sharedPreferences: SharedPreferences): RecyclerView.Adapter<RecyclerHomeAdapter.HomeViewHolder>() {
    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtHotelName: TextView = view.findViewById(R.id.txtHotelName)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val txtCostForOne: TextView = view.findViewById(R.id.txtCostForOne)
        val imgImage: ImageView = view.findViewById(R.id.imgHotelImage)
        val relativeLayout: RelativeLayout = view.findViewById(R.id.rlLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val hotel = hotelList[position]
        holder.txtHotelName.text = hotel.hotelName
        holder.txtCostForOne.text = "${hotel.hotelCostForOne}/person"
        holder.txtRating.text = hotel.hotelRating
        Picasso.get().load(hotel.hotelImage).error(R.drawable.ic_fork_knife).into(holder.imgImage)

        val hotelEntity = HotelEntity(
            "${hotel.hotelId}/${sharedPreferences.getString("Id","0").toString()}",
            hotel.hotelName,
            hotel.hotelRating,
            hotel.hotelCostForOne,
            hotel.hotelImage
        )

//        println("HOTELE $hotelEntity")
        holder.relativeLayout.setOnClickListener {
            sharedPreferences.edit().putString("restaurant_id", hotel.hotelId).commit()
            sharedPreferences.edit().putString("restaurant_name", hotel.hotelName).commit()
            sharedPreferences.edit().putString("restaurant_rating", hotel.hotelRating).commit()
            sharedPreferences.edit().putString("restaurant_cost", hotel.hotelCostForOne).commit()
            sharedPreferences.edit().putString("restaurant_image", hotel.hotelImage).commit()
            sharedPreferences.edit().putInt("items_selected",0).commit()
            sharedPreferences.edit().putInt("amount",0).commit()
            RestaurantDetailsActivity.DBAsyncTask(context, null, 4).execute()
            val intent = Intent(context, RestaurantDetailsActivity::class.java)
//            intent.putExtra("restaurant_id", hotel.hotelId)
//            intent.putExtra("restaurant_name", hotel.hotelName)
//            intent.putExtra("restaurant_rating", hotel.hotelRating)
//            intent.putExtra("restaurant_cost", hotel.hotelCostForOne)
//            intent.putExtra("restaurant_image", hotel.hotelImage)
            context.startActivity(intent)
            ActivityCompat.finishAffinity(context as Activity)
        }


        var checkFav = HomeFragment.DBAsyncTask(context, hotelEntity, 1).execute()
        var isFav = checkFav.get()

        if (isFav)
            holder.txtRating.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favourite_red, 0, 0)
        else
            holder.txtRating.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favourite_border, 0, 0)

        holder.txtRating.setOnClickListener {
            checkFav = HomeFragment.DBAsyncTask(context, hotelEntity, 1).execute()
            isFav = checkFav.get()
            if (isFav){
                val async = HomeFragment.DBAsyncTask(context, hotelEntity, 3).execute()
                val result = async.get()
                if (result){
                    Toast.makeText(
                        context,
                        "Hotel removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.txtRating.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favourite_border, 0, 0)
                } else{
                    Toast.makeText(
                        context,
                        "Some Error Occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else{
                val async = HomeFragment.DBAsyncTask(context, hotelEntity, 2).execute()
                val result = async.get()
                if (result){
                    Toast.makeText(
                        context,
                        "Hotel added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.txtRating.setCompoundDrawablesRelativeWithIntrinsicBounds(0, R.drawable.ic_favourite_red, 0, 0)
                } else{
                    Toast.makeText(
                        context,
                        "Some Error Occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return hotelList.size
    }
}