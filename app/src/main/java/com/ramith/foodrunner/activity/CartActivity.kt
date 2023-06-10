package com.ramith.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
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
import com.ramith.foodrunner.adapter.RecyclerCartAdapter
import com.ramith.foodrunner.database.DishDatabase
import com.ramith.foodrunner.database.DishEntity
import com.ramith.foodrunner.fragment.HomeFragment
import com.ramith.foodrunner.fragment.OrderHistoryFragment
import com.ramith.foodrunner.model.Dish
import com.ramith.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RecyclerCartAdapter
    lateinit var toolbar: Toolbar
    lateinit var btnPlaceOrder: Button
    lateinit var sharedPreferences: SharedPreferences
    lateinit var restaurantId: String
    lateinit var restaurantName: String
    lateinit var btnOk: Button
    lateinit var txtCart: TextView
    lateinit var btnViewOrderHistory: Button
    var dishList = arrayListOf<DishEntity>()
    var dishdbList = listOf<DishEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if(intent != null){
//            restaurantId = intent.getStringExtra("restaurant_id").toString()
//            restaurantName = intent.getStringExtra("restaurant_name").toString()
//        }

        sharedPreferences = getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_cart)

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        toolbar = findViewById(R.id.toolbar)
        txtCart = findViewById(R.id.txtCart)
        txtCart.text = "Ordering From: ${(sharedPreferences.getString("restaurant_name",null))}"

        setUpToolbar()

        dishdbList = DBAsyncTask(applicationContext, 1).execute().get()
        dishList.addAll(dishdbList)

//        for (i in 0 until dishdbList.size) {
//            val dishObject = Dish(
//                dishdbList[i].dish_id.toString(),
//                dishdbList[i].dishName,
//                dishdbList[i].dishPrice
//            )
//            dishList.add(dishObject)
//        }

        layoutManager = LinearLayoutManager(this@CartActivity)
        recyclerAdapter =
            RecyclerCartAdapter(
                this@CartActivity,
                dishList,
                sharedPreferences,
                btnPlaceOrder
            )
        btnPlaceOrder.text = "Place Order(Total Rs. ${(sharedPreferences.getInt("amount",0))})"
        recyclerCart = findViewById(R.id.recyclerCart)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager

        btnPlaceOrder.text = "Place Order(Total Rs. ${(sharedPreferences.getInt("amount",0))})"
        btnPlaceOrder.setOnClickListener {
            val queue = Volley.newRequestQueue(this@CartActivity)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"


            if (ConnectionManager().checkConnectivity(this@CartActivity)) {

                val params = JSONObject()
                params.put("user_id", sharedPreferences.getString("Id", null))
                params.put("restaurant_id", sharedPreferences.getString("restaurant_id",null))
                params.put("total_cost", sharedPreferences.getInt("amount",0).toString())
                val foodIdList = JSONArray()
                for (i in 0 until dishdbList.size) {
                    val foodJsonObject = JSONObject()
                    foodJsonObject.put("food_item_id", dishdbList[i].dish_id.toString())
                    foodIdList.put(foodJsonObject)
                    //println("JSON $foodJsonObject")
                }
                params.put("food", foodIdList)

                //println("LISTT $params")
                val jsonRequest =
                    object : JsonObjectRequest(Request.Method.POST, url, params, Response.Listener {
                        try {
                            //println("RESPONSE $it")
                            val jsonObject = it.getJSONObject("data")
                            val success = jsonObject.getBoolean("success")
                            if (success) {
                                setContentView(R.layout.activity_successful)
                                btnOk = findViewById(R.id.btnOk)
                                btnViewOrderHistory = findViewById(R.id.btnViewOrderHistory)

                                RestaurantDetailsActivity.DBAsyncTask(this@CartActivity, null, 4).execute()
                                btnOk.setOnClickListener {
                                    val intent = Intent(this@CartActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                btnViewOrderHistory.setOnClickListener {
                                    val intent = Intent(this@CartActivity, MainActivity::class.java)
                                    intent.putExtra("open_history", true)
                                    startActivity(intent)
                                    finish()
                                }

                            } else {
                                Toast.makeText(
                                    this@CartActivity,
                                    "Some Error Occurred!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@CartActivity,
                                "Some Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(
                            this@CartActivity,
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
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
//        println("AMOUNT ${(amount[0])}")
    }

    override fun onResume() {
        super.onResume()
//        println("AMOUNTR ${(amount[0])}")
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@CartActivity, RestaurantDetailsActivity::class.java)
        startActivity(intent)
        finish()
    }

    class DBAsyncTask(val context: Context, val mode: Int) :
        AsyncTask<Void, Void, List<DishEntity>>() {
        val db = Room.databaseBuilder(context, DishDatabase::class.java, "menu-db").build()
        override fun doInBackground(vararg p0: Void?): List<DishEntity>? {
            when (mode) {
                1 -> {
                    val dish: List<DishEntity> =
                        db.dishDao().getAllDishes()
                    db.close()
                    return dish
                }
            }
            return null
        }
    }
}