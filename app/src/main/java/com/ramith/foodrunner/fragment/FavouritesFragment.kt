package com.ramith.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ramith.foodrunner.R
import com.ramith.foodrunner.adapter.RecyclerFavouriteAdapter
import com.ramith.foodrunner.database.HotelDatabase
import com.ramith.foodrunner.database.HotelEntity

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavouritesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerFavourite: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RecyclerFavouriteAdapter
    var dbHotelList = listOf<HotelEntity>()
    var hotelList = arrayListOf<HotelEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        layoutManager = LinearLayoutManager(activity as Context)
        dbHotelList = RetrieveFavourites(activity as Context).execute().get()
        println("HOTELT ${dbHotelList}")
        hotelList.addAll(dbHotelList)

        println("HOTELT $hotelList")
        if(activity != null){
            try {
            progressLayout.visibility = View.GONE
            recyclerAdapter = RecyclerFavouriteAdapter(activity as Context, hotelList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager}
            catch (e: Exception){
                println("ERRR")
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<HotelEntity>>(){
        override fun doInBackground(vararg p0: Void?): List<HotelEntity> {
            val db = Room.databaseBuilder(context, HotelDatabase::class.java, "hotel-db").build()
            val sharedPreferences = context.getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
            return db.hotelDao().getAllHotels("%/${(sharedPreferences.getString("Id","0").toString())}%")
        }
    }
}