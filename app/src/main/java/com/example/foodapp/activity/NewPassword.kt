package com.example.foodapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class NewPassword : AppCompatActivity() {

    private lateinit var edtOTP: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPassword: EditText
    private lateinit var btnSubmit: Button
    private var mobileNumber: String? = "9998886666"
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)

        edtOTP = findViewById(R.id.edtOTP)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        progressLayout.visibility = View.GONE


        if (intent != null) {
            mobileNumber = intent.getStringExtra("mobile_number")
        }

        if (mobileNumber == "9998886666")
            finish()

        btnSubmit.setOnClickListener {
            if (edtOTP.text.length < 4)
                Toast.makeText(this@NewPassword, "Invalid OTP", Toast.LENGTH_SHORT).show()
            else {
                if (edtPassword.text.length < 4)
                    Toast.makeText(this@NewPassword, "Invalid Password", Toast.LENGTH_SHORT).show()
                else {
                    if (edtPassword.text.toString()
                            .compareTo(edtConfirmPassword.text.toString()) != 0
                    )
                        Toast.makeText(
                            this@NewPassword,
                            "Passwords don't match",
                            Toast.LENGTH_SHORT
                        ).show()
                    else {
                        val queue = Volley.newRequestQueue(this@NewPassword)
                        val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                        val jsonParams = JSONObject()
                        jsonParams.put("mobile_number", mobileNumber)
                        jsonParams.put("password", edtPassword.text.toString())
                        jsonParams.put("otp", edtOTP.text.toString())

                        if (ConnectionManager().CheckConnectivity(this@NewPassword)) {
                                progressLayout.visibility = View.VISIBLE
                                progressBar.visibility = View.VISIBLE
                                val jsonRequest =
                                    object : JsonObjectRequest(
                                        Method.POST,
                                        url,
                                        jsonParams,
                                        Response.Listener {
                                            try {
                                                println("Response is $it")
                                                val jsonObject = it.getJSONObject("data")
                                                val success = jsonObject.getBoolean("success")
                                                if (success) {
                                                    val successMessage =
                                                        jsonObject.getString("successMessage")
                                                    val dialog =
                                                        AlertDialog.Builder(this@NewPassword)
                                                    dialog.setTitle("Confirmation")
                                                    dialog.setMessage(successMessage)
                                                    dialog.setPositiveButton("OK") { _, _ ->
                                                        finish()
                                                    }
                                                    dialog.create()
                                                    dialog.show()
                                                } else {
                                                    Toast.makeText(
                                                        this@NewPassword,
                                                        jsonObject.getString("errorMessage"),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                progressLayout.visibility = View.GONE
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    this@NewPassword,
                                                    e.message,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        Response.ErrorListener {
                                            println("Error is $it")
                                            Toast.makeText(
                                                this@NewPassword,
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
                            val dialog = AlertDialog.Builder(this@NewPassword)
                            dialog.setTitle("Error")
                            dialog.setMessage("Internet Connection is not Found")
                            dialog.setPositiveButton("Open Settings") { _, _ ->
                                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                                startActivity(settingsIntent)
                                finish()

                            }
                            dialog.setNegativeButton("Exit") { _, _ ->
                                //closes the app
                                ActivityCompat.finishAffinity(this@NewPassword)
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
