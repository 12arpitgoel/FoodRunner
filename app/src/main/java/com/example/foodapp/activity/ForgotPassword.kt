package com.example.foodapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodapp.R
import com.example.foodapp.util.ConnectionManager
import org.json.JSONObject

class ForgotPassword : AppCompatActivity() {

    lateinit var edtMobileNumber: EditText
    private lateinit var edtEmailAddress: EditText
    private lateinit var btnNext: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        edtMobileNumber = findViewById(R.id.edtMobileNumber)
        edtEmailAddress = findViewById(R.id.edtEmailAddress)
        btnNext = findViewById(R.id.btnNext)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        progressLayout.visibility = View.GONE

        btnNext.setOnClickListener {
            if (edtMobileNumber.text.length != 10)
                edtMobileNumber.error = "Invalid Number"
            else {
                if (!Patterns.EMAIL_ADDRESS.matcher(edtEmailAddress.text).matches())
                    edtEmailAddress.error = "Invalid Email"
                else {
                    val queue = Volley.newRequestQueue(this@ForgotPassword)
                    val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", edtMobileNumber.text.toString())
                    jsonParams.put("email", edtEmailAddress.text.toString())

                    if (ConnectionManager().CheckConnectivity(this@ForgotPassword)) {
                            progressLayout.visibility = View.VISIBLE
                            progressBar.visibility = View.VISIBLE
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
                                                val firstTry = jsonObject.getBoolean("first_try")
                                                if (firstTry) {
                                                    val dialog =
                                                        AlertDialog.Builder(this@ForgotPassword)
                                                    dialog.setTitle("Information")
                                                    dialog.setMessage("OTP sent to Registered email id")
                                                    dialog.setPositiveButton("OK") { _, _ ->
                                                        val intent =
                                                            Intent(
                                                                this@ForgotPassword,
                                                                NewPassword::class.java
                                                            )
                                                        intent.putExtra(
                                                            "mobile_number",
                                                            edtMobileNumber.text.toString()
                                                        )
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    dialog.create()
                                                    dialog.show()
                                                } else {
                                                    val dialog =
                                                        AlertDialog.Builder(this@ForgotPassword)
                                                    dialog.setTitle("Information")
                                                    dialog.setMessage("Please refer to the previous email for the OTP")
                                                    dialog.setPositiveButton("OK") { _, _ ->
                                                        val intent =
                                                            Intent(
                                                                this@ForgotPassword,
                                                                NewPassword::class.java
                                                            )
                                                        intent.putExtra(
                                                            "mobile_number",
                                                            edtMobileNumber.text.toString()
                                                        )
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                    dialog.create()
                                                    dialog.show()
                                                }
                                                progressLayout.visibility = View.GONE

                                            } else {
                                                Toast.makeText(
                                                    this@ForgotPassword,
                                                    jsonObject.getString("errorMessage"),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                this@ForgotPassword,
                                                e.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    Response.ErrorListener {
                                        //println("Error is $it")
                                        Toast.makeText(
                                            this@ForgotPassword,
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
                        val dialog = AlertDialog.Builder(this@ForgotPassword)
                        dialog.setTitle("Error")
                        dialog.setMessage("Internet Connection is not Found")
                        dialog.setPositiveButton("Open Settings") { _, _ ->
                            val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                            startActivity(settingsIntent)
                            finish()

                        }
                        dialog.setNegativeButton("Exit") { _, _ ->
                            //closes the app
                            ActivityCompat.finishAffinity(this@ForgotPassword)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }
            }
        }
    }
}
