package com.ramith.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ramith.foodrunner.R
import com.ramith.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button
    lateinit var txtForgotPassword: TextView
    lateinit var txtSignUp: TextView

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        if (sharedPreferences.getBoolean("LoggedIn", false)) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else
            setContentView(R.layout.activity_login)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtSignUp = findViewById(R.id.txtSignup)

        btnLogin.setOnClickListener {
            val mobileNumber = etMobileNumber.text.toString()
            val password = etPassword.text.toString()
            if (mobileNumber.length != 10) {
                Toast.makeText(
                    this@LoginActivity,
                    "Invalid mobile number",
                    Toast.LENGTH_SHORT
                ).show()
            } else {


                val queue = Volley.newRequestQueue(this@LoginActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                val details = JSONObject()
                details.put("mobile_number", mobileNumber)
                details.put("password", password)

                if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                    val postRequest =
                        object :
                            JsonObjectRequest(Request.Method.POST, url, details, Response.Listener {
                                try {
                                    val jsonObject = it.getJSONObject("data")
                                    val success = jsonObject.getBoolean("success")
                                    if (success) {
                                        val data = jsonObject.getJSONObject("data")

                                        sharedPreferences.edit()
                                            .putString("Id", data.getString("user_id")).apply()
                                        sharedPreferences.edit()
                                            .putString("Name", data.getString("name")).apply()
                                        sharedPreferences.edit()
                                            .putString("Email", data.getString("email")).apply()
                                        sharedPreferences.edit().putString(
                                            "Mobile Number",
                                            data.getString("mobile_number")
                                        ).apply()
                                        sharedPreferences.edit().putString(
                                            "Delivery Address",
                                            data.getString("address")
                                        ).apply()
                                        sharedPreferences.edit().putBoolean("LoggedIn", true)
                                            .apply()

                                        val intent =
                                            Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Invalid Credentials",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Some unexpected error occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Volley error occurred!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "7b39ec930e790b"
                                return headers
                            }
                        }
                    queue.add(postRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(settingsIntent)
                    }
                    dialog.setNegativeButton("Close") { text, listener ->
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }

        txtSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(intent)
        }
        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}