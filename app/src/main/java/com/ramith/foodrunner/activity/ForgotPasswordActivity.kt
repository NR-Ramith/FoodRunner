package com.ramith.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
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


class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button

    //    lateinit var txtDisplayHeader: TextView
//    lateinit var txtDisplay: TextView
//    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        sharedPreferences =
//            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_forgot_password)

        etMobileNumber = findViewById(R.id.etMobileNumberFPpage)
        etEmail = findViewById(R.id.etEmailFPpage)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            val mobileNumber = etMobileNumber.text.toString()
            val email = etEmail.text.toString()


            if (mobileNumber.length != 10) {
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Incorrect Mobile Number",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                val details = JSONObject()
                details.put("mobile_number", mobileNumber)
                details.put("email", email)

                if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {
                    val postRequest =
                        object :
                            JsonObjectRequest(Request.Method.POST, url, details, Response.Listener {
                                try {

                                    val jsonObject = it.getJSONObject("data")
                                    val success = jsonObject.getBoolean("success")
                                    if (success) {
                                        val intent = Intent(
                                            this@ForgotPasswordActivity,
                                            ConfirmPasswordActivity::class.java
                                        )
                                        intent.putExtra("mobile_number", mobileNumber)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,
                                            "Invalid Credentials",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@ForgotPasswordActivity,
                                        "Some unexpected error occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this@ForgotPasswordActivity,
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
                    val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
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


//            if(mobileNumber == sharedPreferences.getString("Mobile Number",null) && email == sharedPreferences.getString("Email",null)){
//                setContentView(R.layout.activity_displayinfo)
//                txtDisplayHeader = findViewById(R.id.txtDisplayHeader)
//                txtDisplay = findViewById(R.id.txtDisplay)
//                txtDisplayHeader.text = "Here is your Info!"
//                txtDisplay.text = "Your Profile\nName - ${sharedPreferences.getString("Name",null)}\n" +
//                        "Email - ${sharedPreferences.getString("Email",null)}\n" +
//                        "Mobile Number - ${sharedPreferences.getString("Mobile Number","NA")}\n" +
//                        "Address - ${sharedPreferences.getString("Delivery Address","NA")}\n" +
//                        "Password - ${sharedPreferences.getString("Password",null)}\n"
//            }
//            else{
//                Toast.makeText(
//                    this@ForgotPasswordActivity,
//                    "Invalid credentials",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}