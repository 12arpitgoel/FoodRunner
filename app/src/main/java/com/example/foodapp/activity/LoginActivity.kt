package com.example.foodapp.activity

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
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.util.ConnectionManager
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var edtMobileNumber: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtForgotPassword: TextView
    private lateinit var txtSignUpNew: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("logged_in", false)

        setContentView(R.layout.activity_login)

        if (isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        edtMobileNumber = findViewById(R.id.edtMobileNumber)
        edtPassword = findViewById(R.id.edtPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        this.txtSignUpNew = findViewById(R.id.txtSignUpNew)

        txtForgotPassword.setOnClickListener {
            val intent = Intent(
                this@LoginActivity,
                ForgotPassword::class.java
            )
            startActivity(intent)
        }

        txtSignUpNew.setOnClickListener {
            val intent = Intent(
                this@LoginActivity,
                RegistrationPage::class.java
            )
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            if (edtMobileNumber.text.length != 10 || edtPassword.text.length < 4)
                Toast.makeText(this@LoginActivity, "Invalid number or Password", Toast.LENGTH_SHORT)
                    .show()
            else {
                val queue = Volley.newRequestQueue(this@LoginActivity)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", edtMobileNumber.text.toString())
                jsonParams.put("password", edtPassword.text.toString())

                if (ConnectionManager().CheckConnectivity(this@LoginActivity)) {
                        val jsonRequest =
                            object : JsonObjectRequest(
                                Method.POST,
                                url,
                                jsonParams,
                                Response.Listener {
                                    try {
                                        //println("Response is $it")
                                        val jsonObject = it.getJSONObject("data")
                                        val success = jsonObject.getBoolean("success")
                                        if (success) {
                                            sharedPreferences.edit().putBoolean("logged_in", true)
                                                .apply()
                                            val data = jsonObject.getJSONObject("data")
                                            sharedPreferences.edit()
                                                .putString("user_id", data.getString("user_id"))
                                                .apply()
                                            sharedPreferences.edit()
                                                .putString("name", data.getString("name")).apply()
                                            sharedPreferences.edit()
                                                .putString("email", data.getString("email")).apply()
                                            sharedPreferences.edit().putString(
                                                "mobile_number",
                                                data.getString("mobile_number")
                                            ).apply()
                                            sharedPreferences.edit()
                                                .putString("address", data.getString("address"))
                                                .apply()

                                            val intent =
                                                Intent(this@LoginActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        } else {
                                            Toast.makeText(
                                                this@LoginActivity,
                                                it.getString("errorMessage"),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            this@LoginActivity,
                                            e.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                Response.ErrorListener {
                                    //println("Error is $it")
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Volley Error $it",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] = "f79d8d4e322024"
                                    return headers
                                }
                            }

                        queue.add(jsonRequest)

                } else {
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { _, _ ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()

                    }
                    dialog.setNegativeButton("Exit") { _, _ ->
                        //closes the app
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }
    }


}
