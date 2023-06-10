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

class ConfirmPasswordActivity : AppCompatActivity() {
    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button
    lateinit var mobileNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_password)

        if (intent != null) {
            mobileNumber = intent.getStringExtra("mobile_number").toString()
        } else {
            Toast.makeText(
                this@ConfirmPasswordActivity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPasswordCPpage)
        etConfirmPassword = findViewById(R.id.etConfirmPasswordCPpage)
        btnSubmit = findViewById(R.id.btnSubmitCPpage)

        btnSubmit.setOnClickListener {
            val otp = etOTP.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()


            if (otp.length != 4) {
                Toast.makeText(
                    this@ConfirmPasswordActivity,
                    "Incorrect OTP",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (newPassword.length < 4) {
                Toast.makeText(
                    this@ConfirmPasswordActivity,
                    "Password needs to be of min. 4 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (confirmPassword != newPassword) {
                Toast.makeText(
                    this@ConfirmPasswordActivity,
                    "Passwords do not match",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val queue = Volley.newRequestQueue(this@ConfirmPasswordActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val details = JSONObject()
                details.put("mobile_number", mobileNumber)
                details.put("password", newPassword)
                details.put("otp", otp)

                if (ConnectionManager().checkConnectivity(this@ConfirmPasswordActivity)) {
                    val postRequest =
                        object :
                            JsonObjectRequest(Request.Method.POST, url, details, Response.Listener {
                                try {
                                    val jsonObject = it.getJSONObject("data")
                                    val success = jsonObject.getBoolean("success")
                                    if (success) {
                                        Toast.makeText(
                                            this@ConfirmPasswordActivity,
                                            jsonObject.getString("successMessage"),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(
                                            this@ConfirmPasswordActivity,
                                            LoginActivity::class.java
                                        )
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(
                                            this@ConfirmPasswordActivity,
                                            "Invalid Credentials",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this@ConfirmPasswordActivity,
                                        "Some unexpected error occurred!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this@ConfirmPasswordActivity,
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
                    val dialog = AlertDialog.Builder(this@ConfirmPasswordActivity)
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

    override fun onBackPressed() {
        val intent = Intent(this@ConfirmPasswordActivity, ForgotPasswordActivity::class.java)
        intent.putExtra("mobile_number", mobileNumber)
        startActivity(intent)
        finish()
    }
}