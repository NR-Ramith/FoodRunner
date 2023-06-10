package com.ramith.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ramith.foodrunner.R
import com.ramith.foodrunner.adapter.RecyclerHomeAdapter
import com.ramith.foodrunner.database.HotelDatabase
import com.ramith.foodrunner.database.HotelEntity
import com.ramith.foodrunner.model.Hotel
import com.ramith.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerHome: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RecyclerHomeAdapter
    lateinit var sharedPreferences: SharedPreferences

    var hotelInfoList = arrayListOf<Hotel>()

    var alphabetComaparator = Comparator<Hotel> { hotel1, hotel2 ->
        hotel1.hotelName.compareTo(hotel2.hotelName, true)
    }

    var ratingComparator = Comparator<Hotel> { hotel1, hotel2 ->
        if(hotel1.hotelRating.compareTo(hotel2.hotelRating) == 0)
            hotel2.hotelName.compareTo(hotel1.hotelName, true)
        else
            hotel1.hotelRating.compareTo(hotel2.hotelRating)
    }

    var costComparator = Comparator<Hotel> { hotel1, hotel2 ->
        if(hotel1.hotelCostForOne.toInt().compareTo(hotel2.hotelCostForOne.toInt()) == 0)
            hotel1.hotelName.compareTo(hotel2.hotelName, true)
        else
            hotel1.hotelCostForOne.toInt().compareTo(hotel2.hotelCostForOne.toInt())
    }

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
        setHasOptionsMenu(true)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        val jsonObject = it.getJSONObject("data")
                        val success = jsonObject.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val data = jsonObject.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val hotelJsonObject = data.optJSONObject(i)
                                val hotelObject = Hotel(
                                    hotelJsonObject.getString("id"),
                                    hotelJsonObject.getString("name"),
                                    hotelJsonObject.getString("rating"),
                                    hotelJsonObject.getString("cost_for_one"),
                                    hotelJsonObject.getString("image_url")
                                )
                                hotelInfoList.add(hotelObject)
                            }


                            layoutManager = LinearLayoutManager(activity)
                            recyclerAdapter =
                                RecyclerHomeAdapter(activity as Context, hotelInfoList, sharedPreferences)
                            recyclerHome = view.findViewById(R.id.recyclerHome)
                            recyclerHome.adapter = recyclerAdapter
                            recyclerHome.layoutManager = layoutManager
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some unexpected error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "7b39ec930e790b"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if (id == R.id.action_alphasort) {
            Collections.sort(hotelInfoList, alphabetComaparator)
        } else if(id == R.id.action_ratingsort){
            Collections.sort(hotelInfoList, ratingComparator)
            hotelInfoList.reverse()
        } else if (id == R.id.action_costsort){
            Collections.sort(hotelInfoList, costComparator)
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    class DBAsyncTask(val context: Context, val hotelEntity: HotelEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        lateinit var sharedPreferences: SharedPreferences
        val db = Room.databaseBuilder(context, HotelDatabase::class.java, "hotel-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            sharedPreferences = context.getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
            when (mode) {
                1 -> {
                    val hotel: HotelEntity? =
                        db.hotelDao().getHotelById(hotelEntity.hotel_id)
                    db.close()
                    return hotel != null
                }
                2 -> {
                    db.hotelDao().insertHotel(hotelEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.hotelDao().deleteHotel(hotelEntity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}