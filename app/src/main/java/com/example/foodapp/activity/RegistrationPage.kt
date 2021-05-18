package com.example.foodapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.audiofx.BassBoost
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.util.ConnectionManager
import org.json.JSONObject

class RegistrationPage : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmailAddress: EditText
    private lateinit var edtMobileNumber: EditText
    private lateinit var edtDeliveryAddress: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnRegisterYourself: Button
    private lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        setContentView(R.layout.activity_registration_page)

        edtName = findViewById(R.id.edtName)
        edtEmailAddress = findViewById(R.id.edtEmailAddress)
        edtMobileNumber = findViewById(R.id.edtMobileNumber)
        edtDeliveryAddress = findViewById(R.id.edtDeliveryAddress)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnRegisterYourself = findViewById(R.id.btnRegisterYourself)
        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()

        btnRegisterYourself.setOnClickListener {
            if (edtName.text.length < 3)
                edtName.error = "Invalid Name"
            else {
                if (!Patterns.EMAIL_ADDRESS.matcher(edtEmailAddress.text).matches())
                    edtEmailAddress.error = "Invalid Email"
                else {
                    if (edtMobileNumber.text.length != 10)
                        edtMobileNumber.error = "Invalid Number"
                    else {
                        if (edtDeliveryAddress.text.isNullOrEmpty())
                            edtDeliveryAddress.error = "Invalid Delivery address"
                        else {
                            if (edtPassword.text.length < 4)
                                edtPassword.error =
                                    "Password should be more than or equal to 4 digits"
                            else {
                                if (edtPassword.text.toString()
                                        .compareTo(edtConfirmPassword.text.toString()) != 0
                                ) {
                                    edtPassword.error = "Passwords don't match"
                                    edtConfirmPassword.error = "Passwords don't match"
                                } else {
                                    val queue = Volley.newRequestQueue(this@RegistrationPage)
                                    val url = " http://13.235.250.119/v2/register/fetch_result"
                                    val jsonParams = JSONObject()
                                    jsonParams.put("name", edtName.text.toString())
                                    jsonParams.put("mobile_number", edtMobileNumber.text.toString())
                                    jsonParams.put("password", edtPassword.text.toString())
                                    jsonParams.put("address", edtDeliveryAddress.text.toString())
                                    jsonParams.put("email", edtEmailAddress.text.toString())

                                    if (ConnectionManager().CheckConnectivity(this@RegistrationPage)) {
                                            val jsonRequest =
                                                object : JsonObjectRequest(
                                                    Method.POST,
                                                    url,
                                                    jsonParams,
                                                    Response.Listener {
                                                        try {
                                                            //println("Response is $it")
                                                            val jsonObject =
                                                                it.getJSONObject("data")
                                                            val success =
                                                                jsonObject.getBoolean("success")
                                                            if (success) {
                                                                sharedPreferences.edit()
                                                                    .putBoolean("logged_in", true)
                                                                    .apply()
                                                                val data =
                                                                    jsonObject.getJSONObject("data")
                                                                sharedPreferences.edit().putString(
                                                                    "user_id",
                                                                    data.getString("user_id")
                                                                ).apply()
                                                                sharedPreferences.edit().putString(
                                                                    "name",
                                                                    data.getString("name")
                                                                ).apply()
                                                                sharedPreferences.edit().putString(
                                                                    "email",
                                                                    data.getString("email")
                                                                ).apply()
                                                                sharedPreferences.edit().putString(
                                                                    "mobile_number",
                                                                    data.getString("mobile_number")
                                                                ).apply()
                                                                sharedPreferences.edit().putString(
                                                                    "address",
                                                                    data.getString("address")
                                                                ).apply()

                                                                val intent = Intent(
                                                                    this@RegistrationPage,
                                                                    MainActivity::class.java
                                                                )
                                                                startActivity(intent)
                                                                finish()
                                                            } else {
                                                                Toast.makeText(
                                                                    this@RegistrationPage,
                                                                    it.getString("errorMessage"),
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        } catch (e: Exception) {
                                                            Toast.makeText(
                                                                this@RegistrationPage,
                                                                e.message,
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                        }
                                                    },
                                                    Response.ErrorListener {
                                                        //println("Error is $it")
                                                        Toast.makeText(
                                                            this@RegistrationPage,
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
                                        val dialog = AlertDialog.Builder(this@RegistrationPage)
                                        dialog.setTitle("Error")
                                        dialog.setMessage("Internet Connection is not Found")
                                        dialog.setPositiveButton("Open Settings") { _, _ ->
                                            val settingsIntent =
                                                Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                            startActivity(settingsIntent)
                                            finish()

                                        }
                                        dialog.setNegativeButton("Exit") { _, _ ->
                                            //closes the app
                                            ActivityCompat.finishAffinity(this@RegistrationPage)
                                        }
                                        dialog.create()
                                        dialog.show()
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }


    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home)
            finish()

        return super.onOptionsItemSelected(item)
    }


}


