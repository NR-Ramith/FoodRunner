package com.ramith.foodrunner.activity


import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.ramith.foodrunner.R
import com.ramith.foodrunner.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class RegistrationActivity : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumberReg: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etPasswordReg: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
//    lateinit var txtDisplay: TextView
//    lateinit var txtDisplayHeader: TextView
    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file), MODE_PRIVATE)

        setContentView(R.layout.activity_registration)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumberReg = findViewById(R.id.etMobileNumberReg)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etPasswordReg = findViewById(R.id.etPasswordReg)
        etConfirmPassword = findViewById(R.id.etPasswordConfirmation)
        btnRegister = findViewById(R.id.btnRegister)
        toolbar = findViewById(R.id.toolbar)

        setUpToolBar()

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val mobileNumber = etMobileNumberReg.text.toString()
            val deliveryAddress = etDeliveryAddress.text.toString()
            val password = etPasswordReg.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (name.length < 3) {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Name must be of minimum 3 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (email == "") {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Email is required",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (mobileNumber.length != 10) {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Incorrect mobile number",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (deliveryAddress.length == 0) {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Delivery address required",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password.length < 4) {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Password must be of minimum 4 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (password != confirmPassword) {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Password entered doesn't match",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

//                setContentView(R.layout.activity_displayinfo)
//                txtDisplay = findViewById(R.id.txtDisplay)
//                txtDisplayHeader = findViewById(R.id.txtDisplayHeader)
//                txtDisplayHeader.text = "Thank You for Registering!"
//                txtDisplay.text =
//                    "Your Profile\nName - ${sharedPreferences.getString("Name", null)}\n" +
//                            "Email - ${sharedPreferences.getString("Email", null)}\n" +
//                            "Mobile Number - ${sharedPreferences.getString("Mobile Number", "NA")}\n" +
//                            "Address - ${sharedPreferences.getString("Delivery Address", "NA")}\n" +
//                            "Password - ${sharedPreferences.getString("Password", null)}\n"

                val queue = Volley.newRequestQueue(this@RegistrationActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val details = JSONObject()
                details.put("name", name)
                details.put("mobile_number", mobileNumber)
                details.put("password", password)
                details.put("address", deliveryAddress)
                details.put("email", email)

                if (ConnectionManager().checkConnectivity(this@RegistrationActivity)) {
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

                                        val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@RegistrationActivity,
                                            "Some Error Occurred!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@RegistrationActivity,
                                        "Some unexpected error occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this@RegistrationActivity,
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
                    val dialog = AlertDialog.Builder(this@RegistrationActivity)
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
    }

    fun setUpToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home)
            super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}