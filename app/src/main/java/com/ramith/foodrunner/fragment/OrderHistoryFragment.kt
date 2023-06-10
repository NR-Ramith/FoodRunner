package com.ramith.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ramith.foodrunner.R
import com.ramith.foodrunner.adapter.RecyclerHistoryAdapter
import com.ramith.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OrderHistoryFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var layoutAdapter: RecyclerHistoryAdapter
    lateinit var sharedPreferences: SharedPreferences
    lateinit var progressLayout: RelativeLayout

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
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        var datetimeComparator = Comparator<JSONObject> { obj1, obj2 ->
            val delimiter = " "

            val date1 = obj1.getString("order_placed_at")
            val date2 = obj2.getString("order_placed_at")
            val list1 = date1.split(delimiter)
            val list2 = date2.split(delimiter)

            val sdf = SimpleDateFormat("dd-mm-yyyy")


            val firstDate: Date = sdf.parse(list1[0])
            val secondDate: Date = sdf.parse(list2[0])

            val cmp = firstDate.compareTo(secondDate)
            when {
                cmp != 0 -> cmp
                else -> list1[1].compareTo(list2[1])
            }
        }

        sharedPreferences = requireActivity().getSharedPreferences("FoodRunner Preferences", Context.MODE_PRIVATE)
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/${(sharedPreferences.getString("Id", null))}"


        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        val jsonObject = it.getJSONObject("data")
                        val success = jsonObject.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val data = jsonObject.getJSONArray("data")
                            val datalist = arrayListOf<JSONObject>()
                            for (i in 0 until data.length())
                                datalist.add(data[i] as JSONObject)
                            Collections.sort(datalist, datetimeComparator)
                            recyclerView = view.findViewById(R.id.recyclerHistory)
                            layoutAdapter = RecyclerHistoryAdapter(activity as Context, datalist)
                            layoutManager = LinearLayoutManager(activity)
                            recyclerView.layoutManager = layoutManager
                            recyclerView.adapter = layoutAdapter

                            recyclerView.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerView.context,
                                    (layoutManager as LinearLayoutManager).orientation
                                )
                            )
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some Error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            activity as Context,
                            "Some Error Occurred!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(
                        activity as Context,
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

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}