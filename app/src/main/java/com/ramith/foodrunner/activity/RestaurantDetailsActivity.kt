package com.ramith.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ramith.foodrunner.R
import com.ramith.foodrunner.adapter.RecyclerMenuAdapter
import com.ramith.foodrunner.database.DishDatabase
import com.ramith.foodrunner.database.DishEntity
import com.ramith.foodrunner.database.HotelDatabase
import com.ramith.foodrunner.database.HotelEntity
import com.ramith.foodrunner.fragment.HomeFragment
import com.ramith.foodrunner.model.Dish
import com.ramith.foodrunner.model.Hotel
import com.ramith.foodrunner.util.ConnectionManager
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class RestaurantDetailsActivity : AppCompatActivity() {
    lateinit var recyclerMenu: RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RecyclerMenuAdapter
    lateinit var toolbar: Toolbar
    lateinit var btnProceedCart: Button
    lateinit var txtMenu: TextView
    lateinit var viewDrawable: View
    var dishList = arrayListOf<Dish>()

    lateinit var hotelEntity: HotelEntity
    lateinit var sharedPreferences: SharedPreferences

    var alphabetComaparator = Comparator<Dish> { dish1, dish2 ->
        dish1.dishName.compareTo(dish2.dishName, true)
    }

    var costComparator = Comparator<Dish> { dish1, dish2 ->
        if(dish1.dishPrice.toInt().compareTo(dish2.dishPrice.toInt()) == 0)
            dish1.dishName.compareTo(dish2.dishName, true)
        else
            dish1.dishPrice.toInt().compareTo(dish2.dishPrice.toInt())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if(intent != null){
//            restaurantId = intent.getStringExtra("restaurant_id").toString()
//            restaurantName = intent.getStringExtra("restaurant_name").toString()
//            restaurantRating = intent.getStringExtra("restaurant_rating").toString()
//            restaurantCost = intent.getStringExtra("restaurant_cost").toString()
//            restaurantImage = intent.getStringExtra("restaurant_image").toString()
//        }

        sharedPreferences = getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)


        setContentView(R.layout.activity_restaurant_details)

        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        btnProceedCart = findViewById(R.id.btnProceedCart)
        toolbar = findViewById(R.id.toolbar)
        txtMenu = findViewById(R.id.txtMenu)
        viewDrawable = findViewById(R.id.viewDrawableBorderRDpage)
        progressLayout.visibility = View.VISIBLE

        setUpToolbar()


        hotelEntity = HotelEntity(
            "${sharedPreferences.getString("restaurant_id", null)!!.toInt()}/${sharedPreferences.getString("Id","0").toString()}",
            sharedPreferences.getString("restaurant_name", null).toString(),
            sharedPreferences.getString("restaurant_rating", null).toString(),
            sharedPreferences.getString("restaurant_cost", null).toString(),
            sharedPreferences.getString("restaurant_image", null).toString())
        val queue = Volley.newRequestQueue(this@RestaurantDetailsActivity)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/${(sharedPreferences.getString("restaurant_id", null))}"


        if (ConnectionManager().checkConnectivity(this@RestaurantDetailsActivity)) {
            val jsonRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        //println("Respnosee $it")
                        val jsonObject = it.getJSONObject("data")
                        val success = jsonObject.getBoolean("success")
                        if (success) {
                            val checkFav = HomeFragment.DBAsyncTask(applicationContext, hotelEntity, 1).execute().get()
                            if (checkFav)
                                txtMenu.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_favourite_red, 0)
                            else
                                txtMenu.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_favourite_border, 0)

                            if (sharedPreferences.getInt("items_selected",0) == 0){
                                btnProceedCart.visibility = View.GONE
                            }else{
                                btnProceedCart.visibility = View.VISIBLE
                            }

                            progressLayout.visibility = View.GONE
                            val data = jsonObject.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val dishJsonObject = data.optJSONObject(i)
                                val dishObject = Dish(
                                    dishJsonObject.getString("id"),
                                    dishJsonObject.getString("name"),
                                    dishJsonObject.getString("cost_for_one")
                                )
                                dishList.add(dishObject)
                            }
                            layoutManager = LinearLayoutManager(this@RestaurantDetailsActivity)
                            recyclerAdapter =
                                RecyclerMenuAdapter(
                                    this@RestaurantDetailsActivity,
                                    dishList,
                                    btnProceedCart,
                                    sharedPreferences
                                )
                            recyclerMenu = findViewById(R.id.recyclerMenu)
                            recyclerMenu.adapter = recyclerAdapter
                            recyclerMenu.layoutManager = layoutManager

                            btnProceedCart.setOnClickListener {
                                val intent = Intent(this@RestaurantDetailsActivity, CartActivity::class.java)
//                                intent.putExtra("restaurant_id", restaurantId)
//                                intent.putExtra("restaurant_name", restaurantName)
                                println("AMOUNTT ${(sharedPreferences.getInt("amount", 0))}")
                                startActivity(intent)
                            }

                            if (sharedPreferences.getInt("items_selected",0) > 0)
                                btnProceedCart.visibility = View.VISIBLE
                            else
                                btnProceedCart.visibility = View.GONE

                        } else {
                            Toast.makeText(
                                this@RestaurantDetailsActivity,
                                "Some Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RestaurantDetailsActivity,
                            "Some Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        this@RestaurantDetailsActivity,
                        "Volley Error $it",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "7b39ec930e790b"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this@RestaurantDetailsActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@RestaurantDetailsActivity)
            }
            dialog.create()
            dialog.show()
        }



        viewDrawable.setOnClickListener{
            val checkFav = HomeFragment.DBAsyncTask(applicationContext, hotelEntity, 1).execute().get()
            if (checkFav){
                txtMenu.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_favourite_border, 0)
                HomeFragment.DBAsyncTask(applicationContext, hotelEntity, 3).execute()
            } else{
                txtMenu.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_favourite_red, 0)
                HomeFragment.DBAsyncTask(applicationContext, hotelEntity, 2).execute()
            }
        }
    }

    class DBAsyncTask(val context: Context, val dishEntity: DishEntity?, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, DishDatabase::class.java, "menu-db").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val dish: DishEntity? =
                        db.dishDao().getDishById(dishEntity?.dish_id.toString())
                    db.close()
                    return dish != null
                }
                2 -> {
                    if (dishEntity != null) {
                        db.dishDao().insertDish(dishEntity)
                    }
                    db.close()
                    return true
                }
                3 -> {
                    if (dishEntity != null) {
                        db.dishDao().deleteDish(dishEntity)
                    }
                    db.close()
                    return true
                }
                4 -> {
                    db.dishDao().clearDishes()
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Hotel Name"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        } else if (id == R.id.action_alphasort) {
            Collections.sort(dishList, alphabetComaparator)
        } else if (id == R.id.action_costsort){
            Collections.sort(dishList, costComparator)
        }
        recyclerAdapter.notifyDataSetChanged()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_food, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        val async = DBAsyncTask(this@RestaurantDetailsActivity, null, 4).execute()
        val cleared = async.get()
        if (cleared){
            val intent = Intent(this@RestaurantDetailsActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            Toast.makeText(
                this@RestaurantDetailsActivity,
                "Some Error Occurred",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}